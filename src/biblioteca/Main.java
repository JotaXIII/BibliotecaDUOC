package biblioteca;

public class Main {
    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();
        biblioteca.cargarLibrosDesdeCSV("libros.csv");
        MenuBiblioteca menu = new MenuBiblioteca(biblioteca);
        menu.mostrarMenu();
    }
}
