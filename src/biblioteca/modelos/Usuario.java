package biblioteca.modelos;

public class Usuario {
    private String rut;
    private String nombre;

    public Usuario(String rut, String nombre) {
        this.rut = rut;
        this.nombre = nombre;
    }

    public String getRut() {
        return rut;
    }
    public String getNombre() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        return rut.equals(usuario.rut);
    }

    @Override
    public int hashCode() {
        return rut.hashCode();
    }

    @Override
    public String toString() {
        return "RUT: " + rut + " | Nombre: " + nombre;
    }
}
