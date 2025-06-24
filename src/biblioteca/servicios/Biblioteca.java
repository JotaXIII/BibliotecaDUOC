package biblioteca;

import biblioteca.excepciones.LibroNoEncontradoException;
import biblioteca.excepciones.LibroYaPrestadoException;
import biblioteca.modelos.Libro;
import biblioteca.modelos.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Justificación
 * - ArrayList<Libro>: para almacenar la lista de libros en orden de carga
 * - HashMap<String, Usuario>: para acceso a usuarios por su identificador (Clave = RUT).
 * - HashSet<Libro>: para manejar rápidamente el conjunto de libros prestados.
 * - TreeSet<Libro>: para mantener un catálogo de libros siempre ordenado por título.
 */

public class Biblioteca {

    // Uso de ArrayList para almacenar la lista de libros en orden de carga,
    // permitiendo recorrerlos en orden y accederlos por índice si es necesario.
    private ArrayList<Libro> listaLibros = new ArrayList<>();

    // Uso de HashMap para almacenar los usuarios (RUT clave),
    // permitiendo buscar, insertar y elimiar por identificador único.
    private HashMap<String, Usuario> usuarios = new HashMap<>();

    // Uso de HashSet para los libros prestados,
    // permite comprobar y actualizar el estado del libro, y evitar duplicidad.
    private HashSet<Libro> librosPrestados = new HashSet<>();

    // Uso de TreeSet para mantener el catàlogo ordenado alfabéticamente (título).
    private TreeSet<Libro> Catalogo = new TreeSet<>(Comparator.comparing(Libro::getTitulo));

    // Contador autoincremental para asignar ID único a al libro al agregarlo.
    private int proximoIdLibro = 1;

    // Carga .csv y actualiza libros
    public void cargarLibrosDesdeCSV(String rutaArchivo) {

        proximoIdLibro = GestionArchivos.cargarLibrosDesdeCSV(rutaArchivo, listaLibros, proximoIdLibro);
        Catalogo.clear();
        Catalogo.addAll(listaLibros);
    }

    // Agregar libro con ID único
    public void agregarLibro(String titulo, String autor) {
        Libro libro = new Libro(proximoIdLibro++, titulo, autor);
        listaLibros.add(libro);
        Catalogo.add(libro);
    }

    // Búsqueda por ID.
    public Libro buscarLibroPorId(int id) {
        for (Libro libro : listaLibros) {
            if (libro.getId() == id) {
                return libro;
            }
        }
        return null;
    }

    // Registro de usuario inexistente.
    public boolean agregarUsuario(Usuario usuario) {
        if (usuarios.containsKey(usuario.getRut())) {
            System.out.println("Usuario ya registrado.");
            return false;
        }
        usuarios.put(usuario.getRut(), usuario);
        return true;
    }

    // Si el RUT no está registrado al momento del préstamo, se puede crear el usuario.
    public void prestarLibro(String rutUsuario, int idLibro, Scanner scanner)
            throws LibroNoEncontradoException, LibroYaPrestadoException {
        Usuario usuario = usuarios.get(rutUsuario);
        if (usuario == null) {
            System.out.println("Usuario no encontrado.");
            System.out.print("¿Desea registrar este usuario? (S/N): ");
            String respuesta = scanner.nextLine().trim().toUpperCase();
            if (respuesta.equals("S")) {
                System.out.print("Ingrese nombre del usuario: ");
                String nombre = scanner.nextLine().trim();
                usuario = new Usuario(rutUsuario, nombre);
                usuarios.put(rutUsuario, usuario);
                System.out.println("Usuario registrado exitosamente.");
            } else {
                System.out.println("No se realizó el préstamo porque no existe el usuario.");
                return;
            }
        }
        Libro libro = buscarLibroPorId(idLibro);
        if (libro == null) throw new LibroNoEncontradoException("Libro no encontrado por ID.");
        if (librosPrestados.contains(libro)) throw new LibroYaPrestadoException("El libro ya está prestado.");
        librosPrestados.add(libro);
        System.out.println("Libro prestado exitosamente a " + usuario.getNombre());
    }

    // Busca si es el estado del libro es prestado.
    public boolean estaPrestado(Libro libro) {
        return librosPrestados.contains(libro);
    }

    // Acceso al catálogo ordenado
    public TreeSet<Libro> getCatalogo() {
        return Catalogo;
    }

    // Detalle y estado de los libros registrados.
    public void mostrarCatalogo() {
        System.out.println("\nCatálogo ordenado por título:");
        System.out.printf("%-5s\t%-30s\t%-25s\t%-12s%n", "ID", "Título", "Autor", "Estado");
        for (Libro libro : Catalogo) {
            String estado = librosPrestados.contains(libro) ? "Prestado" : "Disponible";
            System.out.printf("%-5d\t%-30s\t%-25s\t%-12s%n", libro.getId(), libro.getTitulo(), libro.getAutor(), estado);
        }
    }

    // Lista de usuarios registrados.
    public void mostrarUsuarios() {
        System.out.println("\nUsuarios registrados:");
        for (Usuario usuario : usuarios.values()) {
            System.out.println(usuario);
        }
    }

    // Genera informe (.txt) con los libros prestados y disponibles.
    public void generarInforme(String rutaArchivo) {
        List<String> lineas = new ArrayList<>();
        lineas.add("=== Libros Prestados ===");
        for (Libro libro : Catalogo) {
            if (librosPrestados.contains(libro)) {
                lineas.add(libro.toString());
            }
        }
        lineas.add("\n=== Libros Disponibles ===");
        for (Libro libro : Catalogo) {
            if (!librosPrestados.contains(libro)) {
                lineas.add(libro.toString());
            }
        }
        GestionArchivos.escribirInforme(rutaArchivo, lineas);
    }
}
