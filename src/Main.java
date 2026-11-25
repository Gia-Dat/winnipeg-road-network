import database_client.DBConnection;
import database_client.DBLoader;
import records.RoadInfo;
import java.sql.Connection;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {
        // This is just a test, we actually have to print things nicely in tables.
        // Let's just focus on the database client for now, and use main() for
        // testing.
        try (Connection db = new DBConnection().connection()) {
            DBLoader loader = new DBLoader(db);

            ArrayList<RoadInfo> roads = loader.getRoadInfo("Bison Dr", "A");
            for (int i = 0; i < roads.size(); i++) {
                System.out.println(roads.get(i).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
