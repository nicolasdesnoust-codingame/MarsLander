package io.github.nicolasdesnoust.marslander.solver;

import java.util.List;

import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.GameState;
import io.github.nicolasdesnoust.marslander.core.LandingState;
import io.github.nicolasdesnoust.marslander.math.Line;
import io.github.nicolasdesnoust.marslander.math.Point;
import io.github.nicolasdesnoust.marslander.math.Segment;

public class CapsuleService {
    public static final int MAP_HEIGHT = 3000;
    public static final int MAP_WIDTH = 7000;
    public static final double MARS_GRAVITY = -3.711;
    public static final int HORIZONTAL_SPEED_THRESHOLD = 20;
    public static final int VERTICAL_SPEED_THRESHOLD = 40;
    private final SegmentChunks segmentChunks;
    private final MarsService marsService;

    private double[] cos = {
            0.0, 0.0174524064372836, 0.03489949670250108, 0.052335956242943966, 0.06975647374412523,
            0.08715574274765836, 0.10452846326765346, 0.12186934340514749, 0.13917310096006547, 0.15643446504023092,
            0.17364817766693041, 0.19080899537654492, 0.20791169081775923, 0.22495105434386514, 0.24192189559966767,
            0.25881904510252074, 0.27563735581699916, 0.29237170472273677, 0.30901699437494745, 0.32556815445715676,
            0.3420201433256688, 0.35836794954530016, 0.3746065934159122, 0.3907311284892737, 0.4067366430758004,
            0.42261826174069944, 0.43837114678907746, 0.45399049973954686, 0.46947156278589086, 0.4848096202463371,
            0.5000000000000001, 0.5150380749100544, 0.5299192642332049, 0.5446390350150272, 0.5591929034707468,
            0.573576436351046, 0.5877852522924731, 0.6018150231520483, 0.6156614753256583, 0.6293203910498375,
            0.6427876096865394, 0.6560590289905074, 0.6691306063588582, 0.6819983600624985, 0.6946583704589974,
            0.7071067811865476, 0.7193398003386512, 0.7313537016191705, 0.7431448254773942, 0.754709580222772,
            0.766044443118978, 0.7771459614569708, 0.7880107536067219, 0.7986355100472928, 0.8090169943749475,
            0.8191520442889918, 0.8290375725550417, 0.838670567945424, 0.848048096156426, 0.8571673007021123,
            0.8660254037844387, 0.8746197071393957, 0.882947592858927, 0.8910065241883679, 0.898794046299167,
            0.9063077870366499, 0.9135454576426009, 0.9205048534524404, 0.9271838545667874, 0.9335804264972017,
            0.9396926207859084, 0.9455185755993168, 0.9510565162951535, 0.9563047559630355, 0.9612616959383189,
            0.9659258262890683, 0.9702957262759965, 0.9743700647852352, 0.9781476007338057, 0.981627183447664,
            0.984807753012208, 0.9876883405951378, 0.9902680687415704, 0.992546151641322, 0.9945218953682733,
            0.9961946980917455, 0.9975640502598242, 0.9986295347545738, 0.9993908270190958, 0.9998476951563913, 1.0
    };
    private double[] sin = {
            1.0, 0.9998476951563913, 0.9993908270190958, 0.9986295347545738, 0.9975640502598242, 0.9961946980917455,
            0.9945218953682733, 0.992546151641322, 0.9902680687415704, 0.9876883405951378, 0.984807753012208,
            0.981627183447664, 0.9781476007338057, 0.9743700647852352, 0.9702957262759965, 0.9659258262890683,
            0.9612616959383189, 0.9563047559630354, 0.9510565162951535, 0.9455185755993167, 0.9396926207859083,
            0.9335804264972017, 0.9271838545667873, 0.9205048534524404, 0.9135454576426009, 0.9063077870366499,
            0.898794046299167, 0.8910065241883678, 0.8829475928589269, 0.8746197071393957, 0.8660254037844386,
            0.8571673007021122, 0.8480480961564261, 0.8386705679454239, 0.8290375725550417, 0.8191520442889918,
            0.8090169943749475, 0.7986355100472928, 0.7880107536067219, 0.7771459614569708, 0.766044443118978,
            0.7547095802227719, 0.7431448254773942, 0.7313537016191705, 0.7193398003386511, 0.7071067811865475,
            0.6946583704589973, 0.6819983600624985, 0.6691306063588582, 0.6560590289905073, 0.6427876096865393,
            0.6293203910498375, 0.6156614753256583, 0.6018150231520483, 0.5877852522924731, 0.573576436351046,
            0.5591929034707468, 0.544639035015027, 0.5299192642332049, 0.5150380749100542, 0.49999999999999994,
            0.48480962024633706, 0.4694715627858908, 0.45399049973954675, 0.4383711467890774, 0.42261826174069944,
            0.40673664307580015, 0.3907311284892737, 0.374606593415912, 0.35836794954530027, 0.3420201433256687,
            0.3255681544571567, 0.3090169943749474, 0.2923717047227367, 0.27563735581699916, 0.25881904510252074,
            0.24192189559966773, 0.22495105434386498, 0.20791169081775931, 0.1908089953765448, 0.17364817766693033,
            0.15643446504023087, 0.13917310096006544, 0.12186934340514748, 0.10452846326765346, 0.08715574274765817,
            0.0697564737441253, 0.05233595624294383, 0.03489949670250097, 0.01745240643728351, 0.0
    };

    public CapsuleService(SegmentChunks segmentChunks, MarsService marsService) {
        this.segmentChunks = segmentChunks;
        this.marsService = marsService;
    }

    public void updateCapsuleState(Capsule capsule, int requestedRotate, int requestedPower,
                                   GameState gameState) {
        if (!segmentChunks.hasBeenInitialized()) {
            segmentChunks.splitsIntoChunksVertically(gameState.getMars().getSurface());
        }

        if (!capsule.getLandingState().isTerminalState()) {
            updateCapsuleRotateAndPower(capsule, requestedRotate, requestedPower);
            updateCapsuleFuel(capsule);
            updateCapsulePositionAndSpeed(capsule, gameState);
        }
    }

    private void updateCapsuleRotateAndPower(Capsule capsule, int requestedRotate, int requestedPower) {
        int newRotate = limitRequestedValue(requestedRotate, capsule.getRotate(), 15, -90, 90);
        capsule.setRotate(newRotate);

        int newPower = capsule.getFuel() == 0 ? 0 : limitRequestedValue(requestedPower, capsule.getPower(), 1, 0, 4);
        capsule.setPower(newPower);
    }

    private int limitRequestedValue(int requestedValue, int currentValue, int limit, int minValue, int maxValue) {
        int newValue;
        if (requestedValue > currentValue) {
            newValue = Math.min(currentValue + limit, requestedValue);
        } else {
            newValue = Math.max(currentValue - limit, requestedValue);
        }

        if (newValue < minValue) {
            newValue = minValue;
        } else if (newValue > maxValue) {
            newValue = maxValue;
        }
        return newValue;
    }

    private void updateCapsuleFuel(Capsule capsule) {
        capsule.setFuel(Math.max(capsule.getFuel() - capsule.getPower(), 0));
    }

    private void updateCapsulePositionAndSpeed(Capsule capsule, GameState initialGameState) {
        double x = capsule.getPosition().getX();
        double y = capsule.getPosition().getY();
        double hSpeed = capsule.gethSpeed();
        double vSpeed = capsule.getvSpeed();
        int rotate = capsule.getRotate();
        int power = capsule.getPower();

        int absoluteRotate = Math.abs(rotate);
	    double powerVectorX = cos[absoluteRotate] * power * (rotate < 0 ? 1 : -1);
	    double powerVectorY = sin[absoluteRotate] * power;
	    double newVSpeed = vSpeed + MARS_GRAVITY + powerVectorY;
        double newHSpeed = hSpeed + powerVectorX;
        double yDisplacement = vSpeed + 0.5 * (MARS_GRAVITY + powerVectorY);
        double xDisplacement = hSpeed + 0.5 * powerVectorX;
        double newX = x + xDisplacement;
        double newY = y + yDisplacement;

        capsule.sethSpeed(newHSpeed);
        capsule.setvSpeed(newVSpeed);

        double maxHeight = marsService.findMaxHeight(initialGameState.getMars());
        if (isCapsuleOutOfMap(capsule)) {
            capsule.setLandingState(LandingState.OUT_OF_MAP);
        } else if (y <= maxHeight || newY <= maxHeight) {
            Segment currentPathSegment = new Segment(
                    new Point(x, y),
                    new Point(newX, newY));
            Segment[] marsSurface = segmentChunks.getSegmentsBetween(
                    (int) Math.min(x, newX),
                    (int) Math.max(x, newX),
                    (int) Math.min(y, newY),
                    (int) Math.max(y, newY));
            Segment landingArea = initialGameState.getLandingArea();

            for (int i = 0; i < marsSurface.length; i++) {
                Segment currentSurfaceSegment = marsSurface[i];
                if (currentPathSegment.doesIntersect(currentSurfaceSegment)) {
                    if (currentSurfaceSegment.equals(landingArea) && doesCapsuleRespectAllLimitations(capsule)) {
                        capsule.setLandingState(LandingState.LANDED);
                    } else {
                        capsule.setLandingState(LandingState.CRASHED);
                    }

                    Point intersection = Line.calculateIntersectionPoint(
                            Line.computeLineEquation(currentPathSegment.getP1(), currentPathSegment.getP2()),
                            Line.computeLineEquation(currentSurfaceSegment.getP1(), currentSurfaceSegment.getP2()))
                            .get();
                    newX = intersection.getX();
                    newY = intersection.getY();
                    break;
                }
            }
        }

        capsule.getPosition().setX(newX);
        capsule.getPosition().setY(newY);
    }

    public void rollbackPositionAndSpeedUpdate(Capsule capsule) {
        double x = capsule.getPosition().getX();
        double y = capsule.getPosition().getY();
        double hSpeed = capsule.gethSpeed();
        double vSpeed = capsule.getvSpeed();
        int rotate = capsule.getRotate();
        int power = capsule.getPower();

        int absoluteRotate = Math.abs(rotate);
        double powerVectorX = cos[absoluteRotate] * power * (rotate < 0 ? 1 : -1);
        double powerVectorY = sin[absoluteRotate] * power;
        double precedingVSpeed = vSpeed - MARS_GRAVITY - powerVectorY;
        double precedingHSpeed = hSpeed - powerVectorX;
        double yDisplacement = precedingVSpeed + 0.5 * (MARS_GRAVITY + powerVectorY);
        double xDisplacement = precedingHSpeed + 0.5 * powerVectorX;

        double precedingX = x - xDisplacement;
        double precedingY = y - yDisplacement;

        capsule.sethSpeed(precedingHSpeed);
        capsule.setvSpeed(precedingVSpeed);
        capsule.getPosition().setX(precedingX);
        capsule.getPosition().setY(precedingY);
    }

    public boolean isCapsuleRightAboveLandingArea(Capsule capsule, List<Segment> marsSurface, Segment landingArea) {

        return isCapsuleAboveLandingArea(capsule, landingArea);
    }

    public boolean isCapsuleAboveLandingArea(Capsule capsule, Segment landingArea) {
        double capsuleX = capsule.getPosition().getX();

        return capsuleX >= landingArea.getP1().getX()
                && capsuleX <= landingArea.getP2().getX();
    }

    private boolean isCapsuleOutOfMap(Capsule capsule) {
        Point position = capsule.getPosition();
        return position.getY() > MAP_HEIGHT
                || position.getY() < 0
                || position.getX() > MAP_WIDTH
                || position.getX() < 0;
    }

    public boolean doesCapsuleRespectAllLimitations(Capsule capsule) {
        int rotate = capsule.getRotate();

        return rotate == 0
                && doesCapsuleRespectSpeedLimitations(capsule);
    }

    public boolean doesCapsuleRespectSpeedLimitations(Capsule capsule) {
        double hSpeed = capsule.gethSpeed();
        double vSpeed = capsule.getvSpeed();

        return hSpeed >= -HORIZONTAL_SPEED_THRESHOLD && hSpeed <= HORIZONTAL_SPEED_THRESHOLD
                && vSpeed >= -VERTICAL_SPEED_THRESHOLD && vSpeed <= VERTICAL_SPEED_THRESHOLD;
    }

    public boolean isCapsuleAlmostASolution(Capsule capsule, List<Segment> surface, Segment landingArea) {
        return isCapsuleRightAboveLandingArea(capsule, surface, landingArea)
                && doesCapsuleRespectSpeedLimitations(capsule);
    }

}