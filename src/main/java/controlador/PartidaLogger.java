package controlador;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PartidaLogger {

    private static final String RUTA = "historial_partidas.txt";
    private static final DateTimeFormatter FMT
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void guardarPartida(int ronda, int dinero, int vidas) {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(RUTA, true))) {          // true = append
            String linea = LocalDateTime.now().format(FMT)
                    + " | Ronda: " + ronda
                    + " | Dinero: " + dinero
                    + " | Vidas: " + vidas;
            bw.write(linea);
            bw.newLine();
            System.out.println("[Log] Partida guardada: " + linea);
        } catch (IOException e) {
            System.err.println("[Log] Error al guardar partida: " + e.getMessage());
        }
    }

    public static List<String> leerHistorial() {
        List<String> historial = new ArrayList<>();
        File f = new File(RUTA);
        if (!f.exists()) {
            return historial;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                historial.add(linea);
            }
        } catch (IOException e) {
            System.err.println("[Log] Error al leer historial: " + e.getMessage());
        }
        return historial;
    }
}
