package globos;

import java.awt.*;
import javax.swing.ImageIcon;
import java.util.HashMap;
import java.util.Map;
import Panel.Path;

public class Bloon {

    public enum Type {
        // Cada tipo es más rápido (speed) y resistente (layers/rbe) que el anterior
        RED(1, 1, 1.2, Color.RED, null, "imagenes/enemigos/enemy_1_bow_bat.png"),
        BLUE(2, 2, 1.6, Color.BLUE, RED, "imagenes/enemigos/enemy_2_skull_cat.png"),
        GREEN(3, 3, 2.2, Color.GREEN, BLUE, "imagenes/enemigos/enemy_3_robot_kitty.png"),
        YELLOW(4, 4, 3.0, Color.YELLOW, GREEN, "imagenes/enemigos/enemy_4_shadow_kitty.png");

        public final int layers;
        public final int rbe;
        public final double speed;
        public final Color color;
        public final Type child;
        public final String imagePath;

        Type(int layers, int rbe, double speed, Color color, Type child, String imagePath) {
            this.layers = layers;
            this.rbe = rbe;
            this.speed = speed;
            this.color = color;
            this.child = child;
            this.imagePath = imagePath;
        }
    }

    private static final Map<String, Image> imageCache = new HashMap<>();

    private static Image getBloonImage(String path, int size) {
        if (!imageCache.containsKey(path)) {
            try {
                ImageIcon icon = new ImageIcon(path);
                Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
                imageCache.put(path, scaled);
            } catch (Exception e) {
                return null;
            }
        }
        return imageCache.get(path);
    }

    private Type type;
    private double x, y;
    private double distanceTraveled;
    private boolean popped = false;
    private Path path;

    public Bloon(Type type, Path path) {
        this.type = type;
        this.path = path;
        updatePosition();
    }

    public Bloon(Type type) {
        this.type = type;
    }

    public void setPath(Path path) {
        this.path = path;
        updatePosition();
    }

    public void update() {
        if (popped || path == null) {
            return;
        }
        distanceTraveled += type.speed;
        updatePosition();
    }

    private void updatePosition() {
        if (path != null) {
            java.awt.geom.Point2D.Double p = path.getPositionAtDistance(distanceTraveled);
            this.x = p.x;
            this.y = p.y;
        }
    }

    public void damage(int amount) {
        if (popped) {
            return;
        }
        if (type.child != null && type.layers > 1) {
            this.type = type.child;
        } else {
            popped = true;
        }
    }

    public Bloon getChild() {
        if (type.child != null) {
            Bloon child = new Bloon(type.child, this.path);
            child.distanceTraveled = this.distanceTraveled;
            return child;
        }
        return null;
    }

    public boolean reachedEnd() {
        return path != null && distanceTraveled >= path.getTotalLength();
    }

    public boolean isPopped() {
        return popped;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getDamage() {
        return type.rbe;
    }

    public int getValue() {
        return 1;
    }

    public Type getType() {
        return type;
    }

    public double getDistanceTraveled() {
        return distanceTraveled;
    }

    public void draw(Graphics2D g) {
        int size = 35;

        // Sombra
        g.setColor(new Color(0, 0, 0, 50));
        g.fillOval((int) x - size / 2 + 3, (int) y - size / 2 + 3, size, size);

        // Renderizado de Hello Kitty Malvada
        Image img = getBloonImage(type.imagePath, size);
        if (img != null) {
            g.drawImage(img, (int) x - size / 2, (int) y - size / 2, null);
        } else {
            g.setColor(type.color);
            g.fillOval((int) x - size / 2, (int) y - size / 2, size, size);
        }
    }

}
