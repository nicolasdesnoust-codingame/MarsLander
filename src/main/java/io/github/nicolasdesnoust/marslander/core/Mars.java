package io.github.nicolasdesnoust.marslander.core;

import java.util.Collections;
import java.util.List;

import io.github.nicolasdesnoust.marslander.math.Segment;

public class Mars {
    private final List<Segment> surface;

    public Mars(List<Segment> surface) {
        this.surface = Collections.unmodifiableList(surface);
    }

    public List<Segment> getSurface() {
        return surface;
    }
}
