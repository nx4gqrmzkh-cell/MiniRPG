package Panel;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class Path {

    // 1. Declaración de la lista de puntos (permitido en el cuerpo de la clase)
    private List<Point> points = new ArrayList<>();

    // 2. CONSTRUCTOR (Aquí metemos de forma segura todas las coordenadas del mapa gris)
    public Path() {
        addPoint(0, 115);      // Entrada superior izquierda
        addPoint(435, 115);    // Fin de primera recta horizontal
        addPoint(435, 290);    // Bajada vertical hacia el centro
        addPoint(145, 290);    // Giro a la izquierda (bucle central)
        addPoint(145, 460);    // Bajada hacia carril inferior
        addPoint(830, 460);    // Recta horizontal larga baja
        addPoint(830, 200);    // Subida vertical derecha
        addPoint(1024, 200);   // Escape final por el borde derecho
    }

    // 3. MÉTODOS DE LA CLASE (Definen el comportamiento del camino)
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
        g.setColor(new Color(255, 255, 255, 30)); // Línea central guía sutil
        g.setStroke(new BasicStroke(4));

        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }
}
