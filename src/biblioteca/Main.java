package biblioteca;

import biblioteca.modelos.*;
import biblioteca.servicios.*;
import biblioteca.excepciones.*;

import java.util.Scanner;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Biblioteca biblioteca = new Biblioteca();

        try {
            // Uso de try para lanzar IOException
            biblioteca.cargarLibrosDesdeCSV("libros.csv");
        } catch (IOException e) {
            System.out.println("No se pudo cargar el archivo de libros: " + e.getMessage());
        }

        boolean salir = false;
        while (!salir) {
            System.out.println("\n--- Menú Biblioteca ---");
            System.out.println("1. Buscar libro");
            System.out.println("2. Prestar libro");
            System.out.println("3. Agregar usuario");
            System.out.println("4. Libros Disponibles");
            System.out.println("5. Historial de usuario");
            System.out.println("6. Devolver libro");
            System.out.println("7. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(sc.nextLine());

                switch (opcion) {
                    case 1:
                        System.out.print("Ingrese título del libro: ");
                        String tituloBuscar = sc.nextLine();
                        try {
                            Libro libro = biblioteca.buscarLibro(tituloBuscar);
                            System.out.println("Encontrado: " + libro);
                        } catch (LibroNoEncontradoException e) {
                            System.out.println(e.getMessage());
                        }
                        break;

                    case 2:
                        System.out.print("Ingrese RUT del usuario: ");
                        String rutPrestar = sc.nextLine();
                        System.out.print("Ingrese título del libro a prestar: ");
                        String tituloPrestar = sc.nextLine();
                        try {
                            biblioteca.prestarLibro(tituloPrestar, rutPrestar);
                            System.out.println("Libro prestado correctamente.");
                        } catch (LibroNoEncontradoException | LibroYaPrestadoException e) {
                            System.out.println(e.getMessage());
                        }
                        break;

                    case 3:
                        System.out.print("Ingrese nombre del usuario: ");
                        String nombre = sc.nextLine();
                        System.out.print("Ingrese RUT del usuario: ");
                        String rut = sc.nextLine();
                        Usuario usuario = new Usuario(nombre, rut);
                        biblioteca.agregarUsuario(usuario);
                        break;

                    case 4:
                        biblioteca.mostrarTodosLosLibros();
                        break;

                    case 5:
                        System.out.print("Ingrese RUT del usuario: ");
                        String rutConsulta = sc.nextLine();
                        biblioteca.mostrarHistorialUsuario(rutConsulta);
                        break;

                    case 6:
                        System.out.print("Ingrese RUT del usuario: ");
                        String rutDevolver = sc.nextLine();
                        System.out.print("Ingrese título del libro a devolver: ");
                        String tituloDevolver = sc.nextLine();
                        try {
                            biblioteca.devolverLibro(tituloDevolver, rutDevolver);
                            System.out.println("Libro devuelto correctamente.");
                        } catch (LibroNoEncontradoException e) { // Paso 3.a
                            System.out.println(e.getMessage());
                        }
                        break;

                    case 7:
                        salir = true;
                        break;

                    default:
                        System.out.println("Opción no válida.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número válido.");
            }
        }

        try {
            biblioteca.guardarEstadoEnArchivo("estado_libros.txt");
        } catch (IOException e) {
            System.out.println("No se pudo guardar el estado de los libros: " + e.getMessage());
        }

        System.out.println("Programa finalizado.");
    }
}
