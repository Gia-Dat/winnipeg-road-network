package cli.commands.inserts;
import cli.ParsingHelper;
import cli.commands.Command;
import database_client.DBClient;
import java.sql.SQLException;
import java.util.Scanner;

public class InsertSegment implements Command {
    private final String KEYWORD = "insertSegment";
    private final String DESCRIPTION =
            "Adds a segment to the database for a given road.";

    @Override
    public String getKeyword() {
        return KEYWORD;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public void execute(Scanner in, DBClient db) throws SQLException {
        String roadName = ParsingHelper.getString(
                in, "Enter road name: "
        );
        String roadQualifier = ParsingHelper.getString(
                in, "Enter road qualifier: "
        );

        Double startMeasure, endMeasure;
        do {
            startMeasure = ParsingHelper.getNonNegativeDouble(
                    in, "Enter segment start measure: "
            );
            endMeasure = ParsingHelper.getNonNegativeDouble(
                    in, "Enter segment end measure: "
            );

            if (startMeasure > endMeasure) {
                System.out.println("End measure should be greater than start measure.");
            }
        } while (startMeasure > endMeasure);

        String streetClass = ParsingHelper.getString(
                in, "Enter street class: "
        );
        int numLanes = ParsingHelper.getPositiveInt(
                in, "Enter number of lanes: "
        );
        boolean oneWay = ParsingHelper.getBoolean(
                in, "Enter whether or not this segment is a one-way: "
        );

        try {
            db.insertSegment(roadName, roadQualifier, startMeasure, endMeasure,
                    streetClass, numLanes, oneWay);

            System.out.println("Segment successfully inserted.");
        } catch (SQLException e) {
            if (e.getErrorCode() == DBClient.FOREIGN_KEY_ERROR_CODE) {
                System.out.printf(
                    "Could not create segment because no road exists with " +
                    "name '%s' and qualifier '%s'.\n",
                    roadName, roadQualifier
                );
            } else if (e.getErrorCode() == DBClient.PRIMARY_KEY_ERROR_CODE) {
                System.out.println(
                    "Could not create segment because one already exists with the " +
                    "same identifier."
                );
            } else {
                throw e;
            }
        }
    }
}
