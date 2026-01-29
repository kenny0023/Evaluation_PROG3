import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/mini_dish_db";
    private static final String USER = "mini_dish_db_manager";
    private static final String PASS = "1234";

    public static Connection getDBConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}