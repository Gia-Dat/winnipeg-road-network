package cli.printers;
import records.RoadInfo;

/**
 * Prints RoadInfo records to the terminal in tabular form.
 */
public class RoadInfoPrinter {
    private final TablePrinter tablePrinter;

    public RoadInfoPrinter() {
        this.tablePrinter = new TablePrinter(
                new String[]{
                    "Road Name", "Road Qualifier", "Speed Limit", "Lanes",
                    "Street Class", "One Way", "Start Measure", "End Measure"
                }
        );

        this.tablePrinter.printHeader();
    }

    public void printRow(RoadInfo roadInfo) {
        this.tablePrinter.printRow(
                new String[]{
                    roadInfo.roadName(), roadInfo.roadQualifier(),
                    Integer.toString(roadInfo.maxKmPerHour()),
                    Integer.toString(roadInfo.numLanes()),
                    roadInfo.streetClass(),
                    Boolean.toString(roadInfo.oneWay()),
                    Double.toString(roadInfo.startMeasure()),
                    Double.toString(roadInfo.endMeasure())
                }
        );
    }
}
