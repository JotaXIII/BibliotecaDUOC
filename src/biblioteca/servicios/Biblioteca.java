package biblioteca.servicios;

import biblioteca.excepciones.LibroNoEncontradoException;
import biblioteca.excepciones.LibroYaPrestadoException;
import biblioteca.modelos.Libro;
import biblioteca.modelos.Usuario;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Biblioteca {
    private ArrayList<Libro> libros;
    private HashMap<String, Usuario> usuarios;
    private HashMap<String, List<String>> historialPrestamos;

    public Biblioteca() {
        libros = new ArrayList<>();
        usuarios = new HashMap<>();
        historialPrestamos = new HashMap<>();
    }

    //try para manejo de excepciones al leer archivo
    public void cargarLibrosDesdeCSV(String ruta) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(ruta), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length >= 2) {
                    libros.add(new Libro(partes[0], partes[1]));
                }
            }
        }
    }

    public void guardarEstadoEnArchivo(String ruta) throws IOException {
        try (FileWriter fw = new FileWriter(ruta)) {
            for (Libro libro : libros) {
                fw.write(libro.toString() + "\n");
            }
        }
    }

    public Libro buscarLibro(String titulo) throws LibroNoEncontradoException {
        for (Libro libro : libros) {
            if (libro.getTitulo().equalsIgnoreCase(titulo)) {
                return libro;
            }
        }
        // Excepción si no se encuentra
        throw new LibroNoEncontradoException("El libro '" + titulo + "' no fue encontrado.");
    }

    public void prestarLibro(String titulo, String rutUsuario) throws LibroNoEncontradoException, LibroYaPrestadoException {
        Libro libro = buscarLibro(titulo);
        if (libro.isPrestado()) {
            throw new LibroYaPrestadoException("El libro ya se encuentra prestado.");
        }
        libro.prestar();
        historialPrestamos.putIfAbsent(rutUsuario, new ArrayList<>());
        historialPrestamos.get(rutUsuario).add("EN POSESIÓN: " + titulo);
    }

    public void devolverLibro(String titulo, String rutUsuario) throws LibroNoEncontradoException {
        Libro libro = buscarLibro(titulo);
        libro.devolver();
        historialPrestamos.putIfAbsent(rutUsuario, new ArrayList<>());
        historialPrestamos.get(rutUsuario).add("DEVUELTO: " + titulo);
    }

    public void agregarUsuario(Usuario usuario) {
        //Validación excepciones
        if (!esNombreValido(usuario.getNombre())) {
            System.out.println("Nombre inválido.");
            return;
        }
        if (!esRutValido(usuario.getRut())) {
            System.out.println("RUT inválido.");
            return;
        }
        if (usuarios.containsKey(usuario.getRut())) {
            System.out.println("Ya existe un usuario con ese RUT.");
            return;
        }
        usuarios.put(usuario.getRut(), usuario);
        System.out.println("Usuario agregado correctamente.");
    }

    public void mostrarTodosLosLibros() {
        for (Libro libro : libros) {
            System.out.println(libro);
        }
    }

    public void mostrarHistorialUsuario(String rut) {
        Usuario usuario = usuarios.get(rut);
        if (usuario == null) {
            System.out.println("Usuario no encontrado.");
            return;
        }
        System.out.println("Nombre: " + usuario.getNombre());
        System.out.println("RUT: " + usuario.getRut());

        List<String> historial = historialPrestamos.get(rut);
        if (historial == null || historial.isEmpty()) {
            System.out.println("Este usuario no tiene historial de préstamos.");
        } else {
            System.out.println("Historial de préstamos:");
            for (String entrada : historial) {
                System.out.println("- " + entrada);
            }
        }
    }

    //Validaciones
    private boolean esRutValido(String rut) {
        String regex = "^\\d{1,2}\\.\\d{3}\\.\\d{3}-[\\dkK]$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(rut);
        return matcher.matches();
    }

    private boolean esNombreValido(String nombre) {
        return nombre != null && !nombre.trim().isEmpty();
    }
}
