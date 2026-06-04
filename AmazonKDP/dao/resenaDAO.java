package dao;

import model.Resena;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResenaDAO {
    
    public static Resena crear(int libroId, int lectorId, int calificacion, String comentario) throws SQLException {
        String sql = "INSERT INTO resenas (libro_id, lector_id, calificacion, comentario) VALUES (?, ?, ?, ?)";
        Connection c = null;
        try {
            c = DatabaseConnection.getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, libroId);
                ps.setInt(2, lectorId);
                ps.setInt(3, calificacion);
                ps.setString(4, comentario);
                ps.executeUpdate();
                
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return obtenerPorId(rs.getInt(1));
                    }
                }
            }
        } finally {
            if (c != null) try { c.close(); } catch (SQLException ignored) {}
        }
        return null;
    }
    
    public static Resena obtenerPorId(int id) throws SQLException {
        String sql = "SELECT r.*, u.nombre as lector_nombre " +
                    "FROM resenas r " +
                    "JOIN usuarios u ON r.lector_id = u.id " +
                    "WHERE r.id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return construirResena(rs);
                }
            }
        }
        return null;
    }
    
    public static List<Resena> listarPorLibro(int libroId) throws SQLException {
        String sql = "SELECT r.*, u.nombre as lector_nombre " +
                    "FROM resenas r " +
                    "JOIN usuarios u ON r.lector_id = u.id " +
                    "WHERE r.libro_id = ? " +
                    "ORDER BY r.fecha_resena DESC";
        List<Resena> resenas = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, libroId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resenas.add(construirResena(rs));
                }
            }
        }
        return resenas;
    }
    
    public static List<Resena> listarPorLector(int lectorId) throws SQLException {
        String sql = "SELECT r.*, u.nombre as lector_nombre, l.titulo as libro_titulo " +
                    "FROM resenas r " +
                    "JOIN usuarios u ON r.lector_id = u.id " +
                    "JOIN libros l ON r.libro_id = l.id " +
                    "WHERE r.lector_id = ? " +
                    "ORDER BY r.fecha_resena DESC";
        List<Resena> resenas = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, lectorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resenas.add(construirResena(rs));
                }
            }
        }
        return resenas;
    }
    
    public static boolean actualizar(int id, int calificacion, String comentario) throws SQLException {
        String sql = "UPDATE resenas SET calificacion = ?, comentario = ? WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, calificacion);
            ps.setString(2, comentario);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }
    
    public static boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM resenas WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
    
    public static boolean existeResena(int libroId, int lectorId) throws SQLException {
        String sql = "SELECT 1 FROM resenas WHERE libro_id = ? AND lector_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, libroId);
            ps.setInt(2, lectorId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    private static Resena construirResena(ResultSet rs) throws SQLException {
        // Implementar según tu clase Resena
        return new Resena(
            rs.getInt("id"),
            rs.getInt("libro_id"),
            rs.getInt("lector_id"),
            rs.getInt("calificacion"),
            rs.getString("comentario"),
            rs.getTimestamp("fecha_resena"),
            rs.getString("lector_nombre")
        );
    }
}