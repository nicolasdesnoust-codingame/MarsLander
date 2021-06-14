package io.github.nicolasdesnoust.marslander.math;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Permet de recuperer un point sur une droite (origin;extremite)
     * à distance length du point origin.
     */
    public static Point getPointOnLine(Point origin, Point extremite, double length) {
        Vector vector = new Vector(origin, extremite);
        vector.normalize();
        vector.scale(length);
        vector.add(origin);
        return new Point(vector.getX(), vector.getY());
    }

    public static double distance(Point a, Point b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }
    
	public static double computeDistance(List<Point> points) {
		double distance = 0;
		Point lastPoint = points.get(0);
		for (int i = 1; i < points.size(); i++) {
			distance += Point.distance(lastPoint, points.get(i));
			lastPoint = points.get(i);
		}
		return distance;
	}

    public Point findNearestPoint(List<Point> points) {
        return points.stream()
                .min(Comparator.comparingDouble(point -> distance(this, point)))
                .orElse(null);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + Math.round(x) +
                ", y=" + Math.round(y) +
                '}';
    }
}
