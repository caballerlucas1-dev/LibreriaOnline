import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.border.*;

class CheckoutDialog extends JDialog {
    private JComboBox<String> metodoPagoCombo;
    
    public CheckoutDialog(Lector lector, LectorPanel parentFrame) {
        super(parentFrame, "Finalizar Compra", true);
        setSize(500, 400);
        setLocationRelativeTo(parentFrame);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Finalizar Compra");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        formPanel.setBackground(Color.WHITE);
        
        double total = lector.getCarrito().calcularTotal();
        JLabel totalLabel = new JLabel("Total a pagar: $" + String.format("%.2f", total));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        formPanel.add(totalLabel);
        
        formPanel.add(new JLabel("Método de pago:"));
        metodoPagoCombo = new JComboBox<>(new String[]{
            "MercadoPago", "Tarjeta de Crédito", "Tarjeta de Débito", "Transferencia"
        });
        formPanel.add(metodoPagoCombo);
        
        JButton confirmarBtn = new JButton("Confirmar Compra");
        confirmarBtn.setBackground(new Color(0, 166, 90));
        confirmarBtn.setForeground(Color.WHITE);
        confirmarBtn.setFont(new Font("Arial", Font.BOLD, 14));
        confirmarBtn.addActionListener(e -> {
            procesarCompra(lector, parentFrame);
        });
        formPanel.add(confirmarBtn);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private void procesarCompra(Lector lector, LectorPanel parentFrame) {
        String metodoPago = (String) metodoPagoCombo.getSelectedItem();
        List<Libro> libros = lector.getCarrito().getLibros();
        
        boolean exito = true;
        for (Libro libro : libros) {
            Venta venta = new Venta(libro, lector, metodoPago);
            if (!venta.procesar()) {
                exito = false;
                break;
            }
        }
        
        if (exito) {
            lector.getCarrito().vaciar();
            JOptionPane.showMessageDialog(this, 
                "¡Compra realizada con éxito!\nGracias por tu compra.", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
            parentFrame.actualizarCarrito();
            parentFrame.refrescarContenido();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error al procesar la compra", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}