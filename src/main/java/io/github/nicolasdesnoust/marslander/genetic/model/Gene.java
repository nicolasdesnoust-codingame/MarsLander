package io.github.nicolasdesnoust.marslander.genetic.model;

public class Gene {
    private int rotateIncrement;
    private int powerIncrement;

    public Gene(int rotateIncrement, int powerIncrement) {
        this.rotateIncrement = rotateIncrement;
        this.powerIncrement = powerIncrement;
    }

    public int getRotateIncrement() {
        return rotateIncrement;
    }

    public void setRotateIncrement(int rotateIncrement) {
        this.rotateIncrement = rotateIncrement;
    }

    public int getPowerIncrement() {
        return powerIncrement;
    }

    public void setPowerIncrement(int powerIncrement) {
        this.powerIncrement = powerIncrement;
    }

    @Override
    public String toString() {
        return "Gene{" +
                "rotateIncrement=" + rotateIncrement +
                ", powerIncrement=" + powerIncrement +
                '}';
    }
}
