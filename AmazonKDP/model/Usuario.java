class Usuario extends AmazonKDP {
    protected String password;
    protected String tipoUsuario;
    
    public Usuario(int id, String nombre, String email, String password, String tipoUsuario) {
        super(id, nombre, email);
        this.password = password;
        this.tipoUsuario = tipoUsuario;
    }
    
    @Override
    public void mostrarPanel() {
        System.out.println("Panel de usuario: " + nombre);
    }
    
    @Override
    public String getTipoUsuario() {
        return tipoUsuario;
    }
}
