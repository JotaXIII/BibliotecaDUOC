package biblioteca;

import biblioteca.modelos.Libro;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GestionArchivos {

    // Lee archivo .csv (nombre;autor) y agrega los libros al registro.
    public static int cargarLibrosDesdeCSV(String rutaArchivo, List<Libro> listaLibros, int idInicial) {
        int id = idInicial;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(rutaArchivo), StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] datos = linea.split("[;,]");
                if (datos.length == 2) {
                    Libro libro = new Libro(id++, datos[0].trim(), datos[1].trim());
                    listaLibros.add(libro);
                }
            }
            System.out.println("Libros cargados correctamente desde " + rutaArchivo);
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de libros: " + e.getMessage());
        }
        return id;
    }

    // Genera informe de libros prestados y disponibles.
    public static void escribirInforme(String rutaArchivo, List<String> lineas) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(rutaArchivo), StandardCharsets.UTF_8))) {
            for (String linea : lineas) {
                bw.write(linea);
                bw.newLine();
            }
            System.out.println("Informe generado en " + rutaArchivo);
        } catch (IOException e) {
            System.out.println("Error al generar el informe: " + e.getMessage());
        }
    }
}
