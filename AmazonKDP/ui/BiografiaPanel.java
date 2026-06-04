import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.border.*;

class BiografiaPanel extends JPanel {
    public BiografiaPanel(Autor autor) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("Mi Biografía");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // Panel central con límite de ancho
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(Color.WHITE);
        centerWrapper.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setMaximumSize(new Dimension(800, 600));
        
        JLabel instruccionLabel = new JLabel("<html><i>Escribe sobre ti, tu experiencia como autor, tus influencias literarias...</i></html>");
        instruccionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        instruccionLabel.setForeground(Color.GRAY);
        formPanel.add(instruccionLabel, BorderLayout.NORTH);
        
        JTextArea bioArea = new JTextArea(15, 60);
        bioArea.setText(autor.getBiografia() != null ? autor.getBiografia() : "");
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        bioArea.setFont(new Font("Arial", Font.PLAIN, 14));
        bioArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JScrollPane scrollPane = new JScrollPane(bioArea);
        scrollPane.setPreferredSize(new Dimension(700, 350));
        formPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        
        JLabel charCount = new JLabel("Caracteres: " + bioArea.getText().length());
        charCount.setFont(new Font("Arial", Font.PLAIN, 11));
        charCount.setForeground(Color.GRAY);
        btnPanel.add(charCount);
        
        btnPanel.add(Box.createHorizontalStrut(20));
        
        JButton saveBtn = new JButton("💾 Guardar Biografía");
        saveBtn.setBackground(new Color(255, 153, 0));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Arial", Font.BOLD, 14));
        saveBtn.setFocusPainted(false);
        saveBtn.setPreferredSize(new Dimension(180, 40));
        saveBtn.addActionListener(e -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "UPDATE autores SET biografia = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, bioArea.getText());
                stmt.setInt(2, autor.getAutorId());
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, 
                    "✅ Biografía actualizada exitosamente", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                autor.setBiografia(bioArea.getText());
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error al guardar: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        btnPanel.add(saveBtn);
        
        // Actualizar contador de caracteres
        bioArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                charCount.setText("Caracteres: " + bioArea.getText().length());
            }
        });
        
        formPanel.add(btnPanel, BorderLayout.SOUTH);
        
        // Panel que centra el formulario
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrapperPanel.setBackground(Color.WHITE);
        wrapperPanel.add(formPanel);
        
        centerWrapper.add(wrapperPanel, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);
    }
}
