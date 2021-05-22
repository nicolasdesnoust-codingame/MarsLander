package marslander;

import marslander.math.Point;

import java.util.List;

public class Mars {
    private List<Point> surface;

    public Mars(List<Point> surface) {
        this.surface = surface;
    }

    public List<Point> getSurface() {
        return surface;
    }

    public void setSurface(List<Point> surface) {
        this.surface = surface;
    }
}
