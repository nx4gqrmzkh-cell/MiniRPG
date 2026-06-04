package GamePane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// Importaciones necesarias de tus otros paquetes
import globos.Bloon;
import Monos.Tower;
import Monos.TowerFactory;
import Monos.Projectile;
import Panel.Path;
import Oleadas.WaveManager;
import controlador.TorreDAO; // Importamos el nuevo controlador del proyecto

public class GamePanel extends JPanel implements Runnable {

    // Constantes de dimensiones
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 640;

    // Variables de estado y UI (Mantén las tuyas originales debajo)
    private WaveManager waveManager;
    private Path path;
    private Point mousePos;
    private Tower selectedTowerType;
    private List<Bloon> bloons = new ArrayList<>();
    private List<Tower> towers = new ArrayList<>();
    private List<Projectile> projectiles = new ArrayList<>();
    private int round = 1;

    // CONSTRUCTOR CORREGIDO: Mismo nombre de la clase
    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(34, 139, 34)); // Verde césped
        setFocusable(true);

        // === VINCULACIÓN CON LA BASE DE DATOS (MÉTODO SEGURO) ===
        System.out.println("[DB Inicialización] Sincronizando datos desde la BBDD del proyecto...");
        try {
            TorreDAO controladorBBDD = new TorreDAO();
            // Esto lee todas las torres guardadas en tu MySQL para verificar la conexión
            List<Object[]> datosTorres = controladorBBDD.leerTodas();
            System.out.println("[DB Inicialización] Sincronización exitosa. Torres en catálogo: " + datosTorres.size());
        } catch (Exception e) {
            System.out.println("[DB Aviso] No se pudo conectar a MySQL. Usando valores predeterminados de Java.");
        }
        // ========================================================

        initPath();
        waveManager = new WaveManager(this);

        // Eventos del Mouse y Teclado corregidos
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePos = e.getPoint();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });
    }

    // Métodos obligatorios que usa tu juego (Mantén tu lógica original de aquí hacia abajo)
    private void initPath() {
        path = new Path();
        // Tus puntos del camino...
    }

    private void handleClick(int x, int y) {
        // Tu lógica de clic...
    }

    private void handleKeyPress(int keyCode) {
        // Tu lógica de teclado...
    }

    @Override
    public void run() {
        // Tu bucle principal de juego...
    }
}
