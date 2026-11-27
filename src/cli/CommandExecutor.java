package cli;
import cli.commands.*;
import database_client.DBClient;
import java.util.Scanner;

public class CommandExecutor {
    private static final int TITLE_LENGTH = 72;
    private static final String TITLE_PADDING = "=";
    private static final String CURSOR = "> ";

    private static final String QUIT_CMD_KEYWORD = "quit";
    private static final String HELP_CMD_KEYWORD = "help";

    private static final Command[] COMMANDS = new Command[]{
        new GetRoadInfo(),
        new ComputeLanePercentage(),
        new InsertRoad(),
        new InsertSegment(),
        new DeleteAll()
    };

    /**
     * Prints a welcome message, then runs the main command loop until
     * the user enters 'quit'.
     */
    public static void run(Scanner in, DBClient db) {
        printTitle("CITY OF WINNIPEG ROAD NETWORK");
        System.out.printf(
            "Welcome! Type '%s' for a list commands, or '%s' to quit.\n",
            HELP_CMD_KEYWORD, QUIT_CMD_KEYWORD
        );

        String rawInput;
        while (true) {
            System.out.print("\n" + CURSOR);
            rawInput = in.nextLine().toLowerCase().trim();

            // Handle "special" commands
            if (rawInput.equals(HELP_CMD_KEYWORD)) {
                printCommands();
                continue;
            } else if (rawInput.equals(QUIT_CMD_KEYWORD)) {
                break;
            }

            // Find the command using its keyword
            Command toExecute = null;
            for (Command cmd : COMMANDS) {
                if (rawInput.equals(cmd.getKeyword().toLowerCase())) {
                    toExecute = cmd;
                }
            }

            if (toExecute == null) {
                System.out.printf(
                        "Invalid command: '%s'. Enter '%s' for a list of valid commands.\n",
                        rawInput, HELP_CMD_KEYWORD
                );
                continue;
            }

            try {
                toExecute.execute(in, db);
            } catch (Exception e) {
                ExceptionHandler.printErrorMsg(e);
            }
        }
    }

    private static void printCommands() {
        printTitle("COMMANDS");
        for (Command cmd : COMMANDS) {
            System.out.printf("%s - %s\n", cmd.getKeyword(), cmd.getDescription());
        }
    }

    private static void printTitle(String title) {
        title = title.toUpperCase();

        int titleLength = title.length();

        int paddingNeeded = Math.max(
                0, (TITLE_LENGTH - titleLength - 2) / 2
        );

        String paddedTitle = TITLE_PADDING.repeat(paddingNeeded) +
                String.format(" %s ", title) +
                TITLE_PADDING.repeat(paddingNeeded);

        if (paddedTitle.length() == TITLE_LENGTH - 1) {
            paddedTitle += TITLE_PADDING;
        }

        System.out.println(paddedTitle);
    }
}
