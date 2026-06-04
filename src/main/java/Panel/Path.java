package Panel;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class Path {

    // Lista para almacenar los puntos de giro del mapa
    private List<Point> points = new ArrayList<>();

    // ========================================================
    // CONSTRUCTOR: Aquí definimos las coordenadas de tu mapa
    // ========================================================
    public Path() {
        // Coordenadas calculadas según el recorrido de mapa.png
        addPoint(0, 115);      // Entrada por el borde izquierdo (arriba)
        addPoint(435, 115);    // Fin de la primera línea recta horizontal
        addPoint(435, 290);    // Bajada vertical hacia la mitad
        addPoint(145, 290);    // Giro a la izquierda (bucle central)
        addPoint(145, 460);    // Bajada hacia el carril inferior
        addPoint(830, 460);    // Recta larga horizontal por abajo hacia la derecha
        addPoint(830, 200);    // Subida vertical en la zona derecha
        addPoint(1024, 200);   // Salida/Escape final por el borde derecho
    }

    // Método para añadir puntos de forma ordenada
    public void addPoint(int x, int y) {
        points.add(new Point(x, y));
    }

    public Point getPoint(int index) {
        return points.get(Math.min(index, points.size() - 1));
    }

    public int getPointCount() {
        return points.size();
    }

    // Calcula la longitud total de píxeles del camino
    public double getTotalLength() {
        double length = 0;
        for (int i = 0; i < points.size() - 1; i++) {
            length += points.get(i).distance(points.get(i + 1));
        }
        return length;
    }

    // Permite a los globos saber en qué coordenadas (X, Y) exactas deben estar según su distancia recorrida
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

    // Verifica si el jugador está intentando colocar una torre encima del camino gris
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

    // Dibuja una línea de depuración guía (opcional, tu mapa ya tiene fondo)
    public void draw(Graphics2D g) {
        g.setColor(new Color(255, 255, 255, 40)); // Línea blanca semitransparente central
        g.setStroke(new BasicStroke(4));
        for (int i = 0; i < points.size() - 1; i++) {
            g.drawLine(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
        }
    }
}
