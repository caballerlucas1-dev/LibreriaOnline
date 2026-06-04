import java.sql.*;

/**
 * PATRÓN SINGLETON
 * Garantiza una única instancia de conexión a la base de datos
 */
public class DatabaseConnectionSingleton {
    // Instancia única (patrón Singleton)
    private static DatabaseConnectionSingleton instance = null;
    
    private final String URL = "jdbc:mysql://localhost:3306/amazon_kdp";
    private final String USER = "root";
    private final String PASSWORD = "";
    private Connection connection;
    
    // Constructor privado (impide instanciación externa)
    private DatabaseConnectionSingleton() throws SQLException {
        this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    // Método estático para obtener la única instancia
    public static DatabaseConnectionSingleton getInstance() throws SQLException {
        if (instance == null) {
            synchronized (DatabaseConnectionSingleton.class) {
                if (instance == null) {
                    instance = new DatabaseConnectionSingleton();
                }
            }
        }
        return instance;
    }
    
    public Connection getConnection() {
        return connection;
    }
}
