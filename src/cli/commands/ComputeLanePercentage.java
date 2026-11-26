package cli.commands;
import cli.ExceptionHandler;
import cli.ParsingHelper;
import database_client.DBClient;
import java.util.Scanner;

public class ComputeLanePercentage implements Command {
    private static final String KEYWORD = "lanepercent";
    private static final String DESCRIPTION =
        "Computes the percentage of a given road with a specific number of lanes";

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
        String roadName = ParsingHelper.getString(
                in, "Enter road name: "
        );
        String roadQualifier = ParsingHelper.getString(
                in, "Enter road qualifier: "
        );

        Integer numLanes = null;
        do {
            if (numLanes != null) {
                System.out.println("The number of lanes should be positive.");
            }

            numLanes = ParsingHelper.getInt(in, "Enter number of lanes: ");
        } while (numLanes < 1);

        try {
            Double percentageResult = db.getLaneCountPercentage(
                roadName, roadQualifier, numLanes
            );
            System.out.printf("%.2f%%", percentageResult);
        } catch (Exception e) {
            ExceptionHandler.printErrorMsg(e);
        }
    }
}
