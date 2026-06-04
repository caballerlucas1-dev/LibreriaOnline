import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

class VentasPanel extends JPanel {
    public VentasPanel(Autor autor) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("Informe de Ventas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setText(autor.generarReporte());
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(new EmptyBorder(20, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);
    }
}