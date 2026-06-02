package sql;

// IMPORTANTE: Importamos las clases del paquete Monos para poder usarlas aquí
import Monos.Monos;
import Monos.Dardero;
import Monos.Minigun;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class sql1 {

    // Configuración de la base de datos (Usamos la que creamos: bloons_db)
    private static final String URL = "jdbc:mysql://localhost/juego";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {

        System.out.println("=== 1. CREANDO E INSERTANDO UN MONO EN LA BD ===");
        // Creamos un objeto de tu clase Minigun (recuerda que por defecto cuesta 950 y tiene 10 DPS)
        Minigun miTorreNueva = new Minigun();

        // Ejecutamos el método para guardarlo en la base de datos
        insertarTorreEnBD("Mono Minigun", "Dispara rafagas de dardos a velocidad extrema.", miTorreNueva.getCoste());

        System.out.println("\n=== 2. LEYENDO Y CREANDO OBJETOS JAVA DESDE LA BD ===");
        // Cargamos las torres desde la base de datos
        cargarTorresDesdeBD();
    }

    /**
     * MÉTODO PARA INSERTAR: Toma los datos de tus objetos Java y los mete al
     * SQL
     */
    public static void insertarTorreEnBD(String nombre, String descripcion, int costo) {
        String sql = "INSERT INTO torres (nombre, descripcion, costo_desbloqueo) VALUES (?, ?, ?)";

        try (Connection conexion = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement psnt = conexion.prepareStatement(sql)) {

            // Pasamos los parámetros de forma segura
            psnt.setString(1, nombre);
            psnt.setString(2, descripcion);
            psnt.setInt(3, costo);

            int filasAfectadas = psnt.executeUpdate(); // executeUpdate se usa para INSERT, UPDATE, DELETE
            if (filasAfectadas > 0) {
                System.out.println("¡" + nombre + " guardado exitosamente en la base de datos!");
            }

        } catch (SQLException e) {
            System.out.println("Error al insertar la torre: " + e.getMessage());
        }
    }

    /**
     * MÉTODO PARA LEER: Trae los datos de SQL y te muestra cómo se
     * instanciarían en Java
     */
    public static void cargarTorresDesdeBD() {
        String sql = "SELECT * FROM torres";

        try (Connection conexion = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement psnt = conexion.prepareStatement(sql); ResultSet rset = psnt.executeQuery()) {

            while (rset.next()) {
                String nombre = rset.getString("nombre");
                int costo = rset.getInt("costo_desbloqueo");

                System.out.println("Cargado de SQL -> Nombre: " + nombre + " | Costo: $" + costo);

                // Aquí es donde ocurre la magia de la conexión:
                // Dependiendo de lo que diga la Base de datos, tú crearías el objeto en Java
                if (nombre.contains("Dardos")) {
                    Dardero d = new Dardero(costo, 1); // Instancia tu clase Dardero
                    System.out.println("   [Sistema] Objeto Dardero creado en memoria de Java.");
                } else if (nombre.contains("Minigun")) {
                    Minigun m = new Minigun(); // Instancia tu clase Minigun
                    System.out.println("   [Sistema] Objeto Minigun creado en memoria de Java.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al cargar las torres: " + e.getMessage());
        }
    }
}
