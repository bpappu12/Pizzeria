package cpsc4620;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class DBConnectorTest {

    public static void main(String[] args) {
        try {
            // Attempt to make a connection
            Connection connection = DBConnector.make_connection();

            // Check if the connection is not null (indicating success)
            if (connection != null) {
                System.out.println("Connection to the database successful!");
                
                // Close the connection after testing
                connection.close();
            } else {
                System.out.println("Failed to connect to the database.");
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
