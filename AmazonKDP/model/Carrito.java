import java.sql.*;
import java.util.*;

class Carrito {
    private int lectorId;
    private List<Libro> libros;
    
    public Carrito(int lectorId) {
        this.lectorId = lectorId;
        this.libros = new ArrayList<>();
        cargarCarrito();
    }
    
    private void cargarCarrito() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT l.*, u.nombre as autor_nombre, a.id as autor_id " +
                        "FROM carrito c " +
                        "JOIN libros l ON c.libro_id = l.id " +
                        "JOIN autores a ON l.autor_id = a.id " +
                        "JOIN usuarios u ON a.usuario_id = u.id " +
                        "WHERE c.lector_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, lectorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                libros.add(new Libro(
                    rs.getInt("id"),
                    rs.getString("titulo"),
                    rs.getString("subtitulo"),
                    rs.getString("descripcion"),
                    rs.getDouble("precio"),
                    rs.getString("imagen_portada"),
                    rs.getInt("total_ventas"),
                    rs.getDouble("calificacion_promedio"),
                    rs.getInt("total_resenas"),
                    rs.getString("autor_nombre"),
                    rs.getInt("autor_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void agregarLibro(Libro libro) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT IGNORE INTO carrito (lector_id, libro_id) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, lectorId);
            stmt.setInt(2, libro.getId());
            stmt.executeUpdate();
            if (!libros.contains(libro)) {
                libros.add(libro);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void removerLibro(Libro libro) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM carrito WHERE lector_id = ? AND libro_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, lectorId);
            stmt.setInt(2, libro.getId());
            stmt.executeUpdate();
            libros.remove(libro);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public double calcularTotal() {
        return libros.stream().mapToDouble(Libro::getPrecio).sum();
    }
    
    public List<Libro> getLibros() { return new ArrayList<>(libros); }
    
    public void vaciar() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM carrito WHERE lector_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, lectorId);
            stmt.executeUpdate();
            libros.clear();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}