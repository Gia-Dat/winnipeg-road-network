package database_client;
import records.RoadInfo;
import java.sql.*;
import java.util.ArrayList;

/**
 * Executes SELECT queries and constructs records from the result sets.
 */
public class DBClient {
    private final Connection db;

    public static final int FOREIGN_KEY_ERROR_CODE = 547;
    public static final int PRIMARY_KEY_ERROR_CODE = 2627;

    public DBClient(Connection db) {
        this.db = db;
    }

    /**
     * Inserts a road in the database.
     */
    public void insertRoad(
        String name, String qualifier, Double startMeasure, Double endMeasure
    ) throws SQLException {
        final String query = """
            INSERT INTO road
                (name, qualifier, startMeasure, endMeasure)
            VALUES (?, ?, ?, ?)
        """;

        PreparedStatement statement = this.db.prepareStatement(query);
        statement.setString(1, name);
        statement.setString(2, qualifier);
        statement.setDouble(3, startMeasure);
        statement.setDouble(4, endMeasure);

        statement.execute();
    }

    /**
     * Inserts a segment in the database for a given road.
     */
    public void insertSegment(
        String roadName, String roadQualifier, Double startMeasure,
        Double endMeasure, String streetClass, int numLanes, boolean oneWay
    ) throws SQLException {
        final String query = """
            INSERT INTO segment
                (roadName, roadQualifier, startMeasure, endMeasure, streetClass,
                numLanes, oneWay)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        PreparedStatement statement = this.db.prepareStatement(query);
        statement.setString(1, roadName);
        statement.setString(2, roadQualifier);
        statement.setDouble(3, startMeasure);
        statement.setDouble(4, endMeasure);
        statement.setString(5, streetClass);
        statement.setInt(6, numLanes);
        statement.setBoolean(7, oneWay);

        statement.execute();
    }

    /**
     * Inserts a speed limit into the database for a given road segment.
     */
    public void insertSpeedLimit(
        String roadName, String roadQualifier, Double startMeasure,
        Double endMeasure, int maxKmPerHour
    ) throws SQLException {
        final String query = """
            INSERT INTO speedLimit
                (roadName, roadQualifier, startMeasure, endMeasure, maxKmPerHour)
            VALUES
                (?, ?, ?, ?, ?)
        """;

        PreparedStatement statement = this.db.prepareStatement(query);
        statement.setString(1, roadName);
        statement.setString(2, roadQualifier);
        statement.setDouble(3, startMeasure);
        statement.setDouble(4, endMeasure);
        statement.setInt(5, maxKmPerHour);

        statement.execute();
    }

    /**
     * Inserts a school zone into the database for a given speed limit.
     */
    public void insertSchoolZone(
        String roadName, String roadQualifier, Double startMeasure,
        String schoolName, String effectiveDaysDesc, String effectiveTimesDesc
    ) throws SQLException {
        final String query = """
            INSERT INTO schoolZone
                (roadName, roadQualifier, startMeasure, schoolName, 
                 effectiveDaysDesc, effectiveTimesDesc)
            VALUES
                (?, ?, ?, ?, ?, ?)
        """;

        PreparedStatement statement = this.db.prepareStatement(query);
        statement.setString(1, roadName);
        statement.setString(2, roadQualifier);
        statement.setDouble(3, startMeasure);
        statement.setString(4, schoolName);
        statement.setString(5, effectiveDaysDesc);
        statement.setString(6, effectiveTimesDesc);

        statement.execute();
    }

    /**
     * Inserts a plow zone into the database.
     */
    public void insertPlowZone(
        String zoneID, String name
    ) throws  SQLException {
        final String query = """
            INSERT INTO plowZone
                (zoneID, name)
            VALUES
                (?, ?)
        """;

        PreparedStatement statement = this.db.prepareStatement(query);
        statement.setString(1, zoneID);
        statement.setString(2, name);

        statement.execute();
    }

    /**
     * Inserts a school division into the database.
     */
    public void insertSchoolDivision(
        String name, String websiteUrl
    ) throws SQLException {
        final String query = """
            INSERT INTO schoolDivision
                (name, websiteUrl)
            VALUES
                (?, ?)
        """;

        PreparedStatement statement = this.db.prepareStatement(query);
        statement.setString(1, name);
        statement.setString(2, websiteUrl);

        statement.execute();
    }

    /**
     * Inserts a school division ward into the database for a given school
     * division.
     */
    public void insertSchoolDivisionWard(
        String divisionName, int wardID
    ) throws SQLException {
        final String query = """
            INSERT INTO schoolDivisionWard
                (schoolDivisionName, wardID)
            VALUES
                (?, ?)
        """;

        PreparedStatement statement = this.db.prepareStatement(query);
        statement.setString(1, divisionName);
        statement.setInt(2, wardID);

        statement.execute();
    }

    /**
     * Inserts a neighbourhood into the database.
     */
    public void insertNeighbourhood(
        String neighbourhoodName
    ) throws SQLException {
        final String query = """
            INSERT INTO neighbourhood
                (name)
            VALUES
                (?)
        """;

        PreparedStatement statement = this.db.prepareStatement(query);
        statement.setString(1, neighbourhoodName);

        statement.execute();
    }

    /**
     * Inserts a neighbourhood ward into the database for a given neighbourhood.
     */
    public void insertNeighbourhoodWard(
        String neighbourhoodName, int wardID
    ) throws SQLException {
        final String query = """
            INSERT INTO neighbourhoodWard
                (neighbourhoodName, wardID)
            VALUES
                (?, ?)
        """;

        PreparedStatement statement = this.db.prepareStatement(query);
        statement.setString(1, neighbourhoodName);
        statement.setInt(2, wardID);

        statement.execute();
    }

    /**
     * Inserts an address into the database for a given road, neighbourhood
     * ward, school division ward, and plow zone.
     */
    public void insertAddress(
        int streetNumber, Double latitude, Double longitude,
        String roadName, String roadQualifier, String neighbourhood,
        int neighbourhoodWard, String schoolDivision, int schoolDivisionWard,
        String plowZone
    ) throws SQLException {
        final String query = """
            INSERT INTO address
                (streetNumber, latitude, longitude, roadName, roadQualifier,
                 neighbourhood, neighbourhoodWard, schoolDivision, schoolDivisionWard,
                 plowZone)
            VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        PreparedStatement statement = this.db.prepareStatement(query);
        statement.setInt(1, streetNumber);
        statement.setDouble(2, latitude);
        statement.setDouble(3, longitude);
        statement.setString(4, roadName);
        statement.setString(5, roadQualifier);
        statement.setString(6, neighbourhood);
        statement.setInt(7, neighbourhoodWard);
        statement.setString(8, schoolDivision);
        statement.setInt(9, schoolDivisionWard);
        statement.setString(10, plowZone);

        statement.execute();
    }

    /**
     * Deletes all data from all tables.
     */
    public void deleteAll() throws SQLException {
        final String query = """
            DELETE FROM address;
            DELETE FROM schoolZone;
            DELETE FROM speedLimitPoint;
            DELETE FROM speedLimit;
            DELETE FROM segmentPoint;
            DELETE FROM segment;
            DELETE FROM roadPoint;
            DELETE FROM road;
            DELETE FROM plowZonePoint;
            DELETE FROM plowZone;
            DELETE FROM schoolDivisionPoint;
            DELETE FROM schoolDivisionWard;
            DELETE FROM schoolDivision;
            DELETE FROM neighbourhoodPoint;
            DELETE FROM neighbourhoodWard;
            DELETE FROM neighbourhood;
        """;

        Statement statement = this.db.createStatement();

        statement.execute(query);
    }

    public ArrayList<RoadInfo> getRoadInfo(
        String roadName
    ) throws SQLException {
        final String query = """
            SELECT DISTINCT TOP 30
                segment.roadName,
                segment.roadQualifier,
                speedLimit.maxKmPerHour,
                segment.numLanes,
                segment.streetClass,
                segment.oneway,
                IIF(segment.startMeasure > speedLimit.startMeasure OR speedLimit.startMeasure IS NULL,
                    segment.startMeasure, speedLimit.startMeasure) AS startMeasure, -- i.e. GREATEST()
                IIF(segment.endMeasure > speedLimit.endMeasure OR speedLimit.endMeasure IS NULL,
                    segment.endMeasure, speedLimit.endMeasure) AS endMeasure -- i.e. LEAST()
            FROM road LEFT JOIN segment ON
                road.name = segment.roadName AND
                road.qualifier = segment.roadQualifier
            LEFT JOIN speedLimit ON
                segment.roadName = speedLimit.roadName AND
                segment.roadQualifier = speedLimit.roadQualifier AND
                segment.startMeasure < speedLimit.endMeasure AND
                speedLimit.startMeasure < segment.endMeasure
            WHERE segment.roadName = ?
            ORDER BY startMeasure
        """;

        PreparedStatement statement = this.db.prepareStatement(query);
        statement.setString(1, roadName);

        ResultSet results = statement.executeQuery();

        ArrayList<RoadInfo> roadInfos = new ArrayList<>();
        while (results.next()) {
            roadInfos.add(new RoadInfo(
                    results.getString("roadName"),
                    results.getString("roadQualifier"),
                    results.getInt("maxKmPerHour"),
                    results.getInt("numLanes"),
                    results.getString("streetClass"),
                    results.getBoolean("oneWay"),
                    results.getDouble("startMeasure"),
                    results.getDouble("endMeasure")
            ));
        }

        return roadInfos;
    }

    /***
     * Returns the percentage of the given road which contains the given number
     * of lanes.
     */
    public double getLaneCountPercentage(
        String roadName, String roadQualifier, int numLanes
    ) throws SQLException {
        final String query = """
            WITH segmentPercentage AS (
            	SELECT
            		road.name,
            		road.qualifier,
            		segment.startMeasure,\s
            		segment.endMeasure,
            		segment.numLanes,
            		100.00 * (segment.endMeasure - segment.startMeasure)\s
            		/ (road.endMeasure - road.startMeasure) AS percentageOfRoad
            	FROM road
            	LEFT JOIN segment ON
            		road.name = segment.roadName AND
            		road.qualifier = segment.roadQualifier
            )
            SELECT SUM(percentageOfRoad) AS totalPercentage
            FROM segmentPercentage
            WHERE\s
            	name = ? AND
            	qualifier = ? AND 
            	numLanes = ?;
        """;

        PreparedStatement statement = this.db.prepareStatement(query);
        statement.setString(1, roadName);
        statement.setString(2, roadQualifier);
        statement.setInt(3, numLanes);

        ResultSet results = statement.executeQuery();

        if (!results.next()) {
            return 0.00;
        }

        return results.getDouble("totalPercentage");
    }
}
