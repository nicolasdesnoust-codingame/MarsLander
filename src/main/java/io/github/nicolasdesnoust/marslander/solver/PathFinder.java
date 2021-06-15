package io.github.nicolasdesnoust.marslander.solver;

import java.util.ArrayList;
import java.util.List;

import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.InitialGameState;
import io.github.nicolasdesnoust.marslander.math.Line;
import io.github.nicolasdesnoust.marslander.math.Point;
import io.github.nicolasdesnoust.marslander.math.Segment;

public class PathFinder {
    //private static final Logger log = LoggerFactory.getLogger(PathFinder.class);

    public List<Point> findPath(Capsule capsule, InitialGameState initialGameState) {
    	List<Point> path = buildStraightPath(capsule, initialGameState);

        Segment currentPathSegment = new Segment(path.get(0), path.get(1));
        List<Segment> marsSurface;
        Segment landingArea;
        if (currentPathSegment.getP1().getX() > currentPathSegment.getP2().getX()) {
        	marsSurface = initialGameState.getMarsSurfaceReversed();
        	landingArea = initialGameState.getLandingAreaReversed();
        } else {
        	marsSurface = initialGameState.getMars().getSurface();
        	landingArea = initialGameState.getLandingArea();
        }

        for (int i = 0; i < marsSurface.size(); i++) {
        	Segment currentSurfaceSegment = marsSurface.get(i);
            if (!currentSurfaceSegment.equals(landingArea) && currentPathSegment.doesIntersect(currentSurfaceSegment)) {
                //log.debug("Path segment {} does intersect {}", currentPathSegment, currentSurfaceSegment);

                Point nextPoint = marsSurface.get(i + 1).getP1();
                Line bisectingLine = Line.getBisectingLine(currentSurfaceSegment.getP1(), currentSurfaceSegment.getP2(), nextPoint);
                //log.debug("Bisecting line: {}", bisectingLine);
                Point newPathPoint = bisectingLine.getPointAbove(currentSurfaceSegment.getP2(), 200);

                path.add(path.size() - 1, newPathPoint);
                currentPathSegment = new Segment(newPathPoint, currentPathSegment.getP2());
            }
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

        straightPath.add(capsule.getPosition()); // Est-ce que la position nécessite d'être clonée ?
        straightPath.add(initialGameState.getLandingArea().getMiddle());
        //log.debug("Straight path built between {} and {}", straightPath.get(0), straightPath.get(1));

        return straightPath;
    }
}
