package io.github.nicolasdesnoust.marslander.solver;

import io.github.nicolasdesnoust.marslander.core.Mars;
import io.github.nicolasdesnoust.marslander.math.Point;
import io.github.nicolasdesnoust.marslander.math.Segment;

import java.util.List;

public class MarsService {
    public Segment findLandingArea(Mars mars) {
        List<Point> surface = mars.getSurface();
        Point lastPoint = surface.get(0);

        for (int i = 1; i < surface.size(); i++) {
            Point currentPoint = surface.get(i);
            if (currentPoint.getY() == lastPoint.getY()) {
                return new Segment(lastPoint, currentPoint);
            } else {
                lastPoint = currentPoint;
            }
        }

        return null;
    }
}