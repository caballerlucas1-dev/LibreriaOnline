import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

class LectorPanel extends JFrame {
    private Lector lector;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton carritoBtn;
    
    public LectorPanel(Lector lector) {
        this.lector = lector;
        setTitle("Amazon KDP - Tienda de Libros");
        setSize(1200, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(35, 47, 62));
        topPanel.setPreferredSize(new Dimension(1200, 80));
        
        JLabel logoLabel = new JLabel("  amazon");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 28));
        logoLabel.setForeground(Color.BLACK);
        topPanel.add(logoLabel, BorderLayout.WEST);
        
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        JLabel userLabel = new JLabel("Hola, " + lector.getNombre() + "  ");
        userLabel.setForeground(Color.BLACK);
        userPanel.add(userLabel);
        
        carritoBtn = new JButton("🛒 Carrito (" + lector.getCarrito().getLibros().size() + ")");
        carritoBtn.setBackground(new Color(255, 153, 0));
        carritoBtn.addActionListener(e -> mostrarCarrito());
        userPanel.add(carritoBtn);
        
        topPanel.add(userPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        // Menú lateral
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(52, 73, 94));
        menuPanel.setPreferredSize(new Dimension(200, 650));
        
        addMenuButton(menuPanel, "Todos los Libros", "todos");
        addMenuButton(menuPanel, "Más Vendidos", "top");
        addMenuButton(menuPanel, "Mis Compras", "compras");
        
        add(menuPanel, BorderLayout.WEST);
        
        // Panel de contenido
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.BLACK);
        
        contentPanel.add(new CatalogoLibrosPanel(lector, this), "todos");
        contentPanel.add(new TopLibrosLectorPanel(lector, this), "top");
        contentPanel.add(new MisComprasPanel(lector), "compras");
        
        add(contentPanel, BorderLayout.CENTER);
        
        cardLayout.show(contentPanel, "todos");
    }
    
    private void addMenuButton(JPanel menu, String text, String panelName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 50));
        btn.setBackground(new Color(52, 73, 94));
        btn.setForeground(Color.BLACK);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> cardLayout.show(contentPanel, panelName));
        menu.add(btn);
        menu.add(Box.createVerticalStrut(5));
    }
    
    private void mostrarCarrito() {
        new CarritoDialog(lector, this).setVisible(true);
    }
    
    public void actualizarCarrito() {
        carritoBtn.setText("🛒 Carrito (" + lector.getCarrito().getLibros().size() + ")");
        revalidate();
        repaint();
    }
    
    public void refrescarContenido() {
        contentPanel.removeAll();
        contentPanel.add(new CatalogoLibrosPanel(lector, this), "todos");
        contentPanel.add(new TopLibrosLectorPanel(lector, this), "top");
        contentPanel.add(new MisComprasPanel(lector), "compras");
        cardLayout.show(contentPanel, "compras");
        revalidate();
        repaint();
    }
}