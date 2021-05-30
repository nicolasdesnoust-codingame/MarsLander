package marslander.core;

import marslander.math.Point;

import java.util.List;

public class Mars {
    private final List<Point> surface;

    public Mars(List<Point> surface) {
        this.surface = surface;
    }

    public List<Point> getSurface() {
        return surface;
    }
}
