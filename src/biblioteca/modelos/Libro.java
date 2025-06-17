package biblioteca.modelos;

public class Libro {
    private String titulo;
    private String autor;
    private boolean prestado;

    public Libro(String titulo, String autor) {
        this.titulo = titulo;
        this.autor = autor;
        this.prestado = false;
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public boolean isPrestado() { return prestado; }
    public void setPrestado(boolean prestado) { this.prestado = prestado; }

    public void prestar() { this.prestado = true; }
    public void devolver() { this.prestado = false; }

    @Override
    public String toString() {
        return "Título: " + titulo + ", Autor: " + autor + ", Estado: " + (prestado ? "Prestado" : "Disponible");
    }
}
