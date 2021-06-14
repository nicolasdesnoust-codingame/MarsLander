package io.github.nicolasdesnoust.marslander.math;

import java.util.Objects;
import java.util.Optional;

public class Line {
    private double a;
    private double b;

    public Line(double a, double b) {
        this.a = a;
        this.b = b;
    }

    /**
     * Calcule l'équation d'une droite à partir de deux points
     */
    public static Line computeLineEquation(Point p1, Point p2) {
        double x1 = p1.getX();
        double x2 = p2.getX();

        if (x1 == x2) {
            x2 += 1;
        }
        double a = (p2.getY() - p1.getY()) / (x2 - x1);
        double b = p1.getY() - a * x1;
        return new Line(a, b);
    }

    public static Line computeLineEquation(Segment segment) {
        return computeLineEquation(segment.getP1(), segment.getP2());
    }

    /**
     * Retourne la bissectrice de l'angle ABC
     */
    public static Line getBisectingLine(Point a, Point b, Point c) {
        Point p1 = Point.getPointOnLine(b, a, 10);
        Point p2 = Point.getPointOnLine(b, c, 10);

        Point p3 = new Segment(p1, p2).getMiddle();

        return computeLineEquation(b, p3);
    }

    /**
     * Retourne le point d'intersection entre deux droites, s'il existe.
     */
    public static Optional<Point> calculateIntersectionPoint(Line line1, Line line2) {

        if (line1.a == line2.a) {
            return Optional.empty();
        }

        double x = (line2.b - line1.b) / (line1.a - line2.a);
        double y = line1.a * x + line1.b;

        Point point = new Point((int) x, (int) y);
        return Optional.of(point);
    }

    public Point getPointAbove(Point origin, double distance) {
        double y = origin.getY() + 1;
        double x = ((y - b) / a);
        Point end = new Point(x, y);
        Vector vector = new Vector(origin, end);
        vector.normalize();
        vector.scale(distance);
        vector.add(origin);
        return new Point((int) vector.getX(), (int) vector.getY());
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    @Override
    public String toString() {
        return "Line{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Double.compare(line.a, a) == 0 && Double.compare(line.b, b) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }
}
