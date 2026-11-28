package cli.commands.inserts;
import cli.ParsingHelper;
import cli.commands.Command;
import database_client.DBClient;
import java.sql.SQLException;
import java.util.Scanner;

public class InsertRoad implements Command {
    private static final String KEYWORD = "insertRoad";
    private static final String DESCRIPTION = "Adds a road to the database.";

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
                in, "Enter road name: "
        );
        String qualifier = ParsingHelper.getString(
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

        try {
            db.insertRoad(name, qualifier, startMeasure, endMeasure);
            System.out.println("Road successfully added.");
        } catch (SQLException e) {
            if (e.getErrorCode() == DBClient.PRIMARY_KEY_ERROR_CODE) {
                System.out.println(
                    "Could not create road because one already exists with the " +
                    "same identifier."
                );
            } else {
                throw e;
            }
        }
    }
}
