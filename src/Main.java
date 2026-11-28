import cli.CommandExecutor;
import cli.ExceptionHandler;
import database_client.DBConnection;
import database_client.DBClient;
import java.sql.Connection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Connection conn = new DBConnection().connection()) {
            // Initialize dependencies
            Scanner in = new Scanner(System.in);
            DBClient dbl = new DBClient(conn);

            // Run the REPL
            CommandExecutor.run(in, dbl);

            in.close();
        } catch (Exception e) {
            ExceptionHandler.printErrorMsg(e);
        }
    }
}
