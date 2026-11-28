package cli.commands.inserts;
import cli.ParsingHelper;
import cli.commands.Command;
import database_client.DBClient;
import java.sql.SQLException;
import java.util.Scanner;

public class InsertSchoolDivision implements Command {
    private static final String KEYWORD = "insertSchoolDivision";
    private static final String DESCRIPTION = "Adds a school division to the database.";

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
        String schoolDivisionName = ParsingHelper.getString(
                in, "Enter school division name: "
        );
        String schoolDivisionUrl = ParsingHelper.getString(
                in, "Enter school division url: "
        );

        try {
            db.insertSchoolDivision(schoolDivisionName, schoolDivisionUrl);

            System.out.println("School division successfully inserted.");
        } catch (SQLException e) {
            if (e.getErrorCode() == DBClient.PRIMARY_KEY_ERROR_CODE) {
                System.out.println(
                    "Could not create school division because one already " +
                    "exists with the same identifier."
                );
            } else {
                throw e;
            }
        }
    }
}
