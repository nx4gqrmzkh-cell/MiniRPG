package sql;

import Monos.Tower;
import Monos.TowerFactory;
import globos.Bloon;
import java.awt.Color;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class sql1 {

    private static final String URL = "jdbc:mysql://localhost/juego";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    /**
     * Devuelve las estadísticas de un enemigo directamente de la Base de Datos.
     * Útil si deseas cargar dinámicamente propiedades en el bucle del juego.
     */
    public static Map<String, Object> obtenerEstadisticasBloon(String idTipo) {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT * FROM tipo_bloon WHERE id_tipo = ?";
        
        try (Connection conexion = DriverManager.getConnection(URL, USER, PASSWORD); 
             PreparedStatement psnt = conexion.prepareStatement(sql)) {
            
            psnt.setString(1, idTipo);
            try (ResultSet rset = psnt.executeQuery()) {
                if (rset.next()) {
                    stats.put("layers", rset.getInt("capas"));
                    stats.put("rbe", rset.getInt("rbe"));
                    stats.put("speed", rset.getDouble("velocidad"));
                    stats.put("color", rset.getString("color"));
                    stats.put("imagePath", rset.getString("ruta_imagen"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener datos del Bloon: " + e.getMessage());
        }
        return stats;
    }

    public static void insertarTorreEnBD(Tower t) {
        String sql = "INSERT INTO torres (nombre, rango, costo, daño, cadencia_fuego, color_rgb) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexion = DriverManager.getConnection(URL, USER, PASSWORD); 
             PreparedStatement psnt = conexion.prepareStatement(sql)) {

            psnt.setString(1, t.getName());
            psnt.setInt(2, t.getRange());
            psnt.setInt(3, t.getCost());
            psnt.setInt(4, t.getDamage());
            psnt.setLong(5, t.getFireRate());

            String rgb = t.getColor().getRed() + "," + t.getColor().getGreen() + "," + t.getColor().getBlue();
            psnt.setString(6, rgb);

            psnt.executeUpdate();
            System.out.println("¡Torre '" + t.getName() + "' guardada con éxito en SQL!");

        } catch (SQLException e) {
            System.out.println("Error al guardar la torre: " + e.getMessage());
        }
    }

    public static void cargarTorresDesdeBD() {
        String sql = "SELECT * FROM torres";

        try (Connection conexion = DriverManager.getConnection(URL, USER, PASSWORD); 
             PreparedStatement psnt = conexion.prepareStatement(sql); 
             ResultSet rset = psnt.executeQuery()) {

            while (rset.next()) {
                String nombre = rset.getString("nombre");
                int costo = rset.getInt("costo");
                int rango = rset.getInt("rango");
                int danio = rset.getInt("daño");
                long cadencia = rset.getLong("cadencia_fuego");

                System.out.println("SQL -> Registro: " + nombre + " | Costo: $" + costo + " | Rango: " + rango + " | Daño: " + danio);

                Tower objetoJava = null;
                if (nombre.equalsIgnoreCase("Mono Dardo") || nombre.equalsIgnoreCase("Mono Dardero Tradicional")) {
                    objetoJava = TowerFactory.createDartMonkey();
                } else if (nombre.equalsIgnoreCase("Mono Boomerang")) {
                    objetoJava = TowerFactory.createTackShooter();
                } else if (nombre.equalsIgnoreCase("Mono Militar")) {
                    objetoJava = TowerFactory.createSniper();
                } else if (nombre.equalsIgnoreCase("Super Kitty")) {
                    objetoJava = TowerFactory.createSuperMonkey();
                }

                if (objetoJava != null) {
                    System.out.println("   [Sistema] Objeto '" + objetoJava.getName() + "' verificado y mapeado.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar la tabla de torres: " + e.getMessage());
        }
    }
}