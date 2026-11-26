import cli.CommandExecutor;
import cli.ExceptionHandler;
import database_client.DBConnection;
import database_client.DBClient;
import java.sql.Connection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialize dependencies
            Scanner in = new Scanner(System.in);
            Connection conn = new DBConnection().connection();
            DBClient dbl = new DBClient(conn);

            // Run the REPL
            CommandExecutor.run(in, dbl);
        } catch (Exception e) {
            ExceptionHandler.printErrorMsg(e);
        }
    }
}
