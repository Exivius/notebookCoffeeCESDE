package app.persistence.dbConnection;

import java.sql.Connection;
import java.sql.DriverManager;

public class DataBaseConnection {
    private static DataBaseConnection instance;
    private final Connection connection;

    private static String url = "jdbc:mysql://localhost:3306/notebook_coffee_cesde";
    private static String username = "root";
    private static String password = "Samael.0566";

    private DataBaseConnection() {
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Se estableció conexión con la base de datos");
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar a la base de datos", e);
        }
    }

    public static synchronized DataBaseConnection getInstance(){
        if (instance == null) {
            instance = new DataBaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}