package cli.commands;
import cli.ExceptionHandler;
import cli.ParsingHelper;
import cli.printers.RoadInfoPrinter;
import database_client.DBClient;
import records.RoadInfo;
import java.util.ArrayList;
import java.util.Scanner;

public class GetRoadInfo implements Command {
    private static final String KEYWORD = "rinfo";
    private static final String DESCRIPTION =
        "Get detailed information by segment for a road";

    public GetRoadInfo() {}

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

        try {
            ArrayList<RoadInfo> results = db.getRoadInfo(roadName);

            RoadInfoPrinter printer = new RoadInfoPrinter();
            for (RoadInfo result : results) {
                printer.printRow(result);
            }
        } catch (Exception e) {
            ExceptionHandler.printErrorMsg(e);
        }
    }
}
