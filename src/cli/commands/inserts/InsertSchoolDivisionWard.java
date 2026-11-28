package cli.commands.inserts;
import cli.ParsingHelper;
import cli.commands.Command;
import database_client.DBClient;
import java.sql.SQLException;
import java.util.Scanner;

public class InsertSchoolDivisionWard implements Command {
    private static final String KEYWORD = "insertSchoolDivisionWard";
    private static final String DESCRIPTION =
            "Adds a ward to the database for a given school division.";

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
        String divisionName = ParsingHelper.getString(
                in, "Enter school division name: "
        );
        int wardID = ParsingHelper.getPositiveInt(
                in, "Enter ward ID: "
        );

        try {
            db.insertSchoolDivisionWard(divisionName, wardID);

            System.out.println("School division ward successfully inserted.");
        } catch (SQLException e) {
            if (e.getErrorCode() == DBClient.PRIMARY_KEY_ERROR_CODE) {
                System.out.println(
                    "Could not create school division ward because one " +
                    "already exists with the same identifier."
                );
            } else if (e.getErrorCode() == DBClient.FOREIGN_KEY_ERROR_CODE) {
                System.out.printf(
                    "Could not create school division ward because no " +
                    "school division exists with the name '%s'.\n",
                    divisionName
                );
            } else {
                throw e;
            }
        }
    }
}
