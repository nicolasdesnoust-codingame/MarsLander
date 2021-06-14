package io.github.nicolasdesnoust.marslander.core;

import io.github.nicolasdesnoust.marslander.math.Point;

import java.util.Collections;
import java.util.List;

public class Mars {
    private final List<Point> surface;

    public Mars(List<Point> surface) {
        this.surface = Collections.unmodifiableList(surface);
    }

    public List<Point> getSurface() {
        return surface;
    }
}
