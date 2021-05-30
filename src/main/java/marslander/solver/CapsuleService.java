package marslander.solver;

import marslander.core.Capsule;
import marslander.core.LandingState;
import marslander.core.Mars;
import marslander.math.Line;
import marslander.math.Point;
import marslander.math.Segment;

import java.util.List;

public class CapsuleService {
    public static final int MAP_HEIGHT = 3000;
    public static final int MAP_WIDTH = 7000;
    private static final double MARS_GRAVITY = -3.711;
    private static final int HORIZONTAL_SPEED_THRESHOLD = 20;
    private static final int VERTICAL_SPEED_THRESHOLD = 40;
    private static final MarsService marsService = new MarsService();

    public void updateCapsuleState(Capsule capsule, int requestedRotate, int requestedPower, Mars mars) {
        if (!capsule.getLandingState().isTerminalState()) {
            Point oldPosition = capsule.getPosition();

            updateCapsuleRotateAndPower(capsule, requestedRotate, requestedPower);
            updateCapsuleFuel(capsule);
            updateCapsulePositionAndSpeed(capsule, mars);
            updateCapsuleLandingState(capsule, oldPosition, mars);
        }
    }

    private void updateCapsuleRotateAndPower(Capsule capsule, int requestedRotate, int requestedPower) {
        int newRotate = limitRequestedValue(requestedRotate, capsule.getRotate(), 15);
        capsule.setRotate(newRotate);

        int newPower = capsule.getFuel() == 0 ?
                0 : limitRequestedValue(requestedPower, capsule.getPower(), 1);
        capsule.setPower(newPower);
    }

    private int limitRequestedValue(int requestedValue, int currentValue, int limit) {
        int newValue;
        if (requestedValue > currentValue) {
            newValue = Math.min(currentValue + limit, requestedValue);
        } else {
            newValue = Math.max(currentValue - limit, requestedValue);
        }
        return newValue;
    }

    private void updateCapsuleFuel(Capsule capsule) {
        capsule.setFuel(Math.max(capsule.getFuel() - capsule.getPower(), 0));
    }

    private void updateCapsulePositionAndSpeed(Capsule capsule, Mars mars) {
        double x = capsule.getPosition().getX();
        double y = capsule.getPosition().getY();
        double hSpeed = capsule.gethSpeed();
        double vSpeed = capsule.getvSpeed();
        int rotate = capsule.getRotate();
        int power = capsule.getPower();

        double theta = Math.toRadians(90.0 - Math.abs(rotate));
        double powerVectorX = Math.cos(theta) * power * (rotate < 0 ? 1 : -1);
        double powerVectorY = Math.sin(theta) * power;
        double newVSpeed = vSpeed + MARS_GRAVITY + powerVectorY;
        double newHSpeed = hSpeed + powerVectorX;
        double yDisplacement = vSpeed + 0.5 * (MARS_GRAVITY + powerVectorY);
        double xDisplacement = hSpeed + 0.5 * powerVectorX;
        double newX = x + xDisplacement;
        double newY = y + yDisplacement;

        capsule.sethSpeed(newHSpeed);
        capsule.setvSpeed(newVSpeed);

        if (newX > MAP_WIDTH + 1) {
            newX = MAP_WIDTH + 1.0;
        } else if (newX < 0 - 1.0) {
            newX = 0 - 1.0;
        }
        if (newY > MAP_HEIGHT + 1) {
            newY = MAP_HEIGHT + 1.0;
        } else if (newY < 0 - 1.0) {
            newY = 0 - 1.0;
        }

        Segment currentPathSegment = new Segment(new Point(x, y), new Point(newX, newY));
        List<Point> marsSurface = mars.getSurface();

        Point lastPoint = marsSurface.get(0);
        for (int i = 1; i < marsSurface.size(); i++) {
            Point currentSurfacePoint = marsSurface.get(i);
            Segment currentSurfaceSegment = new Segment(lastPoint, currentSurfacePoint);
            if (currentPathSegment.doesIntersect(currentSurfaceSegment)) {
                Point intersection = Line.calculateIntersectionPoint(
                        Line.computeLineEquation(currentPathSegment),
                        Line.computeLineEquation(currentSurfaceSegment)).get();
                newX = intersection.getX();
                newY = intersection.getY();
                break;
            }
            lastPoint = currentSurfacePoint;
        }

        capsule.setPosition(new Point(newX, newY));
    }

    private void updateCapsuleLandingState(Capsule capsule, Point oldPosition, Mars mars) {
        if (isOutOfMap(capsule)) {
            capsule.setLandingState(LandingState.OUT_OF_MAP);
        } else {
            Segment currentPathSegment = new Segment(oldPosition, capsule.getPosition());
            List<Point> marsSurface = mars.getSurface();
            Segment landingArea = marsService.findLandingArea(mars);

            Point lastPoint = marsSurface.get(0);
            for (int i = 1; i < marsSurface.size(); i++) {
                Point currentSurfacePoint = marsSurface.get(i);
                Segment currentSurfaceSegment = new Segment(lastPoint, currentSurfacePoint);
                if (currentPathSegment.doesIntersect(currentSurfaceSegment)) {
                    if (currentSurfaceSegment.equals(landingArea) && isCapsuleAbleToLand(capsule)) {
                        capsule.setLandingState(LandingState.LANDED);
                    } else {
                        capsule.setLandingState(LandingState.CRASHED);
                    }
                }
                lastPoint = currentSurfacePoint;
            }
        }
    }

    public boolean isCapsuleAboveLandingArea(Capsule capsule, Segment landingArea) {
        double capsuleX = capsule.getPosition().getX();
        return capsuleX >= landingArea.getP1().getX() && capsuleX <= landingArea.getP2().getX();
    }

    private boolean isOutOfMap(Capsule capsule) {
        Point position = capsule.getPosition();
        return position.getY() > MAP_HEIGHT
                || position.getY() < 0
                || position.getX() > MAP_WIDTH
                || position.getX() < 0;
    }

    public boolean isCapsuleAbleToLand(Capsule capsule) {
        int rotate = capsule.getRotate();
        int hSpeed = (int) capsule.gethSpeed();
        int vSpeed = (int) capsule.getvSpeed();

        return rotate == 0
                && hSpeed >= -HORIZONTAL_SPEED_THRESHOLD && hSpeed <= HORIZONTAL_SPEED_THRESHOLD
                && vSpeed >= -VERTICAL_SPEED_THRESHOLD && vSpeed <= VERTICAL_SPEED_THRESHOLD;
    }
}
