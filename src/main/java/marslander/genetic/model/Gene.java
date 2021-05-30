package marslander.genetic.model;

public class Gene {
    private int rotate;
    private int power;

    public Gene(int rotate, int power) {
        this.rotate = rotate;
        this.power = power;
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
        return "Gene{" +
                "rotate=" + rotate +
                ", power=" + power +
                '}';
    }
}
