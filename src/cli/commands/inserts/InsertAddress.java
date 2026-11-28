package cli.commands.inserts;
import cli.ParsingHelper;
import cli.commands.Command;
import database_client.DBClient;
import java.sql.SQLException;
import java.util.Scanner;

public class InsertAddress implements Command {
    private static final String KEYWORD = "insertAddress";
    private static final String DESCRIPTION =
            "Adds an address to the database on a given road, in specific " +
            "neighbourhood and school division wards, and in a given " +
            "plow zone.";

    @Override
    public String getKeyword() {
        return KEYWORD;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public void execute(Scanner in, DBClient db) throws SQLException {
        final double MIN_LATITUDE = -90;
        final double MAX_LATITUDE = 90;
        final double MIN_LONGITUDE = -180;
        final double MAX_LONGITUDE = 180;

        int streetNum = ParsingHelper.getPositiveInt(
            in, "Enter the street number: "
        );

        Double latitude = null;
        do {
            if (latitude != null) {
                System.out.printf(
                    "Latitude should be between %.2f and %.2f.\n",
                    MIN_LATITUDE, MAX_LATITUDE
                );
            }

            latitude = ParsingHelper.getDouble(in, "Enter latitude: ");
        } while (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE);

        Double longitude = null;
        do {
            if (longitude != null) {
                System.out.printf(
                    "Longitude should be between %.2f and %.2f.\n",
                    MIN_LONGITUDE, MAX_LONGITUDE
                );
            }

            longitude = ParsingHelper.getDouble(in, "Enter longitude: ");
        } while (longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE);

        String roadName = ParsingHelper.getString(
            in, "Enter road name: "
        );
        String roadQualifier = ParsingHelper.getString(
            in, "Enter road qualifier: "
        );
        String neighbourhoodName = ParsingHelper.getString(
            in, "Enter neighbourhood name: "
        );
        int neighbourhoodWardID = ParsingHelper.getPositiveInt(
            in, "Enter neighbourhood ward ID: "
        );
        String schoolDivisionName = ParsingHelper.getString(
            in, "Enter school division name: "
        );
        int schoolDivisionWardID = ParsingHelper.getPositiveInt(
                in, "Enter school division ward ID: "
        );
        String plowZone = ParsingHelper.getOptionalString(
            in, "Enter plow zone ID (optional): "
        );

        try {
            db.insertAddress(
                streetNum, latitude, longitude, roadName, roadQualifier,
                neighbourhoodName, neighbourhoodWardID, schoolDivisionName,
                schoolDivisionWardID, plowZone
            );

            System.out.println("Address successfully inserted.");
        } catch (SQLException e) {
            if (e.getErrorCode() == DBClient.FOREIGN_KEY_ERROR_CODE) {
                if (plowZone == null) {
                    plowZone = "N/A";
                }

                System.out.printf(
                    "Could not create address because one or more of the " +
                    "referenced entities does not exist: road ('%s', '%s'), \n" +
                    "neighbourhood ward ('%s', '%s'), \n" +
                    "school division ward ('%s', '%s'), \n" +
                    "plow zone ('%s').\n",
                    roadName, roadQualifier, neighbourhoodName, neighbourhoodWardID,
                    schoolDivisionName, schoolDivisionWardID, plowZone
                );
            } else {
                throw e;
            }
        }
    }
}
