package io.github.nicolasdesnoust.marslander.solver;

import java.util.List;

import io.github.nicolasdesnoust.marslander.core.Mars;
import io.github.nicolasdesnoust.marslander.math.Segment;

public class MarsService {
    public Segment findLandingArea(Mars mars) {
        List<Segment> surface = mars.getSurface();
     
        for (Segment segment : surface) {
            if (segment.isHorizontal()) {
                return segment;
            }
        }

        return null;
    }
}