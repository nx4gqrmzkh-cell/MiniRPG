package Panel;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class Path {

    private List<Point> points = new ArrayList<>();

    public Path() {
        // == ENTRADA (Lado Izquierdo) ==
        addPoint(35, 315);   // Punto 1 (Punto azul de salida)
        addPoint(95, 315);   // Punto 2
        addPoint(155, 315);  // Punto 3
        addPoint(220, 315);  // Punto 4
        addPoint(285, 315);  // Punto 5
        addPoint(370, 315);  // Punto 6 (Intersección central superior)
        addPoint(455, 315);  // Punto 7
        addPoint(525, 315);  // Punto 8
        addPoint(580, 315);  // Punto 9 (Giro hacia arriba)

        // == BUCLE SUPERIOR DERECHO ==
        addPoint(580, 230);  // Punto 10
        addPoint(580, 155);  // Punto 11
        addPoint(545, 90);   // Punto 12 (Curva superior derecha)
        addPoint(475, 55);   // Punto 13
        addPoint(420, 55);   // Punto 14
        addPoint(365, 55);   // Punto 15 (Curva superior izquierda)
        addPoint(365, 115);  // Punto 16
        addPoint(365, 180);  // Punto 17
        addPoint(365, 250);  // Punto 18 (Bajada hacia el centro)

        // == BAJADA CENTRAL ==
        addPoint(365, 400);  // Punto 19 (Cruza la línea horizontal)
        addPoint(365, 580);  // Punto 20 (Sigue bajando)

        // == BUCLE INFERIOR IZQUIERDO ==
        addPoint(365, 715);  // Punto 21 (Giro hacia la izquierda)
        addPoint(280, 740);  // Punto 22
        addPoint(185, 730);  // Punto 23 (Curva inferior izquierda)
        addPoint(185, 500);  // Punto 24 (Sube por la izquierda)
        addPoint(250, 480);  // Punto 25 (Giro a la derecha)

        // == TRAMO HORIZONTAL INFERIOR ==
        addPoint(470, 495);  // Punto 26 (Pasa por encima del puente)
        addPoint(605, 495);  // Punto 27
        addPoint(695, 475);  // Punto 28 (Empieza subida a la gran curva)

        // == GRAN CURVA DERECHA ==
        addPoint(730, 410);  // Punto 29
        addPoint(745, 305);  // Punto 30
        addPoint(745, 235);  // Punto 31
        addPoint(810, 205);  // Punto 32 (Parte más alta de la derecha)
        addPoint(870, 235);  // Punto 33 (Curva exterior descendente)
        addPoint(855, 590);  // Punto 34
        addPoint(845, 640);  // Punto 35 (Giro hacia el puente de abajo)

        // == CAMINO FINAL HACIA LA META ==
        addPoint(695, 640);  // Punto 36
        addPoint(525, 665);  // Punto 37 (Entrada al pasillo de los corazones)
        addPoint(510, 770);  // Punto 38 (Bajando por los corazones)
        addPoint(510, 850);  // Punto 39

        // == META (Abajo Centro) ==
        addPoint(510, 960);  // Punto 40 (Punto verde de llegada)
    }

    public void addPoint(int x, int y) {
        points.add(new Point(x, y));
    }

    public Point getPoint(int index) {
        return points.get(Math.min(index, points.size() - 1));
    }

    public int getPointCount() {
        return points.size();
    }

    public double getTotalLength() {
        double length = 0;
        for (int i = 0; i < points.size() - 1; i++) {
            length += points.get(i).distance(points.get(i + 1));
        }
        return length;
    }

    public Point2D.Double getPositionAtDistance(double distance) {
        double traveled = 0;
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            double segmentLength = p1.distance(p2);
            if (traveled + segmentLength >= distance) {
                double ratio = (distance - traveled) / segmentLength;
                double x = p1.x + (p2.x - p1.x) * ratio;
                double y = p1.y + (p2.y - p1.y) * ratio;
                return new Point2D.Double(x, y);
            }
            traveled += segmentLength;
        }
        Point last = points.get(points.size() - 1);
        return new Point2D.Double(last.x, last.y);
    }

    public boolean isOnPath(int x, int y, int tolerance) {
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            double dist = Line2D.ptSegDist(p1.x, p1.y, p2.x, p2.y, x, y);
            if (dist < tolerance) {
                return true;
            }
        }
        return false;
    }

    public void draw(Graphics2D g) {
        // Dibuja una línea semi-transparente que une todos los puntos para depuración
        g.setColor(new Color(255, 0, 128, 80)); // Un tono rosa/fucsia Hello Kitty
        g.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }
}
