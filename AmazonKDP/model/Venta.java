import java.sql.*;
import java.util.Date;

class Venta {
    private int id;
    private Libro libro;
    private Lector lector;
    private double precioVenta;
    private double regaliaAutor;
    private String metodoPago;
    private Date fechaVenta;
    
    public Venta(Libro libro, Lector lector, String metodoPago) {
        this.libro = libro;
        this.lector = lector;
        this.metodoPago = metodoPago;
        this.precioVenta = libro.getPrecio();
        this.regaliaAutor = precioVenta * 0.30;
    }
    
    public boolean procesar() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            // Registrar venta
            String sqlVenta = "INSERT INTO ventas (libro_id, lector_id, precio_venta, regalia_autor, metodo_pago) " +
                            "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmtVenta = conn.prepareStatement(sqlVenta);
            stmtVenta.setInt(1, libro.getId());
            stmtVenta.setInt(2, lector.getId());
            stmtVenta.setDouble(3, precioVenta);
            stmtVenta.setDouble(4, regaliaAutor);
            stmtVenta.setString(5, metodoPago);
            stmtVenta.executeUpdate();
            
            // Actualizar ventas del libro
            String sqlLibro = "UPDATE libros SET total_ventas = total_ventas + 1 WHERE id = ?";
            PreparedStatement stmtLibro = conn.prepareStatement(sqlLibro);
            stmtLibro.setInt(1, libro.getId());
            stmtLibro.executeUpdate();
            
            // Actualizar ganancias del autor
            String sqlAutor = "UPDATE autores SET total_ganancias = total_ganancias + ? WHERE id = ?";
            PreparedStatement stmtAutor = conn.prepareStatement(sqlAutor);
            stmtAutor.setDouble(1, regaliaAutor);
            stmtAutor.setInt(2, libro.getAutorId());
            stmtAutor.executeUpdate();
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
