package dao;

import model.Libro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {
    
    public static Libro crear(Libro libro) throws SQLException {
        String sql = "INSERT INTO libros (titulo, subtitulo, descripcion, precio, imagen_portada, autor_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        Connection c = null;
        try {
            c = DatabaseConnection.getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, libro.getTitulo());
                ps.setString(2, libro.getSubtitulo());
                ps.setString(3, libro.getDescripcion());
                ps.setDouble(4, libro.getPrecio());
                ps.setString(5, libro.getImagenPortada());
                ps.setInt(6, libro.getAutorId());
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
    
    public static Libro obtenerPorId(int id) throws SQLException {
        String sql = "SELECT l.*, u.nombre as autor_nombre, a.id as autor_id " +
                    "FROM libros l " +
                    "JOIN autores a ON l.autor_id = a.id " +
                    "JOIN usuarios u ON a.usuario_id = u.id " +
                    "WHERE l.id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return construirLibro(rs);
                }
            }
        }
        return null;
    }
    
    public static List<Libro> listarTodos() throws SQLException {
        String sql = "SELECT l.*, u.nombre as autor_nombre, a.id as autor_id " +
                    "FROM libros l " +
                    "JOIN autores a ON l.autor_id = a.id " +
                    "JOIN usuarios u ON a.usuario_id = u.id " +
                    "ORDER BY l.fecha_publicacion DESC";
        List<Libro> libros = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                libros.add(construirLibro(rs));
            }
        }
        return libros;
    }
    
    public static List<Libro> listarPorAutor(int autorId) throws SQLException {
        String sql = "SELECT l.*, u.nombre as autor_nombre, a.id as autor_id " +
                    "FROM libros l " +
                    "JOIN autores a ON l.autor_id = a.id " +
                    "JOIN usuarios u ON a.usuario_id = u.id " +
                    "WHERE l.autor_id = ? " +
                    "ORDER BY l.fecha_publicacion DESC";
        List<Libro> libros = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, autorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    libros.add(construirLibro(rs));
                }
            }
        }
        return libros;
    }
    
    public static List<Libro> listarMasVendidos(int limite) throws SQLException {
        String sql = "SELECT * FROM libros_mas_vendidos LIMIT ?";
        List<Libro> libros = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    libros.add(construirLibro(rs));
                }
            }
        }
        return libros;
    }
    
    public static List<Libro> buscarPorTitulo(String termino) throws SQLException {
        String sql = "SELECT l.*, u.nombre as autor_nombre, a.id as autor_id " +
                    "FROM libros l " +
                    "JOIN autores a ON l.autor_id = a.id " +
                    "JOIN usuarios u ON a.usuario_id = u.id " +
                    "WHERE l.titulo LIKE ? OR l.subtitulo LIKE ? " +
                    "ORDER BY l.total_ventas DESC";
        List<Libro> libros = new ArrayList<>();
        String patron = "%" + termino + "%";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, patron);
            ps.setString(2, patron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    libros.add(construirLibro(rs));
                }
            }
        }
        return libros;
    }
    
    public static boolean actualizar(Libro libro) throws SQLException {
        String sql = "UPDATE libros SET titulo = ?, subtitulo = ?, descripcion = ?, " +
                    "precio = ?, imagen_portada = ? WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getSubtitulo());
            ps.setString(3, libro.getDescripcion());
            ps.setDouble(4, libro.getPrecio());
            ps.setString(5, libro.getImagenPortada());
            ps.setInt(6, libro.getId());
            return ps.executeUpdate() > 0;
        }
    }
    
    public static boolean incrementarVentas(Connection conn, int libroId) throws SQLException {
        String sql = "UPDATE libros SET total_ventas = total_ventas + 1 WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, libroId);
            return ps.executeUpdate() > 0;
        }
    }
    
    public static boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM libros WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
    
    private static Libro construirLibro(ResultSet rs) throws SQLException {
        return new Libro(
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
        );
    }
}