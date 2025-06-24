package biblioteca;

public class Validador {

    // válida que no estén vacíos los campos.
    public static boolean estaVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    // Solo permite RUT válidos.
    public static boolean esRutValido(String rut) {
        if (rut == null) return false;
        return rut.matches("^\\d{1,2}\\.\\d{3}\\.\\d{3}-[\\dkK]$");
    }
}
