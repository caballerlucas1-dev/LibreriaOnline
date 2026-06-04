interface Vendible {
    double calcularPrecioFinal();
    boolean estaDisponible();
}

interface Calificable {
    void agregarCalificacion(int calificacion, String comentario);
    double obtenerCalificacionPromedio();
}

interface Reportable {
    String generarReporte();
}