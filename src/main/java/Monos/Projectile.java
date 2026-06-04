/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Monos;

/**
 *
 * @author Juan
 */
import globos.Bloon;
import java.awt.*;

public class Projectile {
    private double x, y;
    private double vx, vy;
    private int damage;
    private double speed;
    private Bloon target;
    private boolean expired = false;
    private boolean hit = false;
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
        
        // Verificar colisión si tiene objetivo
        if (target != null && !target.isPopped()) {
            double dist = Math.hypot(target.getX() - x, target.getY() - y);
            if (dist < 15) {
                target.damage(damage);
                hit = true;
            }
        }
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Bloon getTarget() {
        return target;
    }

    public void setTarget(Bloon target) {
        this.target = target;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public int getLifetime() {
        return lifetime;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }
    
    public void draw(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);
        g.fillOval((int)x - 4, (int)y - 4, 8, 8);
    }
    
    public boolean isExpired() { return expired; }
    public boolean hitTarget() { return hit; }
}

