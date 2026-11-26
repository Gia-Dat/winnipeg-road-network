package cli;
import java.util.Scanner;

/**
 * Repeatedly prompts the user for an input of a specific type until they
 * enter an input of that type.
 */
public class ParsingHelper {
    public static String getString(Scanner in, String prompt) {
        System.out.print(prompt);
        return in.nextLine();
    }

    public static int getInt(Scanner in, String prompt) {
        String raw = null;
        Integer number = null;
        do {
            if (raw != null) {
                System.out.printf(
                        "'%s' is not a valid integer.\n", raw
                );
            }

            System.out.print(prompt);
            raw = in.nextLine().trim();

            try {
                number = Integer.parseInt(raw);

            } catch (NumberFormatException e) {}
        } while (number == null);

        return number;
    }
}
