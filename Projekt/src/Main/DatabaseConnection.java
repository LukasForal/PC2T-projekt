import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://ep-aged-math-a6u1uyqr.us-west-2.aws.neon.tech/neondb";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_zXp5gDwfLk4G";
    
    private static Connection connection;
    
    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL + "?sslmode=require", USER, PASSWORD);
                System.out.println("Database connected successfully!");
            } catch (SQLException e) {
                System.out.println("Database connection failed!");
                e.printStackTrace();
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Database connection closed!");
            } catch (SQLException e) {
                System.out.println("Connection already closed by database server");
            }
        }
    }
}
