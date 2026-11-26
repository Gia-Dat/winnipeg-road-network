package cli.commands;
import cli.ExceptionHandler;
import database_client.DBClient;
import java.util.Scanner;

public class DeleteAll implements Command {
    private static final String KEYWORD = "clear";
    private static final String DESCRIPTION = "Deletes all data in the database";


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
        System.out.printf(
            "[!] Type '%s' again to confirm that you want to delete ALL data: ",
            KEYWORD
        );

        String confirmation =  in.nextLine().trim().toLowerCase();
        if (confirmation.equals(KEYWORD)) {
            try {
                db.deleteAll();

                System.out.println("All data has been deleted.");
            } catch (Exception e) {
                ExceptionHandler.printErrorMsg(e);
            }
        } else {
            System.out.println("Operation cancelled.");
        }
    }
}
