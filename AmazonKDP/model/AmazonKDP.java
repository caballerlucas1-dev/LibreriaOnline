abstract class AmazonKDP {
    protected int id;
    protected String nombre;
    protected String email;
    
    public AmazonKDP(int id, String nombre, String email) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
    }
    
    public abstract void mostrarPanel();
    public abstract String getTipoUsuario();
    
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
}
