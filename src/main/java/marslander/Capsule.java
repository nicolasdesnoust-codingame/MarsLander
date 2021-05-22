package marslander;

import marslander.math.Point;

public class Capsule {
    private Point position;
    private double hSpeed;
    private double vSpeed;
    private double fuel;
    private int rotate;
    private int power;

    public Capsule(int x, int y, double hSpeed, double vSpeed, double fuel, int rotate, int power) {
        this.position = new Point(x, y);
        this.hSpeed = hSpeed;
        this.vSpeed = vSpeed;
        this.fuel = fuel;
        this.rotate = rotate;
        this.power = power;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public double gethSpeed() {
        return hSpeed;
    }

    public void sethSpeed(double hSpeed) {
        this.hSpeed = hSpeed;
    }

    public double getvSpeed() {
        return vSpeed;
    }

    public void setvSpeed(double vSpeed) {
        this.vSpeed = vSpeed;
    }

    public double getFuel() {
        return fuel;
    }

    public void setFuel(double fuel) {
        this.fuel = fuel;
    }

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public String toString() {
        return "Capsule{" +
                "position=" + position +
                ", hSpeed=" + hSpeed +
                ", vSpeed=" + vSpeed +
                ", fuel=" + fuel +
                ", rotate=" + rotate +
                ", power=" + power +
                '}';
    }
}
