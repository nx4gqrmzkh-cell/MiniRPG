package Monos;

import globos.Bloon;
import java.awt.*;

public class Projectile {

    private double x, y;
    private double vx, vy;
    private int damage;
    private double speed;
    private Bloon target;
    private boolean expired = false;
    private int lifetime = 60;

    public Projectile(double x, double y, double angle, int damage, double speed, Bloon target) {
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.speed = speed;
        this.target = target;
        this.vx = Math.cos(angle) * speed;
        this.vy = Math.sin(angle) * speed;
    }

    public void update() {
        x += vx;
        y += vy;
        lifetime--;

        if (lifetime <= 0 || x < 0 || x > 1024 || y < 0 || y > 640) {
            expired = true;
        }

        if (target != null && !target.isPopped()) {
            double dist = Math.hypot(target.getX() - x, target.getY() - y);
            if (dist < 20) {
                target.takeDamage(damage);
                expired = true;
            }
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.YELLOW);
        g.fillOval((int) x - 4, (int) y - 4, 8, 8);
        g.setColor(Color.ORANGE);
        g.drawOval((int) x - 4, (int) y - 4, 8, 8);
    }

    public boolean isExpired() {
        return expired;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
}
