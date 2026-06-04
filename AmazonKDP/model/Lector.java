class Lector extends Usuario {
    private Carrito carrito;
    
    public Lector(int id, String nombre, String email, String password) {
        super(id, nombre, email, password, "LECTOR");
        this.carrito = new Carrito(id);
    }
    
    public Carrito getCarrito() { return carrito; }
    
    @Override
    public void mostrarPanel() {
        new LectorPanel(this).setVisible(true);
    }
}