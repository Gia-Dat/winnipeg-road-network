import cli.CommandExecutor;
import cli.ExceptionHandler;
import database_client.DBConnection;
import database_client.DBLoader;
import java.sql.Connection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialize dependencies
            Scanner in = new Scanner(System.in);
            Connection conn = new DBConnection().connection();
            DBLoader db = new DBLoader(conn);

            // Run the REPL
            CommandExecutor.run(in, db);
        } catch (Exception e) {
            ExceptionHandler.printErrorMsg(e);
        }
    }
}
