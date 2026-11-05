package app.persistence.dbConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
    private static DataBaseConnection instance;
    private final Connection connection;

    private DataBaseConnection() {
        String url = System.getenv("NOTEBOOK_DB_URL");
        String username = System.getenv("NOTEBOOK_DB_USER");
        String password = System.getenv("NOTEBOOK_DB_PASSWORD");
        if (url == null || url.isBlank() || username == null || username.isBlank() || password == null) {
            throw new IllegalStateException(
                    "Configure NOTEBOOK_DB_URL, NOTEBOOK_DB_USER y NOTEBOOK_DB_PASSWORD para su MySQL.");
        }
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Se estableció conexión con la base de datos");
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo conectar con el MySQL configurado.", e);
        }
    }

    public static synchronized DataBaseConnection getInstance() {
        if (instance == null) {
            instance = new DataBaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
