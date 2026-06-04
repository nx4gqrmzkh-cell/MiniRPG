package GamePane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import globos.Bloon;
import Monos.Tower;
import Monos.TowerFactory;
import Monos.Projectile;
import Panel.Path;
import Oleadas.WaveManager;
import controlador.TorreDAO;
import controlador.PartidaLogger;
import controlador.HistorialDAO;

public class GamePanel extends JPanel implements Runnable {

    public static final int WIDTH = 1024;
    public static final int HEIGHT = 1080;
    private static final int TARGET_FPS = 60;

    private Thread gameThread;
    private boolean running;
    private Image mapaFondo;

    private int money = 650;
    private int lives = 100;
    private int round = 1;
    private int ultimaRondaGuardada = 0;

    private List<Bloon> bloons = new ArrayList<>();
    private List<Tower> towers = new ArrayList<>();
    private List<Projectile> projectiles = new ArrayList<>();

    private Path path;
    private WaveManager waveManager;
    private Tower selectedTowerType = null;
    private Point mousePos = new Point(0, 0);

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);

        // Carga del mapa usando getResource (funciona dentro del JAR y en desarrollo)
        java.net.URL urlMapa = getClass().getClassLoader().getResource("imagenes/mapa/mapa.png");
        if (urlMapa != null) {
            mapaFondo = new ImageIcon(urlMapa).getImage();
            System.out.println("[Mapa] Cargado correctamente.");
        } else {
            System.err.println("[Mapa] No se encontró imagenes/mapa/mapa.png. Usando fondo verde.");
        }

        try {
            TorreDAO db = new TorreDAO();
            db.leerTodos();
        } catch (Exception e) {
            System.out.println("[SQL] Corriendo en modo desconectado.");
        }

        path = new Path();
        waveManager = new WaveManager(this);

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

        start();
    }

    public void start() {
        if (gameThread == null) {
            gameThread = new Thread(this);
            running = true;
            gameThread.start();
        }
    }

    private void handleClick(int x, int y) {
        if (selectedTowerType != null) {
            if (path.isOnPath(x, y, 32)) {
                System.out.println("¡Error: Zona de carril reservada para globos!");
                return;
            }

            for (Tower t : towers) {
                if (Math.hypot(t.getX() - x, t.getY() - y) < 30) {
                    System.out.println("No puedes superponer dos monos.");
                    return;
                }
            }

            if (money >= selectedTowerType.getCost()) {
                money -= selectedTowerType.getCost();
                Tower nuevaTorre;
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
            }
        }
    }

    private void handleKeyPress(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_1 ->
                selectedTowerType = TowerFactory.createDartMonkey();
            case KeyEvent.VK_2 ->
                selectedTowerType = TowerFactory.createTackShooter();
            case KeyEvent.VK_3 ->
                selectedTowerType = TowerFactory.createSniper();
            case KeyEvent.VK_4 ->
                selectedTowerType = TowerFactory.createSuperMonkey();
            case KeyEvent.VK_ESCAPE ->
                selectedTowerType = null;
            case KeyEvent.VK_SPACE -> {
                if (!waveManager.isWaveActive() && bloons.isEmpty()) {
                    waveManager.startWave(round);
                    round++;
                }
            }
        }
    }

    @Override
    public void run() {
        long nsPerFrame = 1000000000 / TARGET_FPS;
        long lastTime = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            long elapsed = now - lastTime;

            if (elapsed >= nsPerFrame) {
                updateGame();
                repaint();
                lastTime = now - (elapsed % nsPerFrame);
            }
            try {
                Thread.sleep(2);
            } catch (Exception e) {
            }
        }
    }

    private void updateGame() {
        waveManager.update();

        for (Tower t : towers) {
            t.update(bloons, projectiles);
        }

        Iterator<Projectile> pIt = projectiles.iterator();
        while (pIt.hasNext()) {
            Projectile p = pIt.next();
            p.update();
            if (p.isExpired()) {
                pIt.remove();
            }
        }

        Iterator<Bloon> bIt = bloons.iterator();
        List<Bloon> nuevosHijos = new ArrayList<>();

        while (bIt.hasNext()) {
            Bloon b = bIt.next();
            b.update();

            if (b.reachedEnd()) {
                lives -= b.getDamage();
                bIt.remove();
                if (lives <= 0) {
                    lives = 0;
                    running = false;
                    System.out.println("GAME OVER");
                    int rondaFinal = round - 1;
                    PartidaLogger.guardarPartida(rondaFinal, money, lives);
                    try {
                        new HistorialDAO().guardar(rondaFinal, money, lives);
                    } catch (Exception ex) {
                        System.err.println("[BD] No se pudo guardar el historial: " + ex.getMessage());
                    }
                }
            } else if (b.isPopped()) {
                money += b.getValue();
                Bloon hijo = b.getChild();
                if (hijo != null) {
                    hijo.setPath(this.path);
                    nuevosHijos.add(hijo);
                }
                bIt.remove();
            }
        }
        bloons.addAll(nuevosHijos);

        int rondaCompletada = round - 1;
        if (!waveManager.isWaveActive() && bloons.isEmpty() && rondaCompletada > 0
                && rondaCompletada != ultimaRondaGuardada) {
            ultimaRondaGuardada = rondaCompletada;
            PartidaLogger.guardarPartida(rondaCompletada, money, lives);
            try {
                new HistorialDAO().guardar(rondaCompletada, money, lives);
            } catch (Exception ex) {
                System.err.println("[BD] No se pudo guardar el historial: " + ex.getMessage());
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (mapaFondo != null) {
            g2d.drawImage(mapaFondo, 0, 0, WIDTH, HEIGHT, this);
        } else {
            g2d.setColor(new Color(34, 139, 34));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
        }

        path.draw(g2d);

        for (Tower t : towers) {
            t.draw(g2d);
        }
        for (Bloon b : bloons) {
            b.draw(g2d);
        }
        for (Projectile p : projectiles) {
            p.draw(g2d);
        }

        if (selectedTowerType != null && mousePos != null) {
            g2d.setColor(new Color(255, 255, 255, 60));
            int r = selectedTowerType.getRange();
            g2d.fillOval(mousePos.x - r, mousePos.y - r, r * 2, r * 2);
            g2d.setColor(Color.WHITE);
            g2d.drawOval(mousePos.x - r, mousePos.y - r, r * 2, r * 2);
        }

        // HUD
        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.fillRect(15, 15, 650, 110);

        g2d.setColor(Color.WHITE);
        g2d.drawRect(15, 15, 650, 110);

        g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2d.drawString("💵 DINERO: $" + money, 25, 38);
        g2d.drawString("❤️ VIDAS:  " + lives + " / 100", 25, 63);
        g2d.drawString("⭐ RONDA:  " + (round - 1), 25, 88);

        g2d.setFont(new Font("Arial", Font.ITALIC, 11));
        g2d.drawString(
                "Teclas [1 Dardero, 2 Boomerang, 3 Militar, 4 SuperKitty] | [ESPACIO] Iniciar ronda",
                25, 112
        );
    }

    public void spawnBloon(Bloon b) {
        b.setPath(this.path);
        bloons.add(b);
    }

    public List<Bloon> getBloons() {
        return bloons;
    }
}
