package io.github.nicolasdesnoust.marslander.core;

import io.github.nicolasdesnoust.marslander.math.Point;

public class Capsule {
    private Point position;
    private double hSpeed;
    private double vSpeed;
    private double fuel;
    private int rotate;
    private int power;
    private LandingState landingState;

    public Capsule() {}

    public Capsule(Capsule capsule) {
        this.position = new Point(capsule.getPosition().getX(), capsule.getPosition().getY());
        this.hSpeed = capsule.hSpeed;
        this.vSpeed = capsule.vSpeed;
        this.fuel = capsule.fuel;
        this.rotate = capsule.rotate;
        this.power = capsule.power;
        this.landingState = capsule.landingState;
    }

    public Capsule(int x, int y, double hSpeed, double vSpeed, double fuel, int rotate, int power) {
        this.position = new Point(x, y);
        this.hSpeed = hSpeed;
        this.vSpeed = vSpeed;
        this.fuel = fuel;
        this.rotate = rotate;
        this.power = power;
        this.landingState = LandingState.STILL_FLYING;
    }

    public Capsule(int rotate, int power) {
        this.rotate = rotate;
        this.power = power;
        this.landingState = LandingState.STILL_FLYING;
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

    public LandingState getLandingState() {
        return landingState;
    }

    public void setLandingState(LandingState landingState) {
        this.landingState = landingState;
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
                ", landingState=" + landingState +
                '}';
    }
}
