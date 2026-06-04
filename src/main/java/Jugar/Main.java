package jugar;

import GamePane.GamePanel;
import vista.VistaCRUD;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Abrimos el CRUD de control (Obligatorio en indicaciones)
            VistaCRUD panelAdministrador = new VistaCRUD();
            panelAdministrador.setVisible(true);

            // 2. Iniciamos la ventana principal del juego con tu nuevo mapa
            JFrame ventanaJuego = new JFrame("Tower Defense Kitty - Edición Especial");
            GamePanel juegoPanel = new GamePanel();

            ventanaJuego.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventanaJuego.add(juegoPanel);
            ventanaJuego.pack();
            ventanaJuego.setLocationRelativeTo(null);
            ventanaJuego.setVisible(true);
        });
    }
}
