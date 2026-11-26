package cli.printers;

/**
 * Prints a header and a corresponding set of rows to the terminal as a table
 * with fixed-width columns.
 * Elements shorter than the column width are padded with spaces, and those
 * longer than the column width are truncated. All elements are left-aligned.
 * Assumes that the length of the given column header array and the lengths of
 * the given row arrays are equal.
 */
public class TablePrinter {
    private final String[] columnHeaders;

    private final int COLUMN_WIDTH = 15;
    private final String THREE_DOTS = "...";
    private final String VERTICAL_DELIMITER = " | ";
    private final String HORIZONTAL_DELIMITER = "-";

    public TablePrinter(String[] columnHeaders) {
        this.columnHeaders = columnHeaders;
    }

    public void printHeader() {
        this.printRow(this.columnHeaders);

        for (int i = 0; i < this.tableWidth(); i++) {
            System.out.print(HORIZONTAL_DELIMITER);
        }
        System.out.println();
    }

    public void printRow(String[] row) {
        for (int i = 0; i < row.length; i++) {
            printAsColWidth(row[i]);

            if (i != row.length - 1) {
                System.out.print(VERTICAL_DELIMITER);
            }
        }
        System.out.println();
    }

    private void printAsColWidth(String text) {
        text = this.padIfLessThanColLength(text);
        text = this.truncateIfExceedsColLength(text);

        System.out.print(text);
    }

    private String padIfLessThanColLength(String text) {
        StringBuilder padder = new StringBuilder(text);

        while (padder.length() < COLUMN_WIDTH) {
            padder.append(" ");
        }

        return padder.toString();
    }

    private String truncateIfExceedsColLength(String text) {
        if (text.length() > COLUMN_WIDTH) {
            text = text.substring(
                0, COLUMN_WIDTH - THREE_DOTS.length()
            );
            text += "...";
        }

        return text;
    }

    private int tableWidth() {
        int numColumns = this.columnHeaders.length;

        return (COLUMN_WIDTH * numColumns) +
                ((numColumns - 1) * VERTICAL_DELIMITER.length());
    }
}
