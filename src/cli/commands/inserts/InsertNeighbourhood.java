package cli.commands.inserts;
import cli.ParsingHelper;
import cli.commands.Command;
import database_client.DBClient;
import java.sql.SQLException;
import java.util.Scanner;

public class InsertNeighbourhood implements Command {
    private static final String KEYWORD = "insertNeighbourhood";
    private static final String DESCRIPTION = "Adds a neighbourhood to the database.";

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
        String name = ParsingHelper.getString(
                in, "Enter neighbourhood name: "
        );

        try {
            db.insertNeighbourhood(name);

            System.out.println("Neighbourhood successfully inserted.");
        } catch (SQLException e) {
            if (e.getErrorCode() == DBClient.PRIMARY_KEY_ERROR_CODE) {
                System.out.println(
                    "Could not create neighbourhood because one already " +
                    "exists with the same identifier."
                );
            } else {
                throw e;
            }
        }
    }
}