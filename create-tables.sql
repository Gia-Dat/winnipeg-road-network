USE prejetm;

DROP TABLE IF EXISTS road;
DROP TABLE IF EXISTS roadPoint;
DROP TABLE IF EXISTS segment;
DROP TABLE IF EXISTS segmentPoint;
DROP TABLE IF EXISTS speedLimit;
DROP TABLE IF EXISTS speedLimitPoint;
DROP TABLE IF EXISTS schoolZone;
DROP TABLE IF EXISTS plowZone;
DROP TABLE IF EXISTS plowZonePoint;
DROP TABLE IF EXISTS schoolDivision;
DROP TABLE IF EXISTS schoolDivisionWard;
DROP TABLE IF EXISTS schoolDivisionPoint;
DROP TABLE IF EXISTS neighbourhood;
DROP TABLE IF EXISTS neighbourhoodWard;
DROP TABLE IF EXISTS neighbourhoodPoint;
DROP TABLE IF EXISTS address;

CREATE TABLE road(
    name VARCHAR(255) NOT NULL,
    qualifier VARCHAR(255) NOT NULL,
    startMeasure DECIMAL(16, 12) NOT NULL,
    endMeasure DECIMAL(16, 12) NOT NULL,

    CHECK(startMeasure >= 0),
    CHECK(endMeasure >= 0),
    CHECK(startMeasure <= endMeasure)

    PRIMARY KEY(name, qualifier)
);

CREATE TABLE roadPoint(
    roadName VARCHAR(255) NOT NULL,
    roadQualifier VARCHAR(255) NOT NULL,
    pointIndex INTEGER NOT NULL,
    latitude REAL NOT NULL,
    longitude REAL NOT NULL,

    CHECK(pointIndex >= 0),
    CHECK(latitude BETWEEN -90 AND 90),
    CHECK(longitude BETWEEN -180 AND 180),

    PRIMARY KEY(roadName, roadQualifier, pointIndex),

    FOREIGN KEY(roadName, roadQualifier)
        REFERENCES road(name, qualifier)
        ON DELETE CASCADE
);

CREATE TABLE segment(
    roadName VARCHAR(255) NOT NULL,
    roadQualifier VARCHAR(255) NOT NULL,
    startMeasure DECIMAL(16, 12) NOT NULL,
    endMeasure DECIMAL(16, 12) NOT NULL,
    streetClass VARCHAR(255) NOT NULL,
    numLanes INTEGER NOT NULL,
    oneWay BIT NOT NULL,

    CHECK(startMeasure >= 0),
    CHECK(endMeasure >= 0),
    CHECK(startMeasure <= endMeasure),
    CHECK(numLanes >= 1),

    PRIMARY KEY(roadName, roadQualifier, startMeasure),

    FOREIGN KEY(roadName, roadQualifier)
        REFERENCES road(name, qualifier)
        ON DELETE CASCADE
);

CREATE TABLE segmentPoint(
    roadName VARCHAR(255) NOT NULL,
    roadQualifier VARCHAR(255) NOT NULL,
    segmentStartMeasure DECIMAL(16, 12) NOT NULL,
    pointIndex INTEGER NOT NULL,
    latitude REAL NOT NULL,
    longitude REAL NOT NULL,

    CHECK(pointIndex >= 0),
    CHECK(latitude BETWEEN -90 AND 90),
    CHECK(longitude BETWEEN -180 AND 180),

    PRIMARY KEY(roadName, roadQualifier, segmentStartMeasure, pointIndex),

    FOREIGN KEY(roadName, roadQualifier, segmentStartMeasure)
        REFERENCES segment(roadName, roadQualifier, startMeasure)
        ON DELETE CASCADE
);

CREATE TABLE speedLimit(
    roadName VARCHAR(255) NOT NULL,
    roadQualifier VARCHAR(255) NOT NULL,
    startMeasure DECIMAL(16, 12) NOT NULL,
    endMeasure DECIMAL(16, 12) NOT NULL,
    maxKmPerHour INTEGER NOT NULL,

    CHECK(startMeasure >= 0),
    CHECK(endMeasure >= 0),
    CHECK(startMeasure <= endMeasure),
    CHECK(maxKmPerHour >= 1),

    PRIMARY KEY(roadName, roadQualifier, startMeasure),

    FOREIGN KEY(roadName, roadQualifier)
        REFERENCES road(name, qualifier)
        ON DELETE CASCADE
);

CREATE TABLE speedLimitPoint(
    roadName VARCHAR(255) NOT NULL,
    roadQualifier VARCHAR(255) NOT NULL,
    speedLimitStartMeasure DECIMAL(16, 12) NOT NULL,
    pointIndex INTEGER NOT NULL,
    latitude REAL NOT NULL,
    longitude REAL NOT NULL,

    CHECK(pointIndex >= 0),
    CHECK(latitude BETWEEN -90 AND 90),
    CHECK(longitude BETWEEN -180 AND 180),

    PRIMARY KEY(roadName, roadQualifier, speedLimitStartMeasure, pointIndex),

    FOREIGN KEY(roadName, roadQualifier, speedLimitStartMeasure)
        REFERENCES speedLimit(roadName, roadQualifier, startMeasure)
        ON DELETE CASCADE
);

CREATE TABLE schoolZone(
    roadName VARCHAR(255) NOT NULL,
    roadQualifier VARCHAR(255) NOT NULL,
    startMeasure DECIMAL(16, 12) NOT NULL,
    schoolName VARCHAR(255) NOT NULL,
    effectiveDaysDesc VARCHAR(255) NOT NULL,
    effectiveTimesDesc VARCHAR(255) NOT NULL,

    PRIMARY KEY(roadName, roadQualifier, startMeasure),

    FOREIGN KEY(roadName, roadQualifier, startMeasure)
        REFERENCES speedLimit(roadName, roadQualifier, startMeasure)
        ON DELETE CASCADE
);

CREATE TABLE plowZone(
    zoneID VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,

    PRIMARY KEY(zoneID)
);

CREATE TABLE plowZonePoint(
    zoneID VARCHAR(255) NOT NULL,
    pointIndex INTEGER NOT NULL,
    latitude REAL NOT NULL,
    longitude REAL NOT NULL,

    CHECK(pointIndex >= 0),
    CHECK(latitude BETWEEN -90 AND 90),
    CHECK(longitude BETWEEN -180 AND 180),

    PRIMARY KEY(zoneID, pointIndex),

    FOREIGN KEY(zoneID)
        REFERENCES plowZone(zoneID)
        ON DELETE CASCADE
);

CREATE TABLE schoolDivision(
    name VARCHAR(255) NOT NULL,
    websiteUrl VARCHAR(255) NOT NULL,

    PRIMARY KEY(name)
);

CREATE TABLE schoolDivisionWard(
    schoolDivisionName VARCHAR(255) NOT NULL,
    wardID INTEGER NOT NULL,

    PRIMARY KEY(schoolDivisionName, wardID),

    FOREIGN KEY(schoolDivisionName)
        REFERENCES schoolDivision(name)
        ON DELETE CASCADE
);

CREATE TABLE schoolDivisionPoint(
    schoolDivisionName VARCHAR(255) NOT NULL,
    pointIndex INTEGER NOT NULL,
    latitude REAL NOT NULL,
    longitude REAL NOT NULL,

    CHECK(pointIndex >= 0),
    CHECK(latitude BETWEEN -90 AND 90),
    CHECK(longitude BETWEEN -180 AND 180),

    PRIMARY KEY(schoolDivisionName, pointIndex),

    FOREIGN KEY(schoolDivisionName)
        REFERENCES schoolDivision(name)
        ON DELETE CASCADE
);

CREATE TABLE neighbourhood(
    name VARCHAR(255) NOT NULL,

    PRIMARY KEY(name)
);

CREATE TABLE neighbourhoodWard(
    neighbourhoodName VARCHAR(255) NOT NULL,
    wardID INTEGER NOT NULL,

    PRIMARY KEY(neighbourhoodName, wardID),

    FOREIGN KEY(neighbourhoodName)
        REFERENCES neighbourhood(name)
        ON DELETE CASCADE
);

CREATE TABLE neighbourhoodPoint(
    neighbourhoodName VARCHAR(255) NOT NULL,
    pointIndex INTEGER NOT NULL,
    latitude REAL NOT NULL,
    longitude REAL NOT NULL,

    CHECK(pointIndex >= 0),
    CHECK(latitude BETWEEN -90 AND 90),
    CHECK(longitude BETWEEN -180 AND 180),

    PRIMARY KEY(neighbourhoodName, pointIndex),

    FOREIGN KEY(neighbourhoodName)
        REFERENCES neighbourhood(name)
        ON DELETE CASCADE
);

CREATE TABLE address(
    addressID INTEGER NOT NULL IDENTITY(1, 1),
    streetNumber INTEGER NOT NULL,
    latitude REAL NOT NULL,
    longitude REAL NOT NULL,
    roadName VARCHAR(255) NOT NULL,
    roadQualifier VARCHAR(255) NOT NULL,
    neighbourhood VARCHAR(255) NOT NULL,
    neighbourhoodWard INTEGER NOT NULL,
    schoolDivision VARCHAR(255) NOT NULL,
    schoolDivisionWard INTEGER NOT NULL,
    plowZone VARCHAR(255),

    CHECK(streetNumber >= 1),
    CHECK(latitude BETWEEN -90 AND 90),
    CHECK(longitude BETWEEN -180 AND 180),

    PRIMARY KEY(addressID),

    FOREIGN KEY(roadName, roadQualifier)
        REFERENCES road(name, qualifier)
        ON DELETE CASCADE,
    FOREIGN KEY(neighbourhood, neighbourhoodWard)
        REFERENCES neighbourhoodWard(neighbourhoodName, wardID)
        ON DELETE CASCADE,
    FOREIGN KEY(schoolDivision, schoolDivisionWard)
        REFERENCES schoolDivisionWard(schoolDivisionName, wardID)
        ON DELETE CASCADE,
    FOREIGN KEY(plowZone)
        REFERENCES plowZone(zoneID)
        ON DELETE SET NULL
);