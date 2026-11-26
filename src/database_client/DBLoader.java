package database_client;
import records.RoadInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Executes SELECT queries and constructs records from the result sets.
 */
public class DBLoader {
    private final Connection db;

    public DBLoader(Connection db) {
        this.db = db;
    }

    public ArrayList<RoadInfo> getRoadInfo(
        String roadName
    ) throws SQLException {
        final String query = """
            SELECT DISTINCT TOP 30
                segment.roadName AS roadName,
                segment.roadQualifier AS roadQualifier,
                speedLimit.maxKmPerHour,
                segment.numLanes,
                segment.streetClass,
                segment.oneway,
                IIF(segment.startMeasure > speedLimit.startMeasure,
                    segment.startMeasure, segment.endMeasure) AS startMeasure, -- i.e. GREATEST()
                IIF(segment.endMeasure > speedLimit.endMeasure,
                    segment.endMeasure, speedLimit.endMeasure) AS endMeasure -- i.e. LEAST()
            FROM segment JOIN speedLimit ON
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
            	LEFT JOIN segment ON\s
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

        if(!results.next()) {
            return 0.00;
        }

        return results.getDouble("totalPercentage");
    }
}
