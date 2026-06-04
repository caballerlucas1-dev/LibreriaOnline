import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.*;
import javax.swing.border.*;

class MisLibrosPanel extends JPanel {
    private Autor autor;
    private AutorPanel parentFrame;
    
    public MisLibrosPanel(Autor autor, AutorPanel parentFrame) {
        this.autor = autor;
        this.parentFrame = parentFrame;
        
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("Mis Libros Publicados");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel librosPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        librosPanel.setBackground(Color.BLACK);
        librosPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM libros WHERE autor_id = ? ORDER BY fecha_publicacion DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, autor.getAutorId());
            ResultSet rs = stmt.executeQuery();
            
            boolean hasLibros = false;
            while (rs.next()) {
                hasLibros = true;
                JPanel libroCard = createLibroCard(rs);
                librosPanel.add(libroCard);
            }
            
            if (!hasLibros) {
                JLabel emptyLabel = new JLabel("Aún no has publicado libros. ¡Comienza ahora!", SwingConstants.CENTER);
                emptyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                emptyLabel.setForeground(Color.GRAY);
                add(emptyLabel, BorderLayout.CENTER);
                return;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JScrollPane scrollPane = new JScrollPane(librosPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createLibroCard(ResultSet rs) throws SQLException {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.BLACK);
        card.setPreferredSize(new Dimension(450, 200));
        
        String titulo = rs.getString("titulo");
        double precio = rs.getDouble("precio");
        int ventas = rs.getInt("total_ventas");
        int libroId = rs.getInt("id");
        String imagenPortada = rs.getString("imagen_portada");
        double rating = rs.getDouble("calificacion_promedio");
        
        // Imagen del libro
        JLabel imgLabel = createImageLabel(imagenPortada);
        card.add(imgLabel, BorderLayout.WEST);
        
        // Información del libro
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.BLACK);
        
        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 16));
        infoPanel.add(tituloLabel);
        
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(new JLabel("💰 Precio: $" + precio));
        infoPanel.add(new JLabel("📊 Ventas: " + ventas));
        infoPanel.add(new JLabel(String.format("⭐ Rating: %.1f", rating)));
        infoPanel.add(new JLabel(String.format("💵 Ganancias: $%.2f", ventas * precio * 0.30)));
        
        infoPanel.add(Box.createVerticalStrut(15));
        
        JButton deleteBtn = new JButton("🗑️ Eliminar");
        deleteBtn.setBackground(Color.RED);
        deleteBtn.setForeground(Color.BLACK);
        deleteBtn.setFocusPainted(false);
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Estás seguro de eliminar este libro?\nEsta acción no se puede deshacer.", 
                "Confirmar Eliminación", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                if (eliminarLibro(libroId)) {
                    parentFrame.refresh();
                }
            }
        });
        infoPanel.add(deleteBtn);
        
        card.add(infoPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JLabel createImageLabel(String imagenPortada) {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(100, 140));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        if (imagenPortada != null && !imagenPortada.isEmpty()) {
            File imgFile = new File("portadas/" + imagenPortada);
            if (imgFile.exists()) {
                try {
                    ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
                    Image img = icon.getImage().getScaledInstance(100, 140, Image.SCALE_SMOOTH);
                    label.setIcon(new ImageIcon(img));
                } catch (Exception e) {
                    label.setText("📚");
                    label.setFont(new Font("Arial", Font.PLAIN, 40));
                }
            } else {
                label.setText("📚");
                label.setFont(new Font("Arial", Font.PLAIN, 40));
            }
        } else {
            label.setText("📚");
            label.setFont(new Font("Arial", Font.PLAIN, 40));
        }
        
        return label;
    }
    
    private boolean eliminarLibro(int libroId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM libros WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, libroId);
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Libro eliminado exitosamente");
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar el libro");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
            return false;
        }
    }
}
