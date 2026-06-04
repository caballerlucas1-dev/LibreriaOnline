import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.border.*;
import javax.swing.table.*;

class MisComprasPanel extends JPanel {
    public MisComprasPanel(Lector lector) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("Mis Compras");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        String[] columns = {"Libro", "Autor", "Precio", "Fecha", "Método de Pago"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT l.titulo, u.nombre as autor, v.precio_venta, v.fecha_venta, v.metodo_pago " +
                        "FROM ventas v " +
                        "JOIN libros l ON v.libro_id = l.id " +
                        "JOIN autores a ON l.autor_id = a.id " +
                        "JOIN usuarios u ON a.usuario_id = u.id " +
                        "WHERE v.lector_id = ? " +
                        "ORDER BY v.fecha_venta DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, lector.getId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    "$" + rs.getDouble("precio_venta"),
                    rs.getTimestamp("fecha_venta"),
                    rs.getString("metodo_pago")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(20, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);
    }
}
