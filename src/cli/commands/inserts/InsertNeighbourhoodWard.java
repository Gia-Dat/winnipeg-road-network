package cli.commands.inserts;
import cli.ParsingHelper;
import cli.commands.Command;
import database_client.DBClient;
import java.sql.SQLException;
import java.util.Scanner;

public class InsertNeighbourhoodWard implements Command {
    private static final String KEYWORD = "insertNeighbourhoodWard";
    private static final String DESCRIPTION =
            "Adds a ward to the database for a given neighbourhood.";

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
        String neighbourhoodName = ParsingHelper.getString(
            in, "Enter neighbourhood name: "
        );
        int wardID = ParsingHelper.getPositiveInt(
            in, "Enter ward ID: "
        );

        try {
            db.insertNeighbourhoodWard(neighbourhoodName, wardID);

            System.out.println("Neighbourhood ward successfully inserted.");
        } catch (SQLException e) {
            if (e.getErrorCode() == DBClient.PRIMARY_KEY_ERROR_CODE) {
                System.out.println(
                    "Could not create neighbourhood ward because one " +
                    "already exists with the same identifier."
                );
            } else if (e.getErrorCode() == DBClient.FOREIGN_KEY_ERROR_CODE) {
                System.out.printf(
                    "Could not create neighbourhood ward because no " +
                    "neighbourhood exists with the name '%s'.\n",
                    neighbourhoodName
                );
            } else {
                throw e;
            }
        }
    }
}
