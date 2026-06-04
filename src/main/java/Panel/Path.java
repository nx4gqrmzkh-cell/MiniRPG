package Panel;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class Path {

    // Lista de puntos que define el camino por el que se moverán los globos
    private List<Point> points = new ArrayList<>();

    // ========================================================
    // CONSTRUCTOR: Inicializa el camino interactivo del mapa
    // ========================================================
    public Path() {
        // Coordenadas milimétricas adaptadas a los carriles grises de tu mapa.png
        addPoint(0, 115);      // Entrada superior izquierda
        addPoint(435, 115);    // Primera recta horizontal hacia la derecha
        addPoint(435, 290);    // Bajada vertical hacia el centro del mapa
        addPoint(145, 290);    // Giro a la izquierda (bucle interior)
        addPoint(145, 460);    // Bajada vertical hacia la zona inferior izquierda
        addPoint(830, 460);    // Recta horizontal larga por abajo hacia la derecha
        addPoint(830, 200);    // Subida vertical por la zona derecha
        addPoint(1024, 200);   // Escape definitivo por el borde derecho
    }

    // Método fundamental para añadir los puntos a la lista
    public void addPoint(int x, int y) {
        points.add(new Point(x, y));
    }

    public Point getPoint(int index) {
        return points.get(Math.min(index, points.size() - 1));
    }

    public int getPointCount() {
        return points.size();
    }

    // Calcula la longitud total en píxeles de la ruta (evita nulos en Bloon.java)
    public double getTotalLength() {
        double length = 0;
        for (int i = 0; i < points.size() - 1; i++) {
            length += points.get(i).distance(points.get(i + 1));
        }
        return length;
    }

    // Permite a los globos calcular su posición X e Y exacta en cada fotograma del gameloop
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

    // SISTEMA INTERACTIVO: Bloquea la colocación de monos sobre el camino gris
    public boolean isOnPath(int x, int y, int tolerance) {
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            double dist = Line2D.ptSegDist(p1.x, p1.y, p2.x, p2.y, x, y);
            if (dist < tolerance) {
                return true; // El punto del ratón está encima del camino
            }
        }
        return false;
    }

    // Dibuja una línea de depuración (Opcional, tu fondo ya tiene el diseño dibujado)
    public void draw(Graphics2D g) {
        g.setColor(new Color(255, 255, 255, 30)); // Línea central sutil blanca
        g.setStroke(new BasicStroke(4));
        for (int i = 0; i < points.size() - 1; i++) {
            g.drawLine(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
        }
    }
}
