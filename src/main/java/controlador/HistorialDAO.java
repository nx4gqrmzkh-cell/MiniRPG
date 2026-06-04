package controlador;

import java.sql.*;

public class HistorialDAO {

    private static final String URL = "jdbc:mysql://localhost/juego";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public boolean guardar(int ronda, int dinero, int vidas) {
        String sql = "INSERT INTO partidas_historial "
                + "(ronda_alcanzada, dinero_final, vidas_restantes) "
                + "VALUES (?, ?, ?)";
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ronda);
            ps.setInt(2, dinero);
            ps.setInt(3, vidas);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar historial: " + e.getMessage());
            return false;
        }
    }
}
