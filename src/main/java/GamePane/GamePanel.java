/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GamePane;

/**
 *
 * @author Juan
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Importaciones necesarias para enlazar tus otros paquetes
import globos.Bloon;
import Monos.Tower;
import Monos.TowerFactory;
import Monos.Projectile;
import Panel.Path;
import Oleadas.WaveManager;

public class GamePanel extends JPanel implements Runnable {

    public static final int WIDTH = 1024;
    public static final int HEIGHT = 640;
    private static final int TARGET_FPS = 60;

    private Thread gameThread;
    private boolean running;

    // Estado del juego
    private int money = 650;
    private int lives = 100;
    private int round = 1;

    // Entidades
    private List<Bloon> bloons = new ArrayList<>();
    private List<Tower> towers = new ArrayList<>();
    private List<Projectile> projectiles = new ArrayList<>();

    // Mapa y camino
    private Path path;

    // UI
    private Tower selectedTowerType = null;
    private Point mousePos = new Point(0, 0);

    // Oleadas
    private WaveManager waveManager;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(34, 139, 34)); // Verde césped
        setFocusable(true);

        initPath();
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
    }

    private void initPath() {
        // Camino en forma de S
        path = new Path();
        path.addPoint(0, 300);
        path.addPoint(200, 300);
        path.addPoint(200, 100);
        path.addPoint(500, 100);
        path.addPoint(500, 400);
        path.addPoint(800, 400);
        path.addPoint(800, 200);
        path.addPoint(1024, 200);
    }

    public void startGame() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerFrame = 1_000_000_000.0 / TARGET_FPS;
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerFrame;
            lastTime = now;

            while (delta >= 1) {
                update();
                delta--;
            }

            repaint();

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        if (lives <= 0) {
            return;
        }

        waveManager.update();

        // Actualizar bloons
        Iterator<Bloon> bloonIt = bloons.iterator();
        while (bloonIt.hasNext()) {
            Bloon bloon = bloonIt.next();

            // CORRECCIÓN: Quitamos el parámetro 'path' ya que la clase Bloon actual maneja su ruta de forma interna
            bloon.update();

            if (bloon.reachedEnd()) {
                lives -= bloon.getDamage();
                bloonIt.remove();
            } else if (bloon.isPopped()) {
                money += bloon.getValue();
                bloonIt.remove();
            }
        }

        // Torres disparan
        for (Tower tower : towers) {
            tower.update(bloons, projectiles);
        }

        // Actualizar proyectiles
        Iterator<Projectile> projIt = projectiles.iterator();
        while (projIt.hasNext()) {
            Projectile proj = projIt.next();
            proj.update();

            if (proj.isExpired() || proj.hitTarget()) {
                projIt.remove();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibujar camino
        path.draw(g2d);

        // Dibujar torres
        for (Tower tower : towers) {
            tower.draw(g2d);
        }

        // Dibujar bloons
        for (Bloon bloon : bloons) {
            bloon.draw(g2d);
        }

        // Dibujar proyectiles
        for (Projectile proj : projectiles) {
            proj.draw(g2d);
        }

        // Preview de torre
        if (selectedTowerType != null) {
            drawTowerPreview(g2d);
        }

        // HUD
        drawHUD(g2d);
    }

    private void drawTowerPreview(Graphics2D g) {
        g.setColor(new Color(0, 255, 0, 100));
        g.fillOval(mousePos.x - 20, mousePos.y - 20, 40, 40);
        g.setColor(new Color(255, 255, 255, 50));
        int range = selectedTowerType.getRange();
        g.fillOval(mousePos.x - range, mousePos.y - range, range * 2, range * 2);
    }

    private void drawHUD(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, WIDTH, 40);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14)); // Bajamos ligeramente el tamaño para que quepa todo el texto
        g.drawString("💰 $" + money, 20, 26);
        g.drawString("❤️ " + lives, 120, 26);
        g.drawString("🌊 Ronda " + round, 220, 26);

        // CORRECCIÓN: Actualizado el HUD de texto para incluir a la cuarta torre en el menú visual
        g.drawString("[1] Dardo $200  [2] Boomerang $350  [3] Militar $400  [4] Super Kitty $2500  [SPACE] Oleada", 330, 26);
    }

    private void handleClick(int x, int y) {
        if (selectedTowerType != null && canPlaceTower(x, y)) {
            if (money >= selectedTowerType.getCost()) {
                Tower newTower = selectedTowerType.clone();
                newTower.setPosition(x, y);
                towers.add(newTower);
                money -= selectedTowerType.getCost();
                selectedTowerType = null;
            }
        }
    }

    private boolean canPlaceTower(int x, int y) {
        // No colocar en el camino
        if (path.isOnPath(x, y, 30)) {
            return false;
        }

        // No colocar sobre otra torre
        for (Tower t : towers) {
            double dist = Math.hypot(t.getX() - x, t.getY() - y);
            if (dist < 50) {
                return false;
            }
        }
        return true;
    }

    private void handleKeyPress(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_1:
                selectedTowerType = TowerFactory.createDartMonkey();
                break;
            case KeyEvent.VK_2:
                selectedTowerType = TowerFactory.createTackShooter();
                break;
            case KeyEvent.VK_3:
                selectedTowerType = TowerFactory.createSniper();
                break;
            case KeyEvent.VK_4:
                selectedTowerType = TowerFactory.createSuperMonkey();
                break;
            case KeyEvent.VK_SPACE:
                if (!waveManager.isWaveActive()) {
                    waveManager.startWave(round);
                    round++;
                }
                break;
            case KeyEvent.VK_ESCAPE:
                selectedTowerType = null;
                break;
        }
    }

    // CORRECCIÓN: Vinculamos la ruta del mapa automáticamente al globo cuando aparece 
    // para asegurar que 'path' nunca sea nulo dentro de su ejecución interna
    public void spawnBloon(Bloon bloon) {
        bloon.setPath(this.path);
        bloons.add(bloon);
    }

    public List<Bloon> getBloons() {
        return bloons;
    }

    public int getRound() {
        return round;
    }
}
