class Libro implements Vendible, Calificable {
    private int id;
    private String titulo;
    private String subtitulo;
    private String descripcion;
    private double precio;
    private String imagenPortada;
    private int totalVentas;
    private double calificacionPromedio;
    private int totalResenas;
    private String autorNombre;
    private int autorId;
    
    public Libro(int id, String titulo, String subtitulo, String descripcion, 
                 double precio, String imagenPortada, int totalVentas, 
                 double calificacionPromedio, int totalResenas, String autorNombre, int autorId) {
        this.id = id;
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagenPortada = imagenPortada;
        this.totalVentas = totalVentas;
        this.calificacionPromedio = calificacionPromedio;
        this.totalResenas = totalResenas;
        this.autorNombre = autorNombre;
        this.autorId = autorId;
    }
    
    @Override
    public double calcularPrecioFinal() {
        return precio;
    }
    
    @Override
    public boolean estaDisponible() {
        return true;
    }
    
    @Override
    public void agregarCalificacion(int calificacion, String comentario) {
        // Implementado en la interfaz gráfica
    }
    
    @Override
    public double obtenerCalificacionPromedio() {
        return calificacionPromedio;
    }
    
    // Getters
    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getSubtitulo() { return subtitulo; }
    public String getDescripcion() { return descripcion; }
    public double getPrecio() { return precio; }
    public String getImagenPortada() { return imagenPortada; }
    public int getTotalVentas() { return totalVentas; }
    public double getCalificacionPromedio() { return calificacionPromedio; }
    public int getTotalResenas() { return totalResenas; }
    public String getAutorNombre() { return autorNombre; }
    public int getAutorId() { return autorId; }
}