package database_client;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    final String CONFIG_FILE_NAME = "auth.cfg";
    final String USERNAME_PROP_NAME = "username";
    final String PASSWORD_PROP_NAME = "password";

    /**
     * Returns a connection to the Microsoft SQLServer database.
     */
    public Connection connection() throws SQLException {
        Properties properties = getConnectionProperties();

        String username = properties.getProperty(USERNAME_PROP_NAME);
        String password = properties.getProperty(PASSWORD_PROP_NAME);
        assertUsernameAndPasswordNotNull(username, password);

        String connectionUrl = getConnectionUrl(username, password);

        return DriverManager.getConnection(connectionUrl);
    }

    private Properties getConnectionProperties() {
        Properties properties = new Properties();

        try {
            FileInputStream configFile = new FileInputStream(CONFIG_FILE_NAME);
            properties.load(configFile);

            return properties;
        } catch (FileNotFoundException e) {
            System.err.printf("Could not open '%s'.\n", CONFIG_FILE_NAME);
        } catch (IOException e) {
            System.err.printf("Could not read '%s'.\n", CONFIG_FILE_NAME);
        }

        System.exit(1);
        return properties;
    }

    private void assertUsernameAndPasswordNotNull(String username, String password) {
        if (username == null) {
            System.err.printf(
                    "No attribute '%s' in '%s'.\n",
                    USERNAME_PROP_NAME, CONFIG_FILE_NAME
            );
            System.exit(1);
        }

        if (password == null) {
            System.err.printf(
                    "No attribute '%s' in '%s'.\n",
                    PASSWORD_PROP_NAME, CONFIG_FILE_NAME
            );
            System.exit(1);
        }
    }

    private String getConnectionUrl(String username, String password) {
        return String.format(
                "jdbc:sqlserver://uranium.cs.umanitoba.ca:1433;database=cs3380;" +
                "user=%s;password=%s;encrypt=false;trustServerCertificate=false;" +
                 "loginTimeout=30;",
                username, password
        );
    }
}
