/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Panel;

/**
 *
 * @author Juan
 */
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class Path {

    private List<Point> points = new ArrayList<>();

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
                double t = (distance - traveled) / segmentLength;
                return new Point2D.Double(
                        p1.x + t * (p2.x - p1.x),
                        p1.y + t * (p2.y - p1.y)
                );
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
        g.setColor(new Color(139, 119, 101));
        g.setStroke(new BasicStroke(40, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        // Borde del camino
        g.setColor(new Color(101, 67, 33));
        g.setStroke(new BasicStroke(44, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        // Redibujar interior
        g.setColor(new Color(139, 119, 101));
        g.setStroke(new BasicStroke(36, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }
}
