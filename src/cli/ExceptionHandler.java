package cli;
import java.sql.SQLException;

/**
 * Prints error messages to the terminal based on exception types.
 */
public class ExceptionHandler {
    public static void printErrorMsg(Exception e) {
        if (e instanceof SQLException) {
            System.out.printf("Database error: %s\n", e.getMessage());
        } else {
            System.out.printf(
                    "An unexpected error occurred: %s\n",
                    e.getMessage()
            );
        }
    }
}
