package io.github.nicolasdesnoust.marslander.math;

import java.util.Objects;

public class Vector {
    private double x;
    private double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector(Point p1, Point p2) {
        this(
                p2.getX() - p1.getX(),
                p2.getY() - p1.getY()
        );
    }

    public double dot(Vector other) {
        return other.x * x + other.y * y;
    }

    public void normalize() {
        double magnitude = getLength();
        x /= magnitude;
        y /= magnitude;
    }

    public double getLength() {
        return Math.sqrt(x * x + y * y);
    }

    public void scale(double length) {
        x *= length;
        y *= length;
    }

    public void add(Point point) {
        this.x += point.getX();
        this.y += point.getY();
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
    public String toString() {
        return "Vector{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector vector = (Vector) o;
        return x == vector.x && y == vector.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
