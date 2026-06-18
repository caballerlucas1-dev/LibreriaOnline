import javax.swing.*;
//hola
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Realizado");
            new LoginFrame().setVisible(true);
        });
    }
}