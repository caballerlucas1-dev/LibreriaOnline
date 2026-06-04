import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

class CrearLibroPanel extends JPanel {
    private Autor autor;
    private AutorPanel parentFrame;
    private JTextField tituloField, subtituloField, precioField;
    private JTextArea descripcionArea;
    private JLabel imagenPreviewLabel;
    private String imagenPath = null;
    
    public CrearLibroPanel(Autor autor, AutorPanel parentFrame) {
        this.autor = autor;
        this.parentFrame = parentFrame;
        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("Publicar Nuevo Libro");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Título
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Título:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        tituloField = new JTextField(30);
        formPanel.add(tituloField, gbc);
        
        // Subtítulo
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Subtítulo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        subtituloField = new JTextField(30);
        formPanel.add(subtituloField, gbc);
        
        // Precio
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Precio ($):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        precioField = new JTextField(10);
        formPanel.add(precioField, gbc);
        
        // Descripción
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        descripcionArea = new JTextArea(6, 30);
        descripcionArea.setLineWrap(true);
        descripcionArea.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(descripcionArea);
        formPanel.add(scrollDesc, gbc);
        
        // Imagen
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(new JLabel("Portada:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        
        JPanel imagenPanel = new JPanel(new BorderLayout(10, 10));
        imagenPanel.setBackground(Color.WHITE);
        
        imagenPreviewLabel = new JLabel("Sin imagen", SwingConstants.CENTER);
        imagenPreviewLabel.setPreferredSize(new Dimension(120, 160));
        imagenPreviewLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        imagenPreviewLabel.setOpaque(true);
        imagenPreviewLabel.setBackground(Color.LIGHT_GRAY);
        imagenPanel.add(imagenPreviewLabel, BorderLayout.WEST);
        
        JButton selectImageBtn = new JButton("Seleccionar Imagen");
        selectImageBtn.addActionListener(e -> seleccionarImagen());
        imagenPanel.add(selectImageBtn, BorderLayout.CENTER);
        
        formPanel.add(imagenPanel, gbc);
        
        // Botón publicar
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        JButton publicarBtn = new JButton("Publicar Libro");
        publicarBtn.setBackground(new Color(255, 153, 0));
        publicarBtn.setForeground(Color.BLACK);
        publicarBtn.setFont(new Font("Arial", Font.BOLD, 14));
        publicarBtn.setPreferredSize(new Dimension(150, 40));
        publicarBtn.addActionListener(e -> publicarLibro());
        formPanel.add(publicarBtn, gbc);
        
        add(formPanel, BorderLayout.CENTER);
    }
    
    private void seleccionarImagen() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "jpeg", "png", "gif"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            imagenPath = selectedFile.getName();
            
            // Copiar imagen a carpeta portadas
            try {
                File destDir = new File("portadas");
                if (!destDir.exists()) {
                    destDir.mkdir();
                }
                
                File destFile = new File(destDir, imagenPath);
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                // Mostrar preview
                ImageIcon icon = new ImageIcon(destFile.getAbsolutePath());
                Image img = icon.getImage().getScaledInstance(120, 160, Image.SCALE_SMOOTH);
                imagenPreviewLabel.setIcon(new ImageIcon(img));
                imagenPreviewLabel.setText("");
                
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al copiar imagen: " + ex.getMessage());
            }
        }
    }
    
    private void publicarLibro() {
        String titulo = tituloField.getText().trim();
        String subtitulo = subtituloField.getText().trim();
        String descripcion = descripcionArea.getText().trim();
        String precioStr = precioField.getText().trim();
        
        if (titulo.isEmpty() || precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete los campos obligatorios (título y precio)");
            return;
        }
        
        try {
            double precio = Double.parseDouble(precioStr);
            
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO libros (titulo, subtitulo, descripcion, precio, imagen_portada, autor_id, total_ventas, calificacion_promedio, total_resenas, fecha_publicacion) " +
                            "VALUES (?, ?, ?, ?, ?, ?, 0, 0.0, 0, NOW())";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, titulo);
                stmt.setString(2, subtitulo);
                stmt.setString(3, descripcion);
                stmt.setDouble(4, precio);
                stmt.setString(5, imagenPath);
                stmt.setInt(6, autor.getAutorId());
                stmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "¡Libro publicado exitosamente!");
                
                // Limpiar formulario
                tituloField.setText("");
                subtituloField.setText("");
                descripcionArea.setText("");
                precioField.setText("");
                imagenPath = null;
                imagenPreviewLabel.setIcon(null);
                imagenPreviewLabel.setText("Sin imagen");
                
                parentFrame.refresh();
                
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al publicar: " + e.getMessage());
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio inválido");
        }
    }
}
