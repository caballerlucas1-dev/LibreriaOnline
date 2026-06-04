package dao;

import model.Autor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AutorDAO {
    
    public static Autor crearAutorCompleto(String nombre, String email, String password, String biografia) throws SQLException {
        Connection c = null;
        try {
            c = DatabaseConnection.getConnection();
            c.setAutoCommit(false);
            
            // Insertar usuario
            String sqlUsuario = "INSERT INTO usuarios (nombre, email, password, tipo_usuario) VALUES (?, ?, ?, 'AUTOR')";
            int usuarioId;
            try (PreparedStatement ps = c.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, nombre);
                ps.setString(2, email);
                ps.setString(3, password);
                ps.executeUpdate();
                
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("Error al crear usuario");
                    usuarioId = rs.getInt(1);
                }
            }
            
            // Insertar autor
            String sqlAutor = "INSERT INTO autores (usuario_id, biografia, total_ganancias) VALUES (?, ?, 0)";
            int autorId;
            try (PreparedStatement ps = c.prepareStatement(sqlAutor, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, usuarioId);
                ps.setString(2, biografia);
                ps.executeUpdate();
                
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("Error al crear autor");
                    autorId = rs.getInt(1);
                }
            }
            
            c.commit();
            return new Autor(usuarioId, nombre, email, password, biografia, 0.0, autorId);
        } catch (SQLException ex) {
            if (c != null) try { c.rollback(); } catch (SQLException ignored) {}
            throw ex;
        } finally {
            if (c != null) try { c.setAutoCommit(true); c.close(); } catch (SQLException ignored) {}
        }
    }
    
    public static Autor obtenerAutorCompleto(int usuarioId) throws SQLException {
        String sql = "SELECT u.*, a.id as autor_id, a.biografia, a.total_ganancias " +
                    "FROM usuarios u " +
                    "JOIN autores a ON u.id = a.usuario_id " +
                    "WHERE u.id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Autor(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("biografia"),
                        rs.getDouble("total_ganancias"),
                        rs.getInt("autor_id")
                    );
                }
            }
        }
        return null;
    }
    
    public static Autor obtenerPorAutorId(int autorId) throws SQLException {
        String sql = "SELECT u.*, a.id as autor_id, a.biografia, a.total_ganancias " +
                    "FROM autores a " +
                    "JOIN usuarios u ON a.usuario_id = u.id " +
                    "WHERE a.id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, autorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Autor(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("biografia"),
                        rs.getDouble("total_ganancias"),
                        rs.getInt("autor_id")
                    );
                }
            }
        }
        return null;
    }
    
    public static List<Autor> listarTodos() throws SQLException {
        String sql = "SELECT u.*, a.id as autor_id, a.biografia, a.total_ganancias " +
                    "FROM autores a " +
                    "JOIN usuarios u ON a.usuario_id = u.id " +
                    "ORDER BY u.nombre";
        List<Autor> autores = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                autores.add(new Autor(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("biografia"),
                    rs.getDouble("total_ganancias"),
                    rs.getInt("autor_id")
                ));
            }
        }
        return autores;
    }
    
    public static boolean actualizarBiografia(int autorId, String biografia) throws SQLException {
        String sql = "UPDATE autores SET biografia = ? WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, biografia);
            ps.setInt(2, autorId);
            return ps.executeUpdate() > 0;
        }
    }
    
    public static boolean actualizarGanancias(int autorId, double monto) throws SQLException {
        String sql = "UPDATE autores SET total_ganancias = total_ganancias + ? WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, monto);
            ps.setInt(2, autorId);
            return ps.executeUpdate() > 0;
        }
    }
}