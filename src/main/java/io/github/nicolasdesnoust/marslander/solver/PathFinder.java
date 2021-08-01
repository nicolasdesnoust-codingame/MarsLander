package io.github.nicolasdesnoust.marslander.solver;

import java.util.ArrayList;
import java.util.List;

import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.GameState;
import io.github.nicolasdesnoust.marslander.math.Point;
import io.github.nicolasdesnoust.marslander.math.Segment;

public class PathFinder {
    public List<Point> findPath(Capsule capsule, GameState initialGameState) {
        List<Point> path = buildStraightPath(capsule, initialGameState);

        Segment currentPathSegment = new Segment(path.get(0), path.get(1));
        List<Segment> marsSurface;
        Segment landingArea;
        boolean reverseDirection;
        if (currentPathSegment.getP1().getX() > currentPathSegment.getP2().getX()) {
            reverseDirection = true;
            marsSurface = initialGameState.getMarsSurfaceReversed();
            landingArea = initialGameState.getLandingAreaReversed();
        } else {
            reverseDirection = false;
            marsSurface = initialGameState.getMars().getSurface();
            landingArea = initialGameState.getLandingArea();
        }

        int pathPointIndex = 1;
        for (int i = 0; i < marsSurface.size(); i++) {
            Segment currentSurfaceSegment = marsSurface.get(i);
            if (!currentSurfaceSegment.equals(landingArea)
                    && currentPathSegment.doesIntersect(currentSurfaceSegment)) {

                int index = reverseDirection ? marsSurface.size() - pathPointIndex : pathPointIndex;
                Point newPathPoint = initialGameState.getPathPoints().get(index);

                path.add(path.size() - 1, newPathPoint);
                currentPathSegment = new Segment(newPathPoint, currentPathSegment.getP2());
                pathPointIndex++;
                i--;
            } else if (pathPointIndex == i + 1) {
                pathPointIndex++;
            }
        }

        return path;
    }

    private List<Point> buildStraightPath(Capsule capsule, GameState initialGameState) {
        List<Point> straightPath = new ArrayList<>(10);

        straightPath.add(capsule.getPosition());
        straightPath.add(initialGameState.getLandingArea().getMiddle());

        return straightPath;
    }

}
