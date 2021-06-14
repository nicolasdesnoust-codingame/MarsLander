package io.github.nicolasdesnoust.marslander.solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.InitialGameState;
import io.github.nicolasdesnoust.marslander.core.Mars;
import io.github.nicolasdesnoust.marslander.math.Line;
import io.github.nicolasdesnoust.marslander.math.Point;
import io.github.nicolasdesnoust.marslander.math.Segment;

public class PathFinder {
    //private static final Logger log = LoggerFactory.getLogger(PathFinder.class);

    public List<Point> findPath(Capsule capsule, InitialGameState initialGameState) {
    	Mars mars = initialGameState.getMars();
        Segment landingArea = initialGameState.getLandingArea();

    	List<Point> path = buildStraightPath(capsule, initialGameState);

        Segment currentPathSegment = new Segment(path.get(0), path.get(1));
        List<Point> marsSurface = new ArrayList<>(mars.getSurface());
        if (currentPathSegment.getP1().getX() > currentPathSegment.getP2().getX()) {
            Collections.reverse(marsSurface);
            Point p1 = landingArea.getP1();
            landingArea.setP1(landingArea.getP2());
            landingArea.setP2(p1);
        }
        Point lastPoint = marsSurface.get(0);

        for (int i = 1; i < marsSurface.size() - 1; i++) {
            Point currentPoint = marsSurface.get(i);
            Segment currentSurfaceSegment = new Segment(lastPoint, currentPoint);
            if (!currentSurfaceSegment.equals(landingArea) && currentPathSegment.doesIntersect(currentSurfaceSegment)) {
                //log.debug("Path segment {} does intersect {}", currentPathSegment, currentSurfaceSegment);

                Point nextPoint = marsSurface.get(i + 1);
                Line bisectingLine = Line.getBisectingLine(lastPoint, currentPoint, nextPoint);
                //log.debug("Bisecting line: {}", bisectingLine);
                Point newPathPoint = bisectingLine.getPointAbove(currentPoint, 200);

                path.add(path.size() - 1, newPathPoint);
                currentPathSegment = new Segment(newPathPoint, currentPathSegment.getP2());
            }
            lastPoint = currentPoint;
        }

//        path.forEach(point -> log.info("Path point: {} {} {}",
//                kv("type", "path"),
//                kv("x", Math.round(point.getX())),
//                kv("y", Math.round(point.getY()))
//        ));

        return path;
    }

    private List<Point> buildStraightPath(Capsule capsule, InitialGameState initialGameState) {
        List<Point> straightPath = new ArrayList<>();

        straightPath.add(capsule.getPosition());
        straightPath.add(initialGameState.getLandingArea().getMiddle());
        //log.debug("Straight path built between {} and {}", straightPath.get(0), straightPath.get(1));

        return straightPath;
    }
}
