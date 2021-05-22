package marslander;

import marslander.math.Line;
import marslander.math.Point;
import marslander.math.Segment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PathFinder {
    private static final Logger log = LoggerFactory.getLogger(PathFinder.class);
    private final MarsService marsService = new MarsService();

    public List<Point> findPath(Capsule capsule, Mars mars) {
        List<Point> path = buildStraightPath(capsule, mars);
        Segment currentPathSegment = new Segment(path.get(0), path.get(1));

        Segment landingArea = marsService.getLandingArea(mars);
        List<Point> marsSurface = mars.getSurface();

        Point lastPoint = marsSurface.get(0);
        for (int i = 1; i < marsSurface.size() - 1; i++) {
            Point currentPoint = marsSurface.get(i);
            Segment currentSurfaceSegment = new Segment(lastPoint, currentPoint);
            if (!currentSurfaceSegment.equals(landingArea) && currentPathSegment.doesIntersect(currentSurfaceSegment)) {
                log.info("Path segment {} does intersect {}", currentPathSegment, currentSurfaceSegment);

                Point nextPoint = marsSurface.get(i + 1);
                Line bisectingLine = Line.getBisectingLine(lastPoint, currentPoint, nextPoint);
                log.info("Bisecting line: {}", bisectingLine);
                Point newPathPoint = bisectingLine.getPointAbove(currentPoint, 200);

                path.add(path.size() - 1, newPathPoint);
                currentPathSegment = new Segment(newPathPoint, currentPathSegment.getP2());
            }
            lastPoint = currentPoint;
        }

        return path;
    }

    private List<Point> buildStraightPath(Capsule capsule, Mars mars) {
        List<Point> straightPath = new ArrayList<>();

        straightPath.add(capsule.getPosition());
        straightPath.add(marsService.getLandingArea(mars).getMiddle());
        log.info("Straight path built between {} and {}", straightPath.get(0), straightPath.get(1));

        return straightPath;
    }
}
