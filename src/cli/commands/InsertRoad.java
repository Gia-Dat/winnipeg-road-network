package cli.commands;
import cli.ExceptionHandler;
import cli.ParsingHelper;
import database_client.DBClient;
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
    public void execute(Scanner in, DBClient db) {
        String name = ParsingHelper.getString(
                in, "Enter road name: "
        );
        String qualifier = ParsingHelper.getString(
                in, "Enter road qualifier: "
        );
        Double startMeasure = ParsingHelper.getNonNegativeDouble(
                in, "Enter start measure: "
        );
        Double endMeasure = ParsingHelper.getNonNegativeDouble(
                in, "Enter end measure: "
        );

        try {
            db.insertRoad(name, qualifier, startMeasure, endMeasure);

            System.out.println("Road successfully added.");
        } catch (Exception e) {
            ExceptionHandler.printErrorMsg(e);
        }
    }
}
