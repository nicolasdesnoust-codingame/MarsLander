package marslander;

import marslander.math.Point;
import marslander.math.Segment;

import java.util.List;

public class MarsService {
    public Segment getLandingArea(Mars mars) {
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
