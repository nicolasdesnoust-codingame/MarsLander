package io.github.nicolasdesnoust.marslander.math;

import java.util.List;

public class Point {
    private double x;
    private double y;

    public Point() {
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point point) {
        this.x = point.x;
        this.y = point.y;
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

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Point)) {
            return false;
        }
        Point other = (Point) obj;
        if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

}