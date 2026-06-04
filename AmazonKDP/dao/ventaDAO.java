package dao;

import model.Libro;
import model.Lector;
import model.Venta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {
    
    public static Venta procesarVenta(Libro libro, Lector lector, String metodoPago) throws SQLException {
        Connection c = null;
        try {
            c = DatabaseConnection.getConnection();
            c.setAutoCommit(false);
            
            double precioVenta = libro.getPrecio();
            double regaliaAutor = precioVenta * 0.30;
            
            // Insertar venta
            String sqlVenta = "INSERT INTO ventas (libro_id, lector_id, precio_venta, regalia_autor, metodo_pago) " +
                            "VALUES (?, ?, ?, ?, ?)";
            int ventaId;
            try (PreparedStatement ps = c.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, libro.getId());
                ps.setInt(2, lector.getId());
                ps.setDouble(3, precioVenta);
                ps.setDouble(4, regaliaAutor);
                ps.setString(5, metodoPago);
                ps.executeUpdate();
                
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("Error al crear venta");
                    ventaId = rs.getInt(1);
                }
            }
            
            // Actualizar ventas del libro
            LibroDAO.incrementarVentas(c, libro.getId());
            
            // Actualizar ganancias del autor
            String sqlAutor = "UPDATE autores SET total_ganancias = total_ganancias + ? WHERE id = ?";
            try (PreparedStatement ps = c.prepareStatement(sqlAutor)) {
                ps.setDouble(1, regaliaAutor);
                ps.setInt(2, libro.getAutorId());
                ps.executeUpdate();
            }
            
            c.commit();
            return obtenerPorId(ventaId);
        } catch (SQLException ex) {
            if (c != null) try { c.rollback(); } catch (SQLException ignored) {}
            throw ex;
        } finally {
            if (c != null) try { c.setAutoCommit(true); c.close(); } catch (SQLException ignored) {}
        }
    }
    
    public static boolean procesarCompraCarrito(List<Libro> libros, int lectorId, String metodoPago) throws SQLException {
        Connection c = null;
        try {
            c = DatabaseConnection.getConnection();
            c.setAutoCommit(false);
            
            for (Libro libro : libros) {
                double precioVenta = libro.getPrecio();
                double regaliaAutor = precioVenta * 0.30;
                
                // Insertar venta
                String sqlVenta = "INSERT INTO ventas (libro_id, lector_id, precio_venta, regalia_autor, metodo_pago) " +
                                "VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = c.prepareStatement(sqlVenta)) {
                    ps.setInt(1, libro.getId());
                    ps.setInt(2, lectorId);
                    ps.setDouble(3, precioVenta);
                    ps.setDouble(4, regaliaAutor);
                    ps.setString(5, metodoPago);
                    ps.executeUpdate();
                }
                
                // Actualizar ventas del libro
                LibroDAO.incrementarVentas(c, libro.getId());
                
                // Actualizar ganancias del autor
                String sqlAutor = "UPDATE autores SET total_ganancias = total_ganancias + ? WHERE id = ?";
                try (PreparedStatement ps = c.prepareStatement(sqlAutor)) {
                    ps.setDouble(1, regaliaAutor);
                    ps.setInt(2, libro.getAutorId());
                    ps.executeUpdate();
                }
            }
            
            // Vaciar carrito
            CarritoDAO.vaciar(lectorId);
            
            c.commit();
            return true;
        } catch (SQLException ex) {
            if (c != null) try { c.rollback(); } catch (SQLException ignored) {}
            throw ex;
        } finally {
            if (c != null) try { c.setAutoCommit(true); c.close(); } catch (SQLException ignored) {}
        }
    }
    
    public static Venta obtenerPorId(int id) throws SQLException {
        String sql = "SELECT v.*, l.titulo, l.precio, l.autor_id, u.nombre as lector_nombre " +
                    "FROM ventas v " +
                    "JOIN libros l ON v.libro_id = l.id " +
                    "JOIN usuarios u ON v.lector_id = u.id " +
                    "WHERE v.id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Retornar datos básicos de venta
                    return construirVenta(rs);
                }
            }
        }
        return null;
    }
    
    public static List<Venta> listarPorLector(int lectorId) throws SQLException {
        String sql = "SELECT v.*, l.titulo, l.precio, l.autor_id, u.nombre as lector_nombre " +
                    "FROM ventas v " +
                    "JOIN libros l ON v.libro_id = l.id " +
                    "JOIN usuarios u ON v.lector_id = u.id " +
                    "WHERE v.lector_id = ? " +
                    "ORDER BY v.fecha_venta DESC";
        List<Venta> ventas = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, lectorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ventas.add(construirVenta(rs));
                }
            }
        }
        return ventas;
    }
    
    public static List<Venta> listarPorAutor(int autorId) throws SQLException {
        String sql = "SELECT v.*, l.titulo, l.precio, l.autor_id, u.nombre as lector_nombre " +
                    "FROM ventas v " +
                    "JOIN libros l ON v.libro_id = l.id " +
                    "JOIN usuarios u ON v.lector_id = u.id " +
                    "WHERE l.autor_id = ? " +
                    "ORDER BY v.fecha_venta DESC";
        List<Venta> ventas = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, autorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ventas.add(construirVenta(rs));
                }
            }
        }
        return ventas;
    }
    
    public static List<Venta> listarTodas() throws SQLException {
        String sql = "SELECT v.*, l.titulo, l.precio, l.autor_id, u.nombre as lector_nombre " +
                    "FROM ventas v " +
                    "JOIN libros l ON v.libro_id = l.id " +
                    "JOIN usuarios u ON v.lector_id = u.id " +
                    "ORDER BY v.fecha_venta DESC";
        List<Venta> ventas = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             Statement stmt = c.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ventas.add(construirVenta(rs));
            }
        }
        return ventas;
    }
    
    private static Venta construirVenta(ResultSet rs) throws SQLException {
        // Aquí construyes el objeto Venta con los datos del ResultSet
        // Nota: Necesitarás ajustar esto según tu clase Venta
        return null; // Implementar según necesites
    }
}