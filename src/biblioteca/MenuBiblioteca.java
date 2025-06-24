package biblioteca;

import biblioteca.excepciones.LibroNoEncontradoException;
import biblioteca.excepciones.LibroYaPrestadoException;
import biblioteca.modelos.Libro;
import biblioteca.modelos.Usuario;

import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.List;
import java.util.ArrayList;

public class MenuBiblioteca {
    private Biblioteca biblioteca;
    private Scanner scanner;

    public MenuBiblioteca(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;
        this.scanner = new Scanner(System.in);
    }

    // Menu principal y validacion de seleccion.
    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n--- Menú Biblioteca DUOC UC ---");
            System.out.println("1. Agregar libro");
            System.out.println("2. Registrar usuario");
            System.out.println("3. Prestar libro");
            System.out.println("4. Mostrar catálogo");
            System.out.println("5. Mostrar usuarios");
            System.out.println("6. Generar Informe");
            System.out.println("7. Cargar libros desde archivo");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                opcion = -1;
            }

            switch(opcion) {
                case 1:
                    agregarLibroMenu();
                    break;
                case 2:
                    registrarUsuarioMenu();
                    break;
                case 3:
                    prestarLibroMenu();
                    break;
                case 4:
                    biblioteca.mostrarCatalogo();
                    break;
                case 5:
                    biblioteca.mostrarUsuarios();
                    break;
                case 6:
                    generarInformeMenu();
                    break;
                case 7:
                    cargarLibrosMenu();
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        } while(opcion != 0);
    }

    // Valida que los campos no estén vacios.
    private String solicitarCampoObligatorio(String mensaje) {
        String valor;
        do {
            System.out.print(mensaje);
            valor = scanner.nextLine().trim();
            if (Validador.estaVacio(valor)) {
                System.out.println("El campo no puede estar vacío. Intente de nuevo.");
            }
        } while (Validador.estaVacio(valor));
        return valor;
    }

    // RUT debe cumplir con Validador.
    private String solicitarRutValido(String mensaje) {
        String rut;
        do {
            System.out.print(mensaje);
            rut = scanner.nextLine().trim();
            if (!Validador.esRutValido(rut)) {
                System.out.println("RUT inválido. Formato correcto: 12.345.678-9 o 1.234.567-8");
            }
        } while (!Validador.esRutValido(rut));
        return rut;
    }

    // Ingreso de libros manual.
    private void agregarLibroMenu() {
        String titulo = solicitarCampoObligatorio("Título: ");
        String autor = solicitarCampoObligatorio("Autor: ");
        biblioteca.agregarLibro(titulo, autor);
        System.out.println("Resumen: Libro agregado exitosamente");
        System.out.println("Título: " + titulo + " | Autor: " + autor);
    }

    // Solicita datos del usuario y válida de acuerdo a Validador.
    private void registrarUsuarioMenu() {
        String rut = solicitarRutValido("RUT: ");
        String nombre = solicitarCampoObligatorio("Nombre: ");
        if (biblioteca.agregarUsuario(new Usuario(rut, nombre))) {
            System.out.println("Resumen: Usuario registrado exitosamente");
            System.out.println("RUT: " + rut + " | Nombre: " + nombre);
        }
    }

    // Busqueda por ID, Autor o título.
    private void prestarLibroMenu() {
        System.out.println("¿Desea buscar el libro por ID, Título o Autor?");
        System.out.println("1. ID");
        System.out.println("2. Título");
        System.out.println("3. Autor");
        System.out.print("Seleccione una opción: ");
        String opcion = scanner.nextLine().trim();

        int idSeleccionado = -1;

        switch(opcion) {
            case "1":
                idSeleccionado = buscarLibroPorIdFlujo();
                break;
            case "2":
                idSeleccionado = buscarLibroPorCampoFlujo("titulo");
                break;
            case "3":
                idSeleccionado = buscarLibroPorCampoFlujo("autor");
                break;
            default:
                System.out.println("Opción no válida.");
                return;
        }

        if (idSeleccionado == -1) {
            System.out.println("No se seleccionó ningún libro.");
            return;
        }

        String rut = solicitarRutValido("RUT del usuario: ");
        try {

            biblioteca.prestarLibro(rut, idSeleccionado, scanner);
            System.out.println("Resumen: Préstamo realizado exitosamente.");
            System.out.println("RUT usuario: " + rut + " | ID libro: " + idSeleccionado);
        } catch (LibroNoEncontradoException | LibroYaPrestadoException e) {
            System.out.println(e.getMessage());
        }
    }

    // Busqueda por ID y confirmacion.
    private int buscarLibroPorIdFlujo() {
        System.out.print("Ingrese el ID del libro: ");
        int id = -1;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return -1;
        }
        Libro libro = biblioteca.buscarLibroPorId(id);
        if (libro == null) {
            System.out.println("No se encontró el libro con ese ID.");
            return -1;
        }
        String estado = biblioteca.estaPrestado(libro) ? "Prestado" : "Disponible";
        System.out.println("Libro encontrado:");
        System.out.printf("ID: %d | Título: %s | Autor: %s | Estado: %s%n", libro.getId(), libro.getTitulo(), libro.getAutor(), estado);
        if (biblioteca.estaPrestado(libro)) {
            System.out.println("El libro ya está prestado. No puede seleccionarse.");
            return -1;
        }
        System.out.print("¿Desea continuar con el préstamo de este libro? (S/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        if (confirm.equals("S")) {
            return id;
        }
        return -1;
    }

    // Búsqueda por título o autor y confirmación.
    private int buscarLibroPorCampoFlujo(String campo) {
        System.out.print("Ingrese " + (campo.equals("titulo") ? "el título" : "el autor") + " a buscar: ");
        String valor = scanner.nextLine().trim().toLowerCase();
        List<Libro> disponibles = new ArrayList<>();
        for (Libro libro : biblioteca.getCatalogo()) {
            boolean coincide = campo.equals("titulo")
                    ? libro.getTitulo().toLowerCase().contains(valor)
                    : libro.getAutor().toLowerCase().contains(valor);
            if (coincide && !biblioteca.estaPrestado(libro)) {
                disponibles.add(libro);
            }
        }
        if (disponibles.isEmpty()) {
            System.out.println("No se encontraron libros disponibles con ese " + campo + ".");
            return -1;
        }
        System.out.println("Libros disponibles encontrados:");
        for (int i = 0; i < disponibles.size(); i++) {
            Libro libro = disponibles.get(i);
            System.out.printf("%d. ID: %d | Título: %s | Autor: %s%n", i + 1, libro.getId(), libro.getTitulo(), libro.getAutor());
        }
        System.out.print("Seleccione el número del libro: ");
        int seleccion = -1;
        try {
            seleccion = Integer.parseInt(scanner.nextLine());
            if (seleccion < 1 || seleccion > disponibles.size()) {
                System.out.println("Selección fuera de rango.");
                return -1;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
            return -1;
        }
        Libro seleccionado = disponibles.get(seleccion - 1);
        System.out.printf("Seleccionado: ID: %d | Título: %s | Autor: %s%n", seleccionado.getId(), seleccionado.getTitulo(), seleccionado.getAutor());
        System.out.print("¿Desea continuar con el préstamo de este libro? (S/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        if (confirm.equals("S")) {
            return seleccionado.getId();
        }
        return -1;
    }

    // Ingresar nombre para generar informe
    private void generarInformeMenu() {
        String archivo = solicitarCampoObligatorio("Nombre del archivo para guardar el informe (ejemplo: informe.txt): ");
        biblioteca.generarInforme(archivo);
    }

    // Carga de libros en archivo (.csv)
    private void cargarLibrosMenu() {
        String archivo = solicitarCampoObligatorio("Nombre del archivo CSV (ejemplo: libros.csv): ");
        biblioteca.cargarLibrosDesdeCSV(archivo);
    }
}
