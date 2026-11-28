package cli.commands.inserts;
import cli.ParsingHelper;
import cli.commands.Command;
import database_client.DBClient;
import java.sql.SQLException;
import java.util.Scanner;

public class InsertSchoolZone implements Command {
    private static final String KEYWORD = "insertSchoolZone";
    private static final String DESCRIPTION =
            "Adds a school zone to the database for a given speed limit.";

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
        Double segmentStartMeasure = ParsingHelper.getNonNegativeDouble(
                in, "Enter the start measure of the corresponding speed limit: "
        );
        String schoolName = ParsingHelper.getString(
                in, "Enter school name: "
        );
        String effectiveDaysDesc = ParsingHelper.getString(
                in, "Enter a description of the school zone's effective days: "
        );
        String effectiveTimesDesc = ParsingHelper.getString(
                in, "Enter a description of the school zone's effective times: "
        );

        try {
            db.insertSchoolZone(
                    roadName, roadQualifier, segmentStartMeasure, schoolName,
                    effectiveDaysDesc, effectiveTimesDesc
            );

            System.out.println("School zone successfully inserted.");
        } catch (SQLException e) {
            if (e.getErrorCode() == DBClient.FOREIGN_KEY_ERROR_CODE) {
                System.out.printf(
                        "Could not create school zone because no speed limit " +
                        "exists with road name '%s', road qualifier '%s', and " +
                        "start measure '%.2f'.\n",
                        roadName, roadQualifier, segmentStartMeasure
                );
            } else if (e.getErrorCode() == DBClient.PRIMARY_KEY_ERROR_CODE) {
                System.out.println(
                        "Could not create school zone because one already exists " +
                        "with the same identifier."
                );
            } else {
                throw e;
            }
        }
    }
}
