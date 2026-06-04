package dao;

import model.Libro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarritoDAO {
    
    public static boolean agregarLibro(int lectorId, int libroId) throws SQLException {
        String sql = "INSERT IGNORE INTO carrito (lector_id, libro_id) VALUES (?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, lectorId);
            ps.setInt(2, libroId);
            return ps.executeUpdate() > 0;
        }
    }
    
    public static boolean removerLibro(int lectorId, int libroId) throws SQLException {
        String sql = "DELETE FROM carrito WHERE lector_id = ? AND libro_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, lectorId);
            ps.setInt(2, libroId);
            return ps.executeUpdate() > 0;
        }
    }
    
    public static List<Libro> obtenerLibros(int lectorId) throws SQLException {
        String sql = "SELECT l.*, u.nombre as autor_nombre, a.id as autor_id " +
                    "FROM carrito c " +
                    "JOIN libros l ON c.libro_id = l.id " +
                    "JOIN autores a ON l.autor_id = a.id " +
                    "JOIN usuarios u ON a.usuario_id = u.id " +
                    "WHERE c.lector_id = ? " +
                    "ORDER BY c.fecha_agregado DESC";
        List<Libro> libros = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, lectorId);
            try (ResultSet rs = ps.executeQuery()) {
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
            }
        }
        return libros;
    }
    
    public static boolean vaciar(int lectorId) throws SQLException {
        String sql = "DELETE FROM carrito WHERE lector_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, lectorId);
            return ps.executeUpdate() >= 0;
        }
    }
    
    public static int contarLibros(int lectorId) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM carrito WHERE lector_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, lectorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }
    
    public static boolean existeLibro(int lectorId, int libroId) throws SQLException {
        String sql = "SELECT 1 FROM carrito WHERE lector_id = ? AND libro_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, lectorId);
            ps.setInt(2, libroId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}