package cli;
import java.util.Scanner;

/**
 * Repeatedly prompts the user for an input of a specific type until they
 * enter an input of that type.
 */
public class ParsingHelper {
    public static String getString(Scanner in, String prompt) {
        String text = null;
        do {
            if (text != null) {
                System.out.println("Value should be non-empty.");
            }

            System.out.print(prompt);
            text = in.nextLine().trim();
        } while (text.isEmpty());

        return text;
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

    public static int getPositiveInt(Scanner in, String prompt) {
        Integer number = null;
        do {
            if (number != null) {
                System.out.printf(
                    "'%d' is not a positive integer.\n", number
                );
            }

            number = getInt(in, prompt);
        } while (number < 1);

        return number;
    }

    public static Double getDouble(Scanner in, String prompt) {
        String raw = null;
        Double number = null;
        do {
            if (raw != null) {
                System.out.printf(
                    "'%s' is not a valid number.\n", raw
                );
            }

            System.out.print(prompt);
            raw = in.nextLine().trim();

            try {
                number = Double.parseDouble(raw);
            } catch (NumberFormatException e) {}
        } while (number == null);

        return number;
    }

    public static Double getNonNegativeDouble(Scanner in, String prompt) {
        Double number = null;
        do {
            if (number != null) {
                System.out.printf(
                        "'%f' is not a non-negative number.\n", number
                );
            }

            number = getDouble(in, prompt);
        } while (number < 0);

        return number;
    }
}
