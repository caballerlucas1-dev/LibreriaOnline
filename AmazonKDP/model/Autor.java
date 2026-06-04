import java.sql.*;

class Autor extends Usuario implements Reportable {
    private String biografia;
    private double totalGanancias;
    private int autorId;
    
    public Autor(int id, String nombre, String email, String password, String biografia, double totalGanancias, int autorId) {
        super(id, nombre, email, password, "AUTOR");
        this.biografia = biografia;
        this.totalGanancias = totalGanancias;
        this.autorId = autorId;
    }
    
    public int getAutorId() { return autorId; }
    public String getBiografia() { return biografia; }
    public void setBiografia(String biografia) { this.biografia = biografia; }
    public double getTotalGanancias() { return totalGanancias; }
    
    @Override
    public String generarReporte() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("=== REPORTE DE VENTAS ===\n");
        reporte.append("Autor: ").append(nombre).append("\n");
        reporte.append("Ganancias totales: $").append(String.format("%.2f", totalGanancias)).append("\n\n");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT l.titulo, l.precio, l.total_ventas, " +
                        "(l.total_ventas * l.precio * 0.30) as ganancias_libro " +
                        "FROM libros l WHERE l.autor_id = ? ORDER BY l.total_ventas DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, autorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reporte.append("Libro: ").append(rs.getString("titulo")).append("\n");
                reporte.append("  Ventas: ").append(rs.getInt("total_ventas")).append("\n");
                reporte.append("  Precio: $").append(rs.getDouble("precio")).append("\n");
                reporte.append("  Ganancias: $").append(String.format("%.2f", rs.getDouble("ganancias_libro"))).append("\n\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reporte.toString();
    }
    
    @Override
    public void mostrarPanel() {
        new AutorPanel(this).setVisible(true);
    }
}