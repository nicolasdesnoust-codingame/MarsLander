package marslander;

import marslander.math.Point;
import marslander.math.Segment;

public class CapsuleService {
    private final Mars mars;
    private final MarsService marsService;

    public CapsuleService(Mars mars) {
        this.mars = mars;
        this.marsService = new MarsService();
    }

    public int getNextRotate(Capsule capsule) {
        Segment landingArea = marsService.getLandingArea(mars);

        if (capsule.getPosition().getX() > landingArea.getP1().getX()
                && capsule.getPosition().getY() < landingArea.getP2().getX()) {
            if (capsule.gethSpeed() > 15) {
                return 30;
            } else if (capsule.gethSpeed() < -15) {
                return -30;
            } else {
                return 0;
            }
        } else if (capsule.getPosition().getX() <= landingArea.getP1().getX()) {
            double distanceFromA = landingArea.getP1().getX() - capsule.getPosition().getX();
            if (distanceFromA >= 1500) {
                return -30;
            } else if (distanceFromA >= 1000) {
                return -15;
            } else {
                return -5;
            }
        } else {
            double distanceFromB = Math.abs(landingArea.getP2().getX() - capsule.getPosition().getX());
            if (distanceFromB >= 1500) {
                return 30;
            } else if (distanceFromB >= 1000) {
                return 15;
            } else {
                return 5;
            }
        }
    }


    public int getNextPower(Capsule capsule) {
        Segment landingArea = marsService.getLandingArea(mars);

        if (capsule.getvSpeed() > 35
                || (capsule.gethSpeed() > 40 && capsule.getPosition().getX() <= landingArea.getP1().getX())
                || (capsule.gethSpeed() < -40 && capsule.getPosition().getX() >= landingArea.getP2().getX())) {
            return 0;
        } else if (capsule.getvSpeed() < -35
                || (capsule.gethSpeed() < -40 && capsule.getPosition().getX() < landingArea.getP1().getX())
                || (capsule.gethSpeed() > 40 && capsule.getPosition().getX() > landingArea.getP2().getX())) {
            return 4;
        } else {
            return 2;
        }
    }

    public double computeDistanceBetween(Point point1, Point point2) {
        return Math.sqrt(Math.pow(point2.getX() - point1.getX(), 2) + Math.pow(point2.getY() - point1.getY(), 2));
    }
}
