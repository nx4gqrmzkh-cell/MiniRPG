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
    public static final int HEIGHT = 640;
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

        // ── Carga del mapa: intenta varias rutas para mayor compatibilidad ────
        mapaFondo = cargarImagen("mapa.png",
                "resources/mapa.png",
                "src/mapa.png",
                "../mapa.png");

        // ── BD (modo desconectado si no hay MySQL) ────────────────────────────
        try {
            new TorreDAO().leerTodos();
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
                repaint();
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

    /**
     * Intenta cargar una imagen desde varias rutas candidatas.
     */
    private Image cargarImagen(String... rutas) {
        for (String ruta : rutas) {
            try {
                ImageIcon icon = new ImageIcon(ruta);
                if (icon.getIconWidth() > 0) {
                    System.out.println("[Mapa] Cargado desde: " + ruta);
                    return icon.getImage();
                }
            } catch (Exception ignored) {
            }
        }
        System.err.println("[Mapa] No se encontró mapa.png. Usando fondo verde.");
        return null;
    }

    public void start() {
        if (gameThread == null) {
            gameThread = new Thread(this);
            running = true;
            gameThread.start();
        }
    }

    // ── CLICK: colocar torre ──────────────────────────────────────────────────
    private void handleClick(int x, int y) {
        if (selectedTowerType == null) {
            return;
        }

        if (path.isOnPath(x, y, 38)) {
            System.out.println("¡Zona de carril reservada para globos!");
            return;
        }
        for (Tower t : towers) {
            if (Math.hypot(t.getX() - x, t.getY() - y) < 32) {
                System.out.println("No puedes superponer dos monos.");
                return;
            }
        }
        if (money >= selectedTowerType.getCost()) {
            money -= selectedTowerType.getCost();
            Tower nueva = crearTorrePorNombre(selectedTowerType.getName());
            nueva.setPosition(x, y);
            towers.add(nueva);
        }
    }

    private Tower crearTorrePorNombre(String nombre) {
        return switch (nombre) {
            case "Mono Militar" ->
                TowerFactory.createSniper();
            case "Mono Boomerang" ->
                TowerFactory.createTackShooter();
            case "Super Kitty" ->
                TowerFactory.createSuperMonkey();
            default ->
                TowerFactory.createDartMonkey();
        };
    }

    // ── TECLADO ───────────────────────────────────────────────────────────────
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

    // ── GAME LOOP ─────────────────────────────────────────────────────────────
    @Override
    public void run() {
        long nsPerFrame = 1_000_000_000L / TARGET_FPS;
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
            } catch (Exception ignored) {
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

        List<Bloon> nuevosHijos = new ArrayList<>();
        Iterator<Bloon> bIt = bloons.iterator();
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
                    int rf = round - 1;
                    PartidaLogger.guardarPartida(rf, money, lives);
                    try {
                        new HistorialDAO().guardar(rf, money, lives);
                    } catch (Exception ex) {
                        System.err.println("[BD] " + ex.getMessage());
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

        // Guardar estado al completar oleada (una sola vez)
        int rc = round - 1;
        if (!waveManager.isWaveActive() && bloons.isEmpty() && rc > 0 && rc != ultimaRondaGuardada) {
            ultimaRondaGuardada = rc;
            PartidaLogger.guardarPartida(rc, money, lives);
            try {
                new HistorialDAO().guardar(rc, money, lives);
            } catch (Exception ex) {
                System.err.println("[BD] " + ex.getMessage());
            }
        }
    }

    // ── RENDER ────────────────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Fondo: mapa real o color de emergencia
        if (mapaFondo != null) {
            g2.drawImage(mapaFondo, 0, 0, WIDTH, HEIGHT, this);
        } else {
            g2.setColor(new Color(255, 192, 203)); // rosa claro de emergencia
            g2.fillRect(0, 0, WIDTH, HEIGHT);
        }

        // Línea guía sutil del camino (sólo en modo debug)
        // path.draw(g2);  // ← descomenta para depurar el trayecto
        for (Tower t : towers) {
            t.draw(g2);
        }
        for (Bloon b : bloons) {
            b.draw(g2);
        }
        for (Projectile p : projectiles) {
            p.draw(g2);
        }

        // Vista previa de rango al colocar torre
        if (selectedTowerType != null) {
            int r = selectedTowerType.getRange();
            g2.setColor(new Color(255, 255, 255, 50));
            g2.fillOval(mousePos.x - r, mousePos.y - r, r * 2, r * 2);
            g2.setColor(new Color(255, 182, 193, 200));
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(mousePos.x - r, mousePos.y - r, r * 2, r * 2);
            g2.setStroke(new BasicStroke(1));
        }

        drawHUD(g2);
    }

    private void drawHUD(Graphics2D g) {
        // Panel HUD con estilo Kitty
        g.setColor(new Color(255, 240, 245, 200));
        g.fillRoundRect(12, 12, 270, 118, 16, 16);
        g.setColor(new Color(220, 80, 120));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(12, 12, 270, 118, 16, 16);
        g.setStroke(new BasicStroke(1));

        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        g.setColor(new Color(150, 0, 60));
        g.drawString("💵  DINERO: $" + money, 24, 38);
        g.drawString("❤️  VIDAS:   " + lives + " / 100", 24, 60);
        g.drawString("⭐  RONDA:   " + (round - 1), 24, 82);

        g.setFont(new Font("Arial", Font.ITALIC, 11));
        g.setColor(new Color(180, 60, 100));
        g.drawString("[1] Dardo  [2] Boom  [3] Sniper  [4] Super", 24, 102);
        g.drawString("[ESPACIO] Iniciar oleada    [ESC] Cancelar", 24, 118);

        // Indicador de torre seleccionada
        if (selectedTowerType != null) {
            g.setColor(new Color(255, 240, 245, 220));
            g.fillRoundRect(12, 140, 200, 28, 10, 10);
            g.setColor(new Color(220, 80, 120));
            g.drawRoundRect(12, 140, 200, 28, 10, 10);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.setColor(new Color(100, 0, 50));
            g.drawString("► " + selectedTowerType.getName()
                    + "  ($" + selectedTowerType.getCost() + ")", 22, 159);
        }
    }

    // ── API pública para WaveManager ─────────────────────────────────────────
    public void spawnBloon(Bloon b) {
        b.setPath(this.path);
        bloons.add(b);
    }

    public List<Bloon> getBloons() {
        return bloons;
    }
}
