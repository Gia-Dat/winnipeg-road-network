package cli.commands;
import database_client.DBClient;
import java.util.Scanner;

/**
 * Represents a command that the user can execute in the REPL.
 */
public interface Command {
    String getKeyword();
    String getDescription();
    void execute(Scanner in, DBClient db);
}
