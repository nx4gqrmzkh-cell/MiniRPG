package GamePane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Importaciones del proyecto
import globos.Bloon;
import Monos.Tower;
import Monos.TowerFactory;
import Monos.Projectile;
import Panel.Path;
import Oleadas.WaveManager;
import controlador.TorreDAO;

public class GamePanel extends JPanel implements Runnable {

    public static final int WIDTH = 1024;
    public static final int HEIGHT = 640;
    private static final int TARGET_FPS = 60;

    private Thread gameThread;
    private boolean running;

    // Estado activo de la partida (Interactivo)
    private int money = 650;
    private int lives = 100;
    private int round = 1;

    // Entidades del mapa
    private List<Bloon> bloons = new ArrayList<>();
    private List<Tower> towers = new ArrayList<>();
    private List<Projectile> projectiles = new ArrayList<>();

    private Path path;
    private WaveManager waveManager;
    private Image mapaFondo;
    private Tower selectedTowerType = null; // Almacena qué mono vas a colocar
    private Point mousePos = new Point(0, 0);

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocusInWindow(); // Obligatorio para recibir eventos de teclado inmediatamente

        // Carga de textura de mapa interactivo
        try {
            mapaFondo = new ImageIcon("mapa.png").getImage();
        } catch (Exception e) {
            System.err.println("Aviso: No se cargó mapa.png, usando respaldo.");
        }

        path = new Path();
        waveManager = new WaveManager(this);

        // ==========================================
        // SISTEMA INTERACTIVO: CAPTURA DE EVENTOS DE MOUSE Y TECLADO
        // ==========================================
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

        // Arrancamos el motor de simulación de forma automática
        start();
    }

    public void start() {
        if (gameThread == null) {
            gameThread = new Thread(this);
            running = true;
            gameThread.start();
        }
    }

    // LÓGICA DE INTERACCIÓN DEL MOUSE AL HACER CLIC
    private void handleClick(int x, int y) {
        if (selectedTowerType != null) {
            // Regla de Oro: No se pueden poner torres encima de la vía gris (grosor de tolerancia 30)
            if (path.isOnPath(x, y, 30)) {
                System.out.println("[Interacción] ¡No puedes colocar una torre sobre el camino!");
                return;
            }

            if (money >= selectedTowerType.getCost()) {
                money -= selectedTowerType.getCost();

                // Creamos un duplicado limpio de la torre seleccionada en la posición del ratón
                Tower nuevaTorre = null;
                String nombre = selectedTowerType.getName();

                if (nombre.equals("Mono Militar")) {
                    nuevaTorre = TowerFactory.createSniper();
                } else if (nombre.equals("Mono Boomerang")) {
                    nuevaTorre = TowerFactory.createTackShooter();
                } else if (nombre.equals("Super Kitty")) {
                    nuevaTorre = TowerFactory.createSuperMonkey();
                } else {
                    nuevaTorre = TowerFactory.createDartMonkey();
                }

                nuevaTorre.setPosition(x, y);
                towers.add(nuevaTorre);

                System.out.println("[Compra] Colocado: " + nombre + " en (" + x + "," + y + ")");

                // Si quieres que tras poner una torre se deseleccione, descomenta la línea siguiente:
                // selectedTowerType = null;
            } else {
                System.out.println("[Interacción] Dinero insuficiente.");
            }
        }
    }

    // CONTROLES DE ENTRADA POR TECLADO
    private void handleKeyPress(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_1:
                selectedTowerType = TowerFactory.createDartMonkey();
                System.out.println("[Selección] Listo para colocar: Mono Dardo ($200)");
                break;
            case KeyEvent.VK_2:
                selectedTowerType = TowerFactory.createTackShooter();
                System.out.println("[Selección] Listo para colocar: Mono Boomerang ($350)");
                break;
            case KeyEvent.VK_3:
                selectedTowerType = TowerFactory.createSniper();
                System.out.println("[Selección] Listo para colocar: Mono Militar ($400)");
                break;
            case KeyEvent.VK_4:
                selectedTowerType = TowerFactory.createSuperMonkey();
                System.out.println("[Selección] Listo para colocar: Super Kitty ($2500)");
                break;
            case KeyEvent.VK_SPACE:
                // Si pulsas Espacio y no hay oleada activa, se inicia la siguiente ronda
                if (bloons.isEmpty()) {
                    System.out.println("[Oleada] Iniciando Ronda: " + round);
                    waveManager.startWave(round);
                    round++;
                }
                break;
            case KeyEvent.VK_ESCAPE:
                selectedTowerType = null;
                System.out.println("[Selección] Cancelada.");
                break;
        }
    }

    // EL MOTOR DE ACTUALIZACIÓN DEL JUEGO (60 FPS)
    @Override
    public void run() {
        long nsPerFrame = 1000000000 / TARGET_FPS;
        long lastTime = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            long elapsed = now - lastTime;

            if (elapsed >= nsPerFrame) {
                updateGame();
                repaint(); // Solicita repintar la pantalla
                lastTime = now - (elapsed % nsPerFrame);
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateGame() {
        // 1. Ejecutar eventos del gestor de oleadas
        waveManager.update();

        // 2. Ciclo de las torres: buscan enemigos en su rango dinámico de SQL y disparan
        for (Tower t : towers) {
            t.update(bloons, projectiles);
        }

        // 3. Procesar proyectiles en movimiento
        Iterator<Projectile> pIt = projectiles.iterator();
        while (pIt.hasNext()) {
            Projectile p = pIt.next();
            p.update();
            // Si el proyectil impactó o salió de los límites configurados en Projectile.java se remueve
            // Nota: Verifica que en tu clase Projectile tengas implementado un método public boolean isExpired()
        }

        // 4. Ciclo de vida dinámico de los globos
        Iterator<Bloon> bIt = bloons.iterator();
        List<Bloon> nuevosHijos = new ArrayList<>();

        while (bIt.hasNext()) {
            Bloon b = bIt.next();
            b.update();

            if (b.reachedEnd()) {
                lives -= b.getDamage(); // Resta vidas según el peso RBE del globo
                bIt.remove();
                if (lives <= 0) {
                    lives = 0;
                    running = false;
                    System.out.println("=== FIN DE LA PARTIDA (GAME OVER) ===");
                }
            } else if (b.isPopped()) {
                money += b.getValue(); // Te premia con dinero
                Bloon hijo = b.getChild();
                if (hijo != null) {
                    hijosNuevosAgregar(hijo);
                }
                bIt.remove();
            }
        }
        bloons.addAll(nuevosHijos);
    }

    private void hijosNuevosAgregar(Bloon hijo) {
        hijo.setPath(this.path);
        bloons.add(hijo);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Renderizado del mapa de fondo
        if (mapaFondo != null) {
            g2d.drawImage(mapaFondo, 0, 0, WIDTH, HEIGHT, this);
        } else {
            g2d.setColor(new Color(34, 139, 34));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
        }

        // Dibujar el camino guía semitransparente sobre el gráfico gris
        path.draw(g2d);

        // Dibujar elementos interactivos
        for (Tower t : towers) {
            t.draw(g2d);
        }
        for (Bloon b : bloons) {
            b.draw(g2d);
        }

        // Renderizar los proyectiles interactivos
        g2d.setColor(Color.YELLOW);
        for (Projectile p : projectiles) {
            // Si tu clase proyectil no tiene draw, puedes renderizar un círculo simple en sus coordenadas de acceso
            g2d.fillOval((int) p.getX() - 4, (int) p.getY() - 4, 8, 8);
        }

        // FEEDBACK VISUAL: Si estás cargando una torre, dibuja un círculo con su rango real de colocación
        if (selectedTowerType != null && mousePos != null) {
            g2d.setColor(new Color(255, 255, 255, 60));
            int r = selectedTowerType.getRange();
            g2d.fillOval(mousePos.x - r, mousePos.y - r, r * 2, r * 2);
            g2d.setColor(Color.WHITE);
            g2d.drawOval(mousePos.x - r, mousePos.y - r, r * 2, r * 2);
        }

        // PANEL DE ESTADO INTERACTIVO (HUD)
        g2d.setColor(new Color(0, 0, 0, 140));
        g2d.fillRect(10, 10, 240, 105);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(10, 10, 240, 105);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 15));
        g2d.drawString("💰 DINERO: $" + money, 20, 30);
        g2d.drawString("❤️ VIDAS:  " + lives + " / 100", 20, 55);
        g2d.drawString("🚀 RONDA:  " + (round - 1), 20, 80);
        g2d.setFont(new Font("Arial", Font.ITALIC, 11));
        g2d.drawString("Controles: Teclas [1,2,3,4] | [ESPACIO] Ola", 20, 100);
    }

    public List<Bloon> getBloons() {
        return bloons;
    }
}
