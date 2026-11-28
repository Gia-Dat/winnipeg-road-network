package cli.commands.inserts;
import cli.ParsingHelper;
import cli.commands.Command;
import database_client.DBClient;
import java.sql.SQLException;
import java.util.Scanner;

public class InsertSpeedLimit implements Command {
    private static final String KEYWORD = "insertSpeedLimit";
    private static final String DESCRIPTION =
            "Adds a speed limit to the database for a given road segment.";

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
        String roadName = ParsingHelper.getString(in, "Enter road name: ");
        String roadQualifier = ParsingHelper.getString(in, "Enter road qualifier: ");

        Double startMeasure, endMeasure;
        do {
            startMeasure = ParsingHelper.getNonNegativeDouble(in, "Enter start measure: ");
            endMeasure = ParsingHelper.getNonNegativeDouble(in, "Enter end measure: ");

            if (startMeasure > endMeasure) {
                System.out.println("End measure should be greater than start measure.");
            }
        } while (startMeasure > endMeasure);

        int maxKmPerHour = ParsingHelper.getPositiveInt(in, "Enter the speed limit (in km/h): ");

        try {
            db.insertSpeedLimit(roadName, roadQualifier, startMeasure, endMeasure, maxKmPerHour);

            System.out.println("Speed limit successfully inserted.");
        } catch (SQLException e) {
            if (e.getErrorCode() == DBClient.FOREIGN_KEY_ERROR_CODE) {
                System.out.printf(
                        "Could not create speed limit because no segment exists with " +
                        "road name '%s', road qualifier '%s', and start measure '%.2f'.\n",
                        roadName, roadQualifier, startMeasure
                );
            } else if (e.getErrorCode() == DBClient.PRIMARY_KEY_ERROR_CODE) {
                System.out.println(
                        "Could not create speed limit because one already exists with the " +
                        "same identifier."
                );
            } else {
                throw e;
            }
        }
    }
}
