import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.border.*;

class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> tipoCombo;
    
    public LoginFrame() {
        setTitle("Amazon KDP - Iniciar Sesión");
        setSize(500, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(35, 47, 62));
        headerPanel.setPreferredSize(new Dimension(500, 100));
        JLabel logoLabel = new JLabel("amazon kdp");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 32));
        logoLabel.setForeground(Color.WHITE);
        headerPanel.add(logoLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(40, 60, 40, 60));
        formPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Iniciar Sesión");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(30));
        
        // Tipo de usuario
        JLabel tipoLabel = new JLabel("Tipo de Usuario:");
        tipoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(tipoLabel);
        formPanel.add(Box.createVerticalStrut(5));
        
        tipoCombo = new JComboBox<>(new String[]{"LECTOR", "AUTOR"});
        tipoCombo.setMaximumSize(new Dimension(400, 35));
        tipoCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(tipoCombo);
        formPanel.add(Box.createVerticalStrut(15));
        
        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(emailLabel);
        formPanel.add(Box.createVerticalStrut(5));
        
        emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(400, 35));
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(15));
        
        // Password
        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(5));
        
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(400, 35));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(25));
        
        // Login Button
        JButton loginBtn = new JButton("Iniciar Sesión");
        loginBtn.setMaximumSize(new Dimension(400, 45));
        loginBtn.setFont(new Font("Arial", Font.BOLD, 16));
        loginBtn.setBackground(new Color(255, 153, 0));
        loginBtn.setForeground(Color.BLACK);
        loginBtn.setFocusPainted(false);
        loginBtn.addActionListener(e -> login());
        formPanel.add(loginBtn);
        formPanel.add(Box.createVerticalStrut(15));
        
        // Register Button
        JButton registerBtn = new JButton("Crear Cuenta");
        registerBtn.setMaximumSize(new Dimension(400, 45));
        registerBtn.setFont(new Font("Arial", Font.BOLD, 16));
        registerBtn.setBackground(new Color(52, 73, 94));
        registerBtn.setForeground(Color.BLACK);
        registerBtn.setFocusPainted(false);
        registerBtn.addActionListener(e -> mostrarRegistro());
        formPanel.add(registerBtn);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String tipo = (String) tipoCombo.getSelectedItem();
        
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos");
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT u.*, a.biografia, a.total_ganancias, a.id as autor_id " +
                        "FROM usuarios u " +
                        "LEFT JOIN autores a ON u.id = a.usuario_id " +
                        "WHERE u.email = ? AND u.password = ? AND u.tipo_usuario = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.setString(3, tipo);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                if (tipo.equals("AUTOR")) {
                    Autor autor = new Autor(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("biografia"),
                        rs.getDouble("total_ganancias"),
                        rs.getInt("autor_id")
                    );
                    dispose();
                    autor.mostrarPanel();
                } else {
                    Lector lector = new Lector(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("password")
                    );
                    dispose();
                    lector.mostrarPanel();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error de conexión: " + e.getMessage());
        }
    }
    
    private void mostrarRegistro() {
        new RegistroFrame().setVisible(true);
        dispose();
    }
}

class RegistroFrame extends JFrame {
    private JTextField nombreField, emailField;
    private JPasswordField passwordField;
    private JComboBox<String> tipoCombo;
    
    public RegistroFrame() {
        setTitle("Amazon KDP - Registro");
        setSize(500, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(35, 47, 62));
        headerPanel.setPreferredSize(new Dimension(500, 100));
        JLabel logoLabel = new JLabel("amazon kdp");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 32));
        logoLabel.setForeground(Color.WHITE);
        headerPanel.add(logoLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(40, 60, 40, 60));
        formPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Crear Cuenta");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(30));
        
        // Tipo
        formPanel.add(new JLabel("Tipo de Usuario:"));
        formPanel.add(Box.createVerticalStrut(5));
        tipoCombo = new JComboBox<>(new String[]{"LECTOR", "AUTOR"});
        tipoCombo.setMaximumSize(new Dimension(400, 35));
        formPanel.add(tipoCombo);
        formPanel.add(Box.createVerticalStrut(15));
        
        // Nombre
        formPanel.add(new JLabel("Nombre:"));
        formPanel.add(Box.createVerticalStrut(5));
        nombreField = new JTextField();
        nombreField.setMaximumSize(new Dimension(400, 35));
        formPanel.add(nombreField);
        formPanel.add(Box.createVerticalStrut(15));
        
        // Email
        formPanel.add(new JLabel("Email:"));
        formPanel.add(Box.createVerticalStrut(5));
        emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(400, 35));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(15));
        
        // Password
        formPanel.add(new JLabel("Contraseña:"));
        formPanel.add(Box.createVerticalStrut(5));
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(400, 35));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(25));
        
        // Botones
        JButton registerBtn = new JButton("Registrarse");
        registerBtn.setMaximumSize(new Dimension(400, 45));
        registerBtn.setBackground(new Color(255, 153, 0));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.addActionListener(e -> registrar());
        formPanel.add(registerBtn);
        formPanel.add(Box.createVerticalStrut(10));
        
        JButton backBtn = new JButton("Volver al Login");
        backBtn.setMaximumSize(new Dimension(400, 45));
        backBtn.setBackground(new Color(52, 73, 94));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        formPanel.add(backBtn);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private void registrar() {
        String tipo = (String) tipoCombo.getSelectedItem();
        String nombre = nombreField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos");
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            // Insertar usuario
            String sqlUsuario = "INSERT INTO usuarios (nombre, email, password, tipo_usuario) VALUES (?, ?, ?, ?)";
            PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS);
            stmtUsuario.setString(1, nombre);
            stmtUsuario.setString(2, email);
            stmtUsuario.setString(3, password);
            stmtUsuario.setString(4, tipo);
            stmtUsuario.executeUpdate();
            
            ResultSet rs = stmtUsuario.getGeneratedKeys();
            if (rs.next()) {
                int usuarioId = rs.getInt(1);
                
                // Si es autor, crear registro en tabla autores
                if (tipo.equals("AUTOR")) {
                    String sqlAutor = "INSERT INTO autores (usuario_id, biografia, total_ganancias) VALUES (?, '', 0.0)";
                    PreparedStatement stmtAutor = conn.prepareStatement(sqlAutor);
                    stmtAutor.setInt(1, usuarioId);
                    stmtAutor.executeUpdate();
                }
            }
            
            conn.commit();
            JOptionPane.showMessageDialog(this, "¡Registro exitoso! Ahora puedes iniciar sesión");
            new LoginFrame().setVisible(true);
            dispose();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al registrar: " + e.getMessage());
        }
    }
}
