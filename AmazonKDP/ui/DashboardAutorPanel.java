import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.border.*;
import javax.swing.table.*;

class DashboardAutorPanel extends JPanel {
    public DashboardAutorPanel(Autor autor) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // Panel de estadísticas
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Total libros
            String sqlLibros = "SELECT COUNT(*) as total FROM libros WHERE autor_id = ?";
            PreparedStatement stmtLibros = conn.prepareStatement(sqlLibros);
            stmtLibros.setInt(1, autor.getAutorId());
            ResultSet rsLibros = stmtLibros.executeQuery();
            int totalLibros = rsLibros.next() ? rsLibros.getInt("total") : 0;
            
            // Total ventas
            String sqlVentas = "SELECT SUM(total_ventas) as total FROM libros WHERE autor_id = ?";
            PreparedStatement stmtVentas = conn.prepareStatement(sqlVentas);
            stmtVentas.setInt(1, autor.getAutorId());
            ResultSet rsVentas = stmtVentas.executeQuery();
            int totalVentas = rsVentas.next() ? rsVentas.getInt("total") : 0;
            
            // Calificación promedio
            String sqlCalif = "SELECT AVG(calificacion_promedio) as promedio FROM libros WHERE autor_id = ?";
            PreparedStatement stmtCalif = conn.prepareStatement(sqlCalif);
            stmtCalif.setInt(1, autor.getAutorId());
            ResultSet rsCalif = stmtCalif.executeQuery();
            double promedioCalif = rsCalif.next() ? rsCalif.getDouble("promedio") : 0.0;
            
            statsPanel.add(createStatCard("📚 Libros", String.valueOf(totalLibros), new Color(52, 152, 219)));
            statsPanel.add(createStatCard("💰 Ganancias", String.format("$%.2f", autor.getTotalGanancias()), new Color(46, 204, 113)));
            statsPanel.add(createStatCard("📊 Ventas", String.valueOf(totalVentas), new Color(155, 89, 182)));
            statsPanel.add(createStatCard("⭐ Rating", String.format("%.1f", promedioCalif), new Color(241, 196, 15)));
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        add(statsPanel, BorderLayout.NORTH);
        
        // Tabla de libros recientes
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        
        JLabel tableTitle = new JLabel("Libros Recientes");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 18));
        tableTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        
        String[] columns = {"Título", "Precio", "Ventas", "Rating", "Ganancias"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM libros WHERE autor_id = ? ORDER BY fecha_publicacion DESC LIMIT 10";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, autor.getAutorId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int ventas = rs.getInt("total_ventas");
                double precio = rs.getDouble("precio");
                model.addRow(new Object[]{
                    rs.getString("titulo"),
                    "$" + precio,
                    ventas,
                    String.format("%.1f ⭐", rs.getDouble("calificacion_promedio")),
                    String.format("$%.2f", ventas * precio * 0.30)
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        add(tablePanel, BorderLayout.CENTER);
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        card.add(titleLabel, BorderLayout.NORTH);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(Color.WHITE);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
}
