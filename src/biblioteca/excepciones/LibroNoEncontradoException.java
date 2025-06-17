//Excepción para libro inexistente

package biblioteca.excepciones;

public class LibroNoEncontradoException extends Exception {
    public LibroNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
