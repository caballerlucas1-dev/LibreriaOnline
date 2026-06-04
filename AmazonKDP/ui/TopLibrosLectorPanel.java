import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.*;
import javax.swing.border.*;

class TopLibrosLectorPanel extends JPanel {
    private Lector lector;
    private LectorPanel parentFrame;
    
    public TopLibrosLectorPanel(Lector lector, LectorPanel parentFrame) {
        this.lector = lector;
        this.parentFrame = parentFrame;
        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("🏆 Libros Más Vendidos");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // Grid de libros
        JPanel librosPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        librosPanel.setBackground(Color.WHITE);
        librosPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT l.*, u.nombre as autor_nombre, a.id as autor_id " +
                        "FROM libros l " +
                        "JOIN autores a ON l.autor_id = a.id " +
                        "JOIN usuarios u ON a.usuario_id = u.id " +
                        "ORDER BY l.total_ventas DESC " +
                        "LIMIT 12";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            int ranking = 1;
            while (rs.next()) {
                Libro libro = new Libro(
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
                
                JPanel libroCard = createLibroCard(libro, ranking++);
                librosPanel.add(libroCard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JScrollPane scrollPane = new JScrollPane(librosPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createLibroCard(Libro libro, int ranking) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(280, 380));
        
        // Badge de ranking
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        JLabel rankLabel = new JLabel("#" + ranking);
        rankLabel.setFont(new Font("Arial", Font.BOLD, 24));
        rankLabel.setForeground(new Color(255, 153, 0));
        headerPanel.add(rankLabel);
        card.add(headerPanel, BorderLayout.NORTH);
        
        // Panel central con imagen e info
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        
        // Imagen del libro
        JLabel imgLabel = createImageLabel(libro.getImagenPortada());
        centerPanel.add(imgLabel, BorderLayout.NORTH);
        
        // Información
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel tituloLabel = new JLabel("<html><b>" + truncate(libro.getTitulo(), 35) + "</b></html>");
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(tituloLabel);
        
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(new JLabel("Por: " + libro.getAutorNombre()));
        infoPanel.add(new JLabel(String.format("%.1f ⭐", libro.getCalificacionPromedio())));
        infoPanel.add(new JLabel("📊 " + libro.getTotalVentas() + " ventas"));
        
        JLabel precioLabel = new JLabel("$" + libro.getPrecio());
        precioLabel.setFont(new Font("Arial", Font.BOLD, 16));
        precioLabel.setForeground(new Color(0, 120, 0));
        infoPanel.add(precioLabel);
        
        infoPanel.add(Box.createVerticalStrut(10));
        
        // Botón
        JButton addBtn = new JButton("🛒 Añadir");
        addBtn.setBackground(new Color(255, 153, 0));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> {
            lector.getCarrito().agregarLibro(libro);
            parentFrame.actualizarCarrito();
            JOptionPane.showMessageDialog(this, "Libro añadido al carrito");
        });
        infoPanel.add(addBtn);
        
        centerPanel.add(infoPanel, BorderLayout.CENTER);
        card.add(centerPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JLabel createImageLabel(String imagenPortada) {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(140, 180));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        if (imagenPortada != null && !imagenPortada.isEmpty()) {
            File imgFile = new File("portadas/" + imagenPortada);
            if (imgFile.exists()) {
                try {
                    ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
                    Image img = icon.getImage().getScaledInstance(140, 180, Image.SCALE_SMOOTH);
                    label.setIcon(new ImageIcon(img));
                } catch (Exception e) {
                    label.setText("📚");
                    label.setFont(new Font("Arial", Font.PLAIN, 50));
                }
            } else {
                label.setText("📚");
                label.setFont(new Font("Arial", Font.PLAIN, 50));
            }
        } else {
            label.setText("📚");
            label.setFont(new Font("Arial", Font.PLAIN, 50));
        }
        
        return label;
    }
    
    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
}
