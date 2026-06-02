package sql;

import Monos.Tower;
import Monos.TowerFactory;
import java.awt.Color;
import java.sql.*;

public class sql1 {

    private static final String URL = "jdbc:mysql://localhost/juego";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        System.out.println("=== 1. REGISTRANDO TORRE EN LA BASE DE DATOS ===");

        // Probamos insertando un aliado usando sus valores dinámicos
        Tower dardo = TowerFactory.createDartMonkey();
        insertarTorreEnBD(dardo);

        System.out.println("\n=== 2. CARGANDO ENTIDADES DESDE LA BASE DE DATOS ===");
        cargarTorresDesdeBD();
    }

    public static void insertarTorreEnBD(Tower t) {
        // Sentencia SQL adaptada para almacenar las propiedades clave de tu juego
        String sql = "INSERT INTO torres (nombre, rango, costo, daño, cadencia_fuego, color_rgb) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexion = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement psnt = conexion.prepareStatement(sql)) {

            psnt.setString(1, t.getName());
            psnt.setInt(2, t.getRange());
            psnt.setInt(3, t.getCost());
            psnt.setInt(4, t.getDamage());
            psnt.setLong(5, t.getFireRate());

            // Convertimos el objeto Color de Java a String legible para la base de datos
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

        try (Connection conexion = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement psnt = conexion.prepareStatement(sql); ResultSet rset = psnt.executeQuery()) {

            while (rset.next()) {
                String nombre = rset.getString("nombre");
                int costo = rset.getInt("costo");
                int rango = rset.getInt("rango");

                System.out.println("SQL -> Registro: " + nombre + " | Costo: $" + costo + " | Rango: " + rango);

                // Mapeo directo de instanciación en memoria de Java usando la factoría corregida
                Tower objetoJava = null;
                if (nombre.equalsIgnoreCase("Mono Dardo")) {
                    objetoJava = TowerFactory.createDartMonkey();
                } else if (nombre.equalsIgnoreCase("Mono Boomerang")) {
                    objetoJava = TowerFactory.createTackShooter();
                } else if (nombre.equalsIgnoreCase("Mono Militar")) {
                    objetoJava = TowerFactory.createSniper();
                } else if (nombre.equalsIgnoreCase("Super Kitty")) {
                    objetoJava = TowerFactory.createSuperMonkey();
                }

                if (objetoJava != null) {
                    System.out.println("   [Sistema] Objeto '" + objetoJava.getName() + "' mapeado en Java.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar la tabla: " + e.getMessage());
        }
    }
}
