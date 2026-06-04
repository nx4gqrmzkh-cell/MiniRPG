package Monos;

import java.awt.*;
import java.util.List;
import javax.imageio.ImageIO;
import globos.Bloon;

public class Tower implements Cloneable {

    protected String name;
    protected int x, y;
    protected int range;
    protected int cost;
    protected int damage;
    protected long fireRate;
    protected long lastShot;
    protected Color color;
    protected String imagePath;
    protected Image towerImage;

    public Tower(String name, int range, int cost, int damage,
            long fireRate, Color color, String imagePath) {

        this.name = name;
        this.range = range;
        this.cost = cost;
        this.damage = damage;
        this.fireRate = fireRate;
        this.color = color;
        this.imagePath = imagePath;

        cargarImagen();
    }

    private void cargarImagen() {
        try {
            var stream = getClass().getClassLoader().getResourceAsStream(imagePath);

            if (stream != null) {
                towerImage = ImageIO.read(stream)
                        .getScaledInstance(45, 45, Image.SCALE_SMOOTH);

                System.out.println("[OK] Imagen cargada: " + imagePath);
            } else {
                System.err.println("[ERROR] No se encontró: " + imagePath);
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Cargando imagen: " + imagePath);
            e.printStackTrace();
        }
    }

    public void update(List<Bloon> bloons, List<Projectile> projectiles) {
        long now = System.currentTimeMillis();

        if (now - lastShot >= fireRate) {
            Bloon target = findTarget(bloons);

            if (target != null) {
                shoot(target, projectiles);
                lastShot = now;
            }
        }
    }

    protected Bloon findTarget(List<Bloon> bloons) {
        for (Bloon bloon : bloons) {

            double dist
                    = Math.hypot(bloon.getX() - x, bloon.getY() - y);

            if (dist <= range && !bloon.isPopped()) {
                return bloon;
            }
        }

        return null;
    }

    protected void shoot(Bloon target, List<Projectile> projectiles) {

        double angle
                = Math.atan2(target.getY() - y, target.getX() - x);

        projectiles.add(
                new Projectile(x, y, angle, damage, 12, target)
        );
    }

    public void draw(Graphics2D g) {

        g.setColor(new Color(0, 0, 0, 40));
        g.fillOval(x - 22, y - 22, 44, 44);

        if (towerImage != null) {

            g.drawImage(
                    towerImage,
                    x - 22,
                    y - 22,
                    45,
                    45,
                    null
            );

        } else {

            g.setColor(color);
            g.fillOval(x - 16, y - 16, 32, 32);
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getRange() {
        return range;
    }

    public int getCost() {
        return cost;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public long getFireRate() {
        return fireRate;
    }

    public Color getColor() {
        return color;
    }
}
