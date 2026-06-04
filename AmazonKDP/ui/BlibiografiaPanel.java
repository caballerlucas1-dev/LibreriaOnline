import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.border.*;

class BiografiaPanel extends JPanel {
    public BiografiaPanel(Autor autor) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("Mi BiografÃ­a");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JTextArea bioArea = new JTextArea(10, 40);
        bioArea.setText(autor.getBiografia() != null ? autor.getBiografia() : "");
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        bioArea.setFont(new Font("Arial", Font.PLAIN, 14));
        bioArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        JScrollPane scrollPane = new JScrollPane(bioArea);
        formPanel.add(scrollPane, BorderLayout.CENTER);
        
        JButton saveBtn = new JButton("Guardar BiografÃ­a");
        saveBtn.setBackground(new Color(255, 153, 0));
        saveBtn.addActionListener(e -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "UPDATE autores SET biografia = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, bioArea.getText());
                stmt.setInt(2, autor.getAutorId());
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "BiografÃ­a actualizada");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(saveBtn);
        formPanel.add(btnPanel, BorderLayout.SOUTH);
        
        add(formPanel, BorderLayout.CENTER);
    }
}