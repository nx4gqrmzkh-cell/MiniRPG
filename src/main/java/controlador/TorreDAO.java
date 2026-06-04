package controlador;

import Monos.Tower;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TorreDAO {

    private static final String URL = "jdbc:mysql://localhost/juego";
    private static final String USER = "root";
    private static final String PASSWORD = "";

// 1. Modifica el método crear para que reciba la Torre directamente
    public boolean crear(Tower t) {
        String sql = "INSERT INTO torres (nombre, rango, costo, daño, cadencia_fuego, color_rgb) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getName());
            ps.setInt(2, t.getRange());
            ps.setInt(3, t.getCost());
            ps.setInt(4, t.getDamage());
            // Nota: Añade un getter getDamage() y getFireRate() en tu clase Tower si te faltan
            ps.setLong(5, t.getFireRate());

            // Convertir color a string RGB
            String rgb = t.getColor().getRed() + "," + t.getColor().getGreen() + "," + t.getColor().getBlue();
            ps.setString(6, rgb);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar torre: " + e.getMessage());
            return false;
        }
    }

// 2. Cambia el nombre de leerTodas() a leerTodos() para que coincida con la Vista
    public List<Object[]> leerTodos() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT * FROM torres";
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getInt("rango"),
                    rs.getInt("costo"),
                    rs.getInt("daño"),
                    rs.getLong("cadencia_fuego")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al leer catálogo: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(int id, int rango, int costo, int danio, long cadencia) {
        String sql = "UPDATE torres SET rango = ?, costo = ?, daño = ?, cadencia_fuego = ? WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, rango);
            ps.setInt(2, costo);
            ps.setInt(3, danio);
            ps.setLong(4, cadencia);
            ps.setInt(5, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar torre: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM torres WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al borrar torre: " + e.getMessage());
            return false;
        }
    }
}
