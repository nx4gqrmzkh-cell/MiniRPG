/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Usuario
 */
public class sql1 {

    String cadcon = "jdbc:mysql://localhost/juego";
    String user = "root";
    String password = "";
    Connection conexion = DriverManager.getConnection();
    PreparedStatement psnt = conexion.prepareStatement();
    ResultSet rset = psnt.executeQuery()

}
