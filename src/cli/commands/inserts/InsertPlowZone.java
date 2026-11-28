package cli.commands.inserts;
import cli.ParsingHelper;
import cli.commands.Command;
import database_client.DBClient;
import java.sql.SQLException;
import java.util.Scanner;

public class InsertPlowZone implements Command {
    private static final String KEYWORD = "insertPlowZone";
    private static final String DESCRIPTION = "Adds a plow zone to the database.";

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
        String zoneID = ParsingHelper.getString(in, "Enter zone ID: ");
        String zoneName = ParsingHelper.getString(in, "Enter zone name: ");

        try {
            db.insertPlowZone(zoneID, zoneName);

            System.out.println("Plow zone successfully inserted.");
        } catch (SQLException e) {
            if (e.getErrorCode() == DBClient.PRIMARY_KEY_ERROR_CODE) {
                System.out.println(
                        "Could not create plow zone because one already " +
                        "exists with the same identifier."
                );
            } else {
                throw e;
            }
        }
    }
}
