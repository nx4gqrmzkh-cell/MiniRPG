package Panel;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Camino adaptado al mapa "mapa.png" (1456×734 → escala a 1024×640).
 *
 * El trazado sigue la espiral de baldosas rosas: Entrada: borde derecho, zona
 * media-baja Recorre la espiral exterior → bucles interiores → salida borde
 * izq./inf.
 *
 * Coordenadas calculadas con factor escala X≈0.703, Y≈0.872 y ajustadas
 * visualmente para el ancho de baldosa (~40 px en panel).
 */
public class Path {

    private List<Point> points = new ArrayList<>();

    public Path() {
        // ── ENTRADA: borde derecho, carril medio ──────────────────────────────
        addPoint(1024, 390);   // 0  Entrada borde derecho

        // ── RECTA INFERIOR DERECHA hacia la izquierda ─────────────────────────
        addPoint(840, 390);   // 1  Inicio giro interior derecho
        addPoint(840, 560);   // 2  Baja hacia carril inferior

        // ── RECTA INFERIOR larga hacia la izquierda ───────────────────────────
        addPoint(200, 560);   // 3  Recta baja, zona inferior

        // ── SUBE por el lado izquierdo ────────────────────────────────────────
        addPoint(200, 120);   // 4  Sube por borde izquierdo

        // ── RECTA SUPERIOR hacia la derecha ───────────────────────────────────
        addPoint(780, 120);   // 5  Recta alta, hacia la derecha

        // ── BAJA segunda vuelta ────────────────────────────────────────────────
        addPoint(780, 470);   // 6  Baja vuelta interior derecha

        // ── SEGUNDA RECTA INFERIOR más corta ──────────────────────────────────
        addPoint(340, 470);   // 7  Segunda recta baja

        // ── SUBE segunda vuelta interior ──────────────────────────────────────
        addPoint(340, 220);   // 8  Sube interior

        // ── TERCERA RECTA SUPERIOR ────────────────────────────────────────────
        addPoint(640, 220);   // 9  Recta media-alta

        // ── BAJA tercera vuelta (centro espiral) ──────────────────────────────
        addPoint(640, 370);   // 10 Baja al centro

        // ── RECTA CENTRAL hacia la izquierda ──────────────────────────────────
        addPoint(450, 370);   // 11 Centro de la espiral

        // ── SALIDA: borde inferior ────────────────────────────────────────────
        addPoint(450, 640);   // 12 Sale por el borde inferior
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
        // Línea guía central semitransparente (debug visual)
        g.setColor(new Color(255, 200, 220, 40));
        g.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        g.setStroke(new BasicStroke(1));
    }
}
