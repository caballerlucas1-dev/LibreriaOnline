import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

class AutorPanel extends JFrame {
    private Autor autor;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    public AutorPanel(Autor autor) {
        this.autor = autor;
        setTitle("Amazon KDP - Panel de Autor");
        setSize(1200, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(35, 47, 62));
        topPanel.setPreferredSize(new Dimension(1200, 80));
        
        JLabel logoLabel = new JLabel("  amazon kdp");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 28));
        logoLabel.setForeground(Color.WHITE);
        topPanel.add(logoLabel, BorderLayout.WEST);
        
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        JLabel userLabel = new JLabel("Autor: " + autor.getNombre() + "  ");
        userLabel.setForeground(Color.WHITE);
        userPanel.add(userLabel);
        
        JLabel gananciaLabel = new JLabel(String.format("💰 $%.2f  ", autor.getTotalGanancias()));
        gananciaLabel.setForeground(new Color(255, 215, 0));
        gananciaLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userPanel.add(gananciaLabel);
        
        topPanel.add(userPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        // Menú lateral
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(52, 73, 94));
        menuPanel.setPreferredSize(new Dimension(200, 650));
        
        addMenuButton(menuPanel, "Dashboard", "dashboard");
        addMenuButton(menuPanel, "Mis Libros", "libros");
        addMenuButton(menuPanel, "Crear Libro", "crear");
        addMenuButton(menuPanel, "Ventas", "ventas");
        addMenuButton(menuPanel, "Biografía", "biografia");
        
        add(menuPanel, BorderLayout.WEST);
        
        // Panel de contenido
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);
        
        contentPanel.add(new DashboardAutorPanel(autor), "dashboard");
        contentPanel.add(new MisLibrosPanel(autor, this), "libros");
        contentPanel.add(new CrearLibroPanel(autor, this), "crear");
        contentPanel.add(new VentasPanel(autor), "ventas");
        contentPanel.add(new BiografiaPanel(autor), "biografia");
        
        add(contentPanel, BorderLayout.CENTER);
        
        cardLayout.show(contentPanel, "dashboard");
    }
    
    private void addMenuButton(JPanel menu, String text, String panelName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 50));
        btn.setBackground(new Color(52, 73, 94));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> cardLayout.show(contentPanel, panelName));
        menu.add(btn);
        menu.add(Box.createVerticalStrut(5));
    }
    
    public void refresh() {
        contentPanel.removeAll();
        contentPanel.add(new DashboardAutorPanel(autor), "dashboard");
        contentPanel.add(new MisLibrosPanel(autor, this), "libros");
        contentPanel.add(new CrearLibroPanel(autor, this), "crear");
        contentPanel.add(new VentasPanel(autor), "ventas");
        contentPanel.add(new BiografiaPanel(autor), "biografia");
        cardLayout.show(contentPanel, "libros");
        revalidate();
        repaint();
    }
}
