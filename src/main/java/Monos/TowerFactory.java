package Monos;

import java.awt.Color;
import globos.Bloon;

public class TowerFactory {

    // 1. Mono Militar
    public static Tower createSniper() {
        return new Tower("Mono Militar", 999, 400, 2, 2000, new Color(0, 100, 0), "imagenes/buenos/personaje_1_militar.jpg") {
            @Override
            protected Bloon findTarget(java.util.List<Bloon> bloons) {
                Bloon best = null;
                double bestProgress = -1;
                for (Bloon bloon : bloons) {
                    if (bloon.getDistanceTraveled() > bestProgress) {
                        best = bloon;
                        bestProgress = bloon.getDistanceTraveled();
                    }
                }
                return best;
            }
        };
    }

    // 2. Mono Bumerán
    public static Tower createTackShooter() {
        return new Tower("Mono Boomerang", 90, 350, 1, 1100, Color.GRAY, "imagenes/buenos/personaje_2_boomerang.jpg") {
            @Override
            protected void shoot(Bloon target, java.util.List<Projectile> projectiles) {
                // Ráfaga octogonal
                for (int i = 0; i < 8; i++) {
                    double angle = (Math.PI * 2 / 8) * i;
                    projectiles.add(new Projectile(x, y, angle, damage, 8, null));
                }
            }
        };
    }

    // 3. Súper Kitty Heroe
    public static Tower createSuperMonkey() {
        return new Tower("Super Kitty", 150, 2500, 1, 120, new Color(218, 165, 32), "imagenes/buenos/personaje_3_superheroe.jpg");
    }

    // 4. Mono Dardero Tradicional
    public static Tower createDartMonkey() {
        return new Tower("Mono Dardo", 130, 200, 1, 900, new Color(139, 69, 19), "imagenes/buenos/personaje_4_dardo.jpg");
    }
}
