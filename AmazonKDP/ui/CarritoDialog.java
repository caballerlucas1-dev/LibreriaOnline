import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.border.*;

class CarritoDialog extends JDialog {
    private Lector lector;
    private LectorPanel parentFrame;
    
    public CarritoDialog(Lector lector, LectorPanel parentFrame) {
        super(parentFrame, "Mi Carrito", true);
        this.lector = lector;
        this.parentFrame = parentFrame;
        
        setSize(700, 600);
        setLocationRelativeTo(parentFrame);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(" Carrito de Compras");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Lista de libros
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Color.WHITE);
        
        List<Libro> libros = lector.getCarrito().getLibros();
        if (libros.isEmpty()) {
            JLabel emptyLabel = new JLabel("El carrito está vacío");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            itemsPanel.add(emptyLabel);
        } else {
            for (Libro libro : libros) {
                JPanel itemPanel = createCarritoItem(libro);
                itemsPanel.add(itemPanel);
                itemsPanel.add(Box.createVerticalStrut(10));
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior con total y botones
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        double total = lector.getCarrito().calcularTotal();
        JLabel totalLabel = new JLabel(String.format("Total: $%.2f", total));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 20));
        bottomPanel.add(totalLabel, BorderLayout.WEST);
        
        JPanel btnsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnsPanel.setBackground(Color.WHITE);
        
        JButton comprarBtn = new JButton("Proceder al Pago");
        comprarBtn.setBackground(new Color(255, 153, 0));
        comprarBtn.setPreferredSize(new Dimension(150, 35));
        comprarBtn.addActionListener(e -> {
            if (!libros.isEmpty()) {
                dispose();
                new CheckoutDialog(lector, parentFrame).setVisible(true);
            }
        });
        btnsPanel.add(comprarBtn);
        
        bottomPanel.add(btnsPanel, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createCarritoItem(Libro libro) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(Color.WHITE);
        
        JLabel imgLabel = new JLabel("📚");
        imgLabel.setFont(new Font("Arial", Font.PLAIN, 40));
        panel.add(imgLabel, BorderLayout.WEST);
        
        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(new JLabel("<html><b>" + libro.getTitulo() + "</b></html>"));
        infoPanel.add(new JLabel("Por: " + libro.getAutorNombre()));
        infoPanel.add(new JLabel("$" + libro.getPrecio()));
        panel.add(infoPanel, BorderLayout.CENTER);
        
        JButton removeBtn = new JButton("Quitar");
        removeBtn.setBackground(Color.RED);
        removeBtn.setForeground(Color.WHITE);
        removeBtn.addActionListener(e -> {
            lector.getCarrito().removerLibro(libro);
            dispose();
            parentFrame.actualizarCarrito();
            new CarritoDialog(lector, parentFrame).setVisible(true);
        });
        panel.add(removeBtn, BorderLayout.EAST);
        
        return panel;
    }
}