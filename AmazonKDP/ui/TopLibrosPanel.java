import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.border.*;
import javax.swing.table.*;

class TopLibrosPanel extends JPanel {
    public TopLibrosPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("Libros Más Vendidos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        String[] columns = {"Ranking", "Título", "Autor", "Ventas", "Precio", "Calificación"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM libros_mas_vendidos LIMIT 10";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            int ranking = 1;
            while (rs.next()) {
                model.addRow(new Object[]{
                    ranking++,
                    rs.getString("titulo"),
                    rs.getString("autor_nombre"),
                    rs.getInt("total_ventas"),
                    "$" + rs.getDouble("precio"),
                    String.format("⭐ %.1f", rs.getDouble("calificacion_promedio"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(20, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);
    }
}