import configparser
import re
import pandas as pd
import pyodbc

DATA_FOLDER = "cleaned-data"
DDL_FILE = "create-tables.sql"
BATCH_SIZE = 5000


def connect_db():
    # Read username and password from auth.cfg
    config = {}
    try:
        with open("auth.cfg", "r", encoding="utf-8") as f:
            for line in f:
                line = line.strip()
                if "=" in line and not line.startswith("#"):
                    key, value = line.split("=", 1)
                    config[key.strip()] = value.strip()
    except FileNotFoundError:
        print("Could not find auth.cfg")
        sys.exit(1)

    username = config.get("username")
    password = config.get("password")

    if not username or not password:
        print("Username or password not provided.")
        sys.exit(1)

    conn_str = (
        "DRIVER={SQL Server Native Client 11.0};"
        "SERVER=uranium.cs.umanitoba.ca,1433;"
        "DATABASE=cs3380;"
        f"UID={username};"
        f"PWD={password};"
    )
    return pyodbc.connect(conn_str, timeout=30, autocommit=False)


def get_points(wkt_text):
    # Extract (lon, lat) pairs from WKT string and return them as (lat, lon)
    if not isinstance(wkt_text, str):
        return []
    matches = re.findall(r"(-?\d+\.\d+)\s+(-?\d+\.\d+)", wkt_text)
    points = []
    for lon, lat in matches:
        lat_val = float(lat)
        lon_val = float(lon)
        if -90 <= lat_val <= 90 and -180 <= lon_val <= 180:
            points.append((lat_val, lon_val))
    return points


def insert_rows(cursor, conn, table_name, sql, rows):
    # Insert data in batches so it runs faster on Uranium
    if len(rows) == 0:
        print(f"{table_name}: 0 rows")
        return

    cursor.fast_executemany = True
    for i in range(0, len(rows), BATCH_SIZE):
        batch = rows[i: i + BATCH_SIZE]
        cursor.executemany(sql, batch)
        conn.commit()
    print(f"{table_name}: inserted {len(rows)} rows")


def create_tables(cursor, conn):
    # Run the SQL file to reset and create all tables
    with open(DDL_FILE, "r", encoding="utf-8") as f:
        sql_script = f.read()

    for statement in sql_script.split(";"):
        stmt = statement.strip()
        if stmt == "" or stmt.upper().startswith("USE "):
            continue
        cursor.execute(stmt)
    conn.commit()
    print("Tables created.")


def main():
    conn = connect_db()
    cursor = conn.cursor()

    try:
        create_tables(cursor, conn)

        # Road and RoadPoint
        df = pd.read_csv(f"{DATA_FOLDER}/road_segment.csv")
        df = df.dropna(
            subset=["street_name", "street_qualifier", "begin_measure", "end_measure"])
        df = df[(df["begin_measure"] >= 0) & (
            df["end_measure"] >= df["begin_measure"])]

        # Group by street name and qualifier to get total road range
        road_df = df.groupby(["street_name", "street_qualifier"], as_index=False).agg(
            startMeasure=("begin_measure", "min"),
            endMeasure=("end_measure", "max"),
        )

        road_rows = []
        valid_roads = set()
        road_lookup = {}

        for row in road_df.itertuples(index=False):
            name = str(row.street_name).strip()
            qual = str(row.street_qualifier).strip()
            road_rows.append((name, qual, round(
                float(row.startMeasure), 6), round(float(row.endMeasure), 6)))
            valid_roads.add((name, qual))

            # Save uppercase name -> (name, qualifier) to match with addresses later
            key = name.upper()
            if key not in road_lookup or qual == "A":
                road_lookup[key] = (name, qual)

        insert_rows(
            cursor, conn, "road",
            "INSERT INTO road (name, qualifier, startMeasure, endMeasure) VALUES (?, ?, ?, ?)",
            road_rows,
        )

        road_point_rows = []
        for (name, qual), group in df.groupby(["street_name", "street_qualifier"]):
            if (name, qual) not in valid_roads:
                continue
            idx = 0
            for loc in group["location"]:
                for lat, lon in get_points(loc):
                    road_point_rows.append((name, qual, idx, lat, lon))
                    idx += 1

        insert_rows(
            cursor, conn, "roadPoint",
            "INSERT INTO roadPoint (roadName, roadQualifier, pointIndex, latitude, longitude) VALUES (?, ?, ?, ?, ?)",
            road_point_rows,
        )

        # Segment and SegmentPoint
        df = pd.read_csv(f"{DATA_FOLDER}/road_block.csv")
        df = df.dropna(subset=["street_name", "street_qualifier", "begin_measure",
                       "end_measure", "street_class", "number_of_lanes", "oneway"])
        df["begin_measure"] = df["begin_measure"].round(6)
        df["end_measure"] = df["end_measure"].round(6)
        df = df[(df["begin_measure"] >= 0) & (df["end_measure"] >=
                                              df["begin_measure"]) & (df["number_of_lanes"] >= 1)]
        df = df.drop_duplicates(
            subset=["street_name", "street_qualifier", "begin_measure"])

        seg_rows = []
        seg_point_rows = []
        for row in df.itertuples(index=False):
            if (row.street_name, row.street_qualifier) not in valid_roads:
                continue
            one_way = 1 if int(row.oneway) != 0 else 0
            seg_rows.append((
                row.street_name,
                row.street_qualifier,
                float(row.begin_measure),
                float(row.end_measure),
                str(row.street_class),
                int(row.number_of_lanes),
                one_way,
            ))
            points = get_points(row.location)
            for idx, (lat, lon) in enumerate(points):
                seg_point_rows.append((row.street_name, row.street_qualifier, float(
                    row.begin_measure), idx, lat, lon))

        insert_rows(
            cursor, conn, "segment",
            "INSERT INTO segment (roadName, roadQualifier, startMeasure, endMeasure, streetClass, numLanes, oneWay) VALUES (?, ?, ?, ?, ?, ?, ?)",
            seg_rows,
        )
        insert_rows(
            cursor, conn, "segmentPoint",
            "INSERT INTO segmentPoint (roadName, roadQualifier, segmentStartMeasure, pointIndex, latitude, longitude) VALUES (?, ?, ?, ?, ?, ?)",
            seg_point_rows,
        )

        # SpeedLimit, SpeedLimitPoint, and SchoolZone
        df_sl = pd.read_csv(f"{DATA_FOLDER}/speed_limit.csv")
        df_sl = df_sl.dropna(subset=[
                             "street_name", "street_qualifier", "begin_measure", "end_measure", "speed_limit"])
        df_sl["begin_measure"] = df_sl["begin_measure"].round(6)
        df_sl["end_measure"] = df_sl["end_measure"].round(6)
        df_sl = df_sl[(df_sl["begin_measure"] >= 0) & (
            df_sl["end_measure"] >= df_sl["begin_measure"]) & (df_sl["speed_limit"] >= 1)]
        df_sl = df_sl.drop_duplicates(
            subset=["street_name", "street_qualifier", "begin_measure"])

        sl_map = {}
        sl_point_rows = []
        for row in df_sl.itertuples(index=False):
            if (row.street_name, row.street_qualifier) not in valid_roads:
                continue
            key = (row.street_name, row.street_qualifier,
                   float(row.begin_measure))
            sl_map[key] = (row.street_name, row.street_qualifier, float(
                row.begin_measure), float(row.end_measure), int(row.speed_limit))
            for idx, (lat, lon) in enumerate(get_points(row.location)):
                sl_point_rows.append((row.street_name, row.street_qualifier, float(
                    row.begin_measure), idx, lat, lon))

        # Add any missing school zones into speedLimit first so FK doesn't fail
        df_sz = pd.read_csv(f"{DATA_FOLDER}/school_zone.csv")
        df_sz = df_sz.dropna(subset=["street_name", "street_qualifier",
                             "begin_measure", "end_measure", "school", "speed_limit"])
        df_sz["begin_measure"] = df_sz["begin_measure"].round(6)
        df_sz["end_measure"] = df_sz["end_measure"].round(6)
        df_sz = df_sz[(df_sz["begin_measure"] >= 0) & (
            df_sz["end_measure"] >= df_sz["begin_measure"]) & (df_sz["speed_limit"] >= 1)]
        df_sz = df_sz.drop_duplicates(
            subset=["street_name", "street_qualifier", "begin_measure"])

        sz_rows = []
        for row in df_sz.itertuples(index=False):
            if (row.street_name, row.street_qualifier) not in valid_roads:
                continue
            key = (row.street_name, row.street_qualifier,
                   float(row.begin_measure))
            if key not in sl_map:
                sl_map[key] = (row.street_name, row.street_qualifier, float(
                    row.begin_measure), float(row.end_measure), int(row.speed_limit))
                for idx, (lat, lon) in enumerate(get_points(row.location)):
                    sl_point_rows.append((row.street_name, row.street_qualifier, float(
                        row.begin_measure), idx, lat, lon))
            sz_rows.append((
                row.street_name,
                row.street_qualifier,
                float(row.begin_measure),
                str(row.school),
                str(row.effective_days),
                str(row.effective_time),
            ))

        insert_rows(
            cursor, conn, "speedLimit",
            "INSERT INTO speedLimit (roadName, roadQualifier, startMeasure, endMeasure, maxKmPerHour) VALUES (?, ?, ?, ?, ?)",
            list(sl_map.values()),
        )
        insert_rows(
            cursor, conn, "speedLimitPoint",
            "INSERT INTO speedLimitPoint (roadName, roadQualifier, speedLimitStartMeasure, pointIndex, latitude, longitude) VALUES (?, ?, ?, ?, ?, ?)",
            sl_point_rows,
        )
        insert_rows(
            cursor, conn, "schoolZone",
            "INSERT INTO schoolZone (roadName, roadQualifier, startMeasure, schoolName, effectiveDaysDesc, effectiveTimesDesc) VALUES (?, ?, ?, ?, ?, ?)",
            sz_rows,
        )

        # PlowZone and PlowZonePoint
        df = pd.read_csv(
            f"{DATA_FOLDER}/plow_zone.csv").dropna(subset=["plow_zone"])
        pz_rows = []
        valid_pz = set()
        for zone in sorted(df["plow_zone"].astype(str).str.strip().unique()):
            pz_rows.append((zone, f"Zone {zone}"))
            valid_pz.add(zone)

        pz_point_rows = []
        for zone, group in df.groupby("plow_zone"):
            z = str(zone).strip()
            idx = 0
            for loc in group["location"]:
                for lat, lon in get_points(loc):
                    pz_point_rows.append((z, idx, lat, lon))
                    idx += 1

        insert_rows(cursor, conn, "plowZone",
                    "INSERT INTO plowZone (zoneID, name) VALUES (?, ?)", pz_rows)
        insert_rows(
            cursor, conn, "plowZonePoint",
            "INSERT INTO plowZonePoint (zoneID, pointIndex, latitude, longitude) VALUES (?, ?, ?, ?)",
            pz_point_rows,
        )

        # SchoolDivision and SchoolDivisionPoint
        df = pd.read_csv(
            f"{DATA_FOLDER}/school_division.csv").dropna(subset=["name", "website"])
        sd_rows = []
        sd_point_rows = []
        valid_sd = set()
        for row in df.itertuples(index=False):
            name = str(row.name).strip()
            sd_rows.append((name, str(row.website).strip()))
            valid_sd.add(name)
            for idx, (lat, lon) in enumerate(get_points(row.location)):
                sd_point_rows.append((name, idx, lat, lon))

        insert_rows(cursor, conn, "schoolDivision",
                    "INSERT INTO schoolDivision (name, websiteUrl) VALUES (?, ?)", sd_rows)
        insert_rows(
            cursor, conn, "schoolDivisionPoint",
            "INSERT INTO schoolDivisionPoint (schoolDivisionName, pointIndex, latitude, longitude) VALUES (?, ?, ?, ?)",
            sd_point_rows,
        )

        # Neighbourhood and NeighbourhoodPoint
        df = pd.read_csv(
            f"{DATA_FOLDER}/neighborhood.csv").dropna(subset=["name"])
        nb_rows = []
        nb_point_rows = []
        valid_nb = set()
        for row in df.itertuples(index=False):
            name = str(row.name).strip()
            nb_rows.append((name,))
            valid_nb.add(name)
            for idx, (lat, lon) in enumerate(get_points(row.location)):
                nb_point_rows.append((name, idx, lat, lon))

        insert_rows(cursor, conn, "neighbourhood",
                    "INSERT INTO neighbourhood (name) VALUES (?)", nb_rows)
        insert_rows(
            cursor, conn, "neighbourhoodPoint",
            "INSERT INTO neighbourhoodPoint (neighbourhoodName, pointIndex, latitude, longitude) VALUES (?, ?, ?, ?)",
            nb_point_rows,
        )

        # Wards and Address
        df = pd.read_csv(f"{DATA_FOLDER}/addresses.csv")
        df = df.dropna(subset=["street_number", "street_name", "neighbourhood", "ward",
                       "school_division", "school_division_ward", "latitude", "longitude"])

        # Convert ward names to integer IDs
        ward_names = sorted(df["ward"].astype(str).str.strip().unique())
        ward_to_id = {}
        for i, w in enumerate(ward_names):
            ward_to_id[w] = i + 1

        nb_wards = set()
        sd_wards = set()
        addr_rows = []
        skipped = 0

        for row in df.itertuples(index=False):
            st_num = int(row.street_number)
            nb = str(row.neighbourhood).strip()
            sd = str(row.school_division).strip()

            if st_num < 1 or nb not in valid_nb or sd not in valid_sd:
                skipped += 1
                continue

            # Match street name + street type against the road table
            st_name = str(row.street_name).strip().upper()
            st_type = str(row.street_type).strip().upper(
            ) if pd.notna(row.street_type) else ""
            full_street = f"{st_name} {st_type}".strip()

            road_match = road_lookup.get(full_street)
            if road_match is None:
                road_match = road_lookup.get(st_name)
            if road_match is None:
                skipped += 1
                continue

            try:
                sd_ward = int(float(row.school_division_ward))
            except (ValueError, TypeError):
                skipped += 1
                continue

            nb_ward = ward_to_id[str(row.ward).strip()]
            pz = str(row.plow_zone).strip() if pd.notna(
                row.plow_zone) else None
            if pz not in valid_pz:
                pz = None

            nb_wards.add((nb, nb_ward))
            sd_wards.add((sd, sd_ward))
            addr_rows.append((
                st_num,
                float(row.latitude),
                float(row.longitude),
                road_match[0],
                road_match[1],
                nb,
                nb_ward,
                sd,
                sd_ward,
                pz,
            ))

        insert_rows(
            cursor, conn, "neighbourhoodWard",
            "INSERT INTO neighbourhoodWard (neighbourhoodName, wardID) VALUES (?, ?)",
            sorted(nb_wards),
        )
        insert_rows(
            cursor, conn, "schoolDivisionWard",
            "INSERT INTO schoolDivisionWard (schoolDivisionName, wardID) VALUES (?, ?)",
            sorted(sd_wards),
        )
        insert_rows(
            cursor, conn, "address",
            "INSERT INTO address (streetNumber, latitude, longitude, roadName, roadQualifier, neighbourhood, neighbourhoodWard, schoolDivision, schoolDivisionWard, plowZone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            addr_rows,
        )

        print(f"Skipped {skipped} address rows.")

    finally:
        cursor.close()
        conn.close()


if __name__ == "__main__":
    main()
