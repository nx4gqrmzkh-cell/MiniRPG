package globos; // O el nombre de tu paquete

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Globos {

    public static void main(String[] args) {

        String cadcon = "jdbc:mysql://localhost:3306/bloons_db";
        String user = "root";
        String password = "";
        String sql = "SELECT * FROM torres";

        try {
            Connection conexion = DriverManager.getConnection(cadcon, user, password);
            PreparedStatement psnt = conexion.prepareStatement(sql);
            ResultSet rset = psnt.executeQuery();

            while (rset.next()) {
                System.out.println(rset.getString("nombre"));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

    } 

}
