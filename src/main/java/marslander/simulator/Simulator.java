package marslander.simulator;

import marslander.Capsule;
import marslander.Main;
import marslander.Mars;
import marslander.MarsService;
import marslander.math.Point;
import marslander.math.Segment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static net.logstash.logback.argument.StructuredArguments.kv;

public class Simulator {
    private static final int HORIZONTAL_SPEED_THRESHOLD = 20;
    private static final int VERTICAL_SPEED_THRESHOLD = 40;
    private static final int TURN_DURATION = 1;
    private static final double MARS_GRAVITY = -3.711;
    private static final String TEST_CASE_NAME = "test-case-04";
    private static final MarsService marsService = new MarsService();
    private static final Logger log = LoggerFactory.getLogger(Simulator.class);
    public static volatile int currentTurn = 0;
    private static SimulatorState state;

    public static void main(String[] args) throws IOException, InterruptedException {
        org.slf4j.MDC.put("turn", String.valueOf(currentTurn));
        state = parseTestCase(TEST_CASE_NAME);

        final PipedInputStream simulatorIn = new PipedInputStream();
        final PipedOutputStream simulatorOut = new PipedOutputStream();
        final PipedInputStream solverIn = new PipedInputStream();
        final PipedOutputStream solverOut = new PipedOutputStream();

        /* Connect pipes */
        simulatorIn.connect(solverOut);
        solverIn.connect(simulatorOut);

        /* Thread for solver program */
        Thread solverThread = new Thread(() -> {
            org.slf4j.MDC.put("turn", String.valueOf(Simulator.currentTurn));

            Scanner in = new Scanner(solverIn);
            PrintWriter out = new PrintWriter(solverOut, true);
            log.debug("Solver thread started");
            try {
                Main.solve(in, out);
            } catch (NoSuchElementException e) {
                log.debug("Solver thread ended");
            }
        }, "Solver");

        /* Thread for simulator program */
        Thread simulatorThread = new Thread(() -> {
            org.slf4j.MDC.put("turn", String.valueOf(Simulator.currentTurn));

            Scanner in = new Scanner(simulatorIn);
            PrintWriter out = new PrintWriter(simulatorOut, true);
            log.debug("Simulator thread started");
            out.println(state.getMars().getSurface().size());
            state.getMars().getSurface().forEach(point -> {
                out.println(Math.round(point.getX()) + " " + Math.round(point.getY()));
                log.info("Surface point: {} {} {}",
                        kv("type", "surface"),
                        kv("x", Math.round(point.getX())),
                        kv("y", Math.round(point.getY()))
                );
            });

            log.info("Initial capsule state: {}", kv("capsule", state.getCapsule()));
            log.info("Capsule position: {} {} {}",
                    kv("type", "capsule"),
                    kv("x", Math.round(state.getCapsule().getPosition().getX())),
                    kv("y", Math.round(state.getCapsule().getPosition().getY()))
            );
            log.info("Initial landing state: {}", kv("landingState", state.getLandingState()));

            while (state.getLandingState() == LandingState.STILL_FLYING) {
                out.println(String.format(
                        "%d %d %d %d %d %d %d",
                        Math.round(state.getCapsule().getPosition().getX()),
                        Math.round(state.getCapsule().getPosition().getY()),
                        Math.round(state.getCapsule().gethSpeed()),
                        Math.round(state.getCapsule().getvSpeed()),
                        Math.round(state.getCapsule().getFuel()),
                        state.getCapsule().getRotate(),
                        state.getCapsule().getPower()
                ));

                log.debug("Waiting solver answer..");
                if (in.hasNextLine()) {
                    int rotate = in.nextInt();
                    int power = in.nextInt();
                    log.debug("Received {} {}", kv("rotate", rotate), kv("power", power));
                    updateCapsuleState(rotate, power);
                }
                currentTurn++;
                org.slf4j.MDC.put("turn", String.valueOf(currentTurn));
            }
        }, "Simulator");

        /* Start thread */
        solverThread.start();
        simulatorThread.start();

        /* Join Thread */
        simulatorThread.join();
        solverThread.interrupt();
        solverThread.join();

        /* Close stream */
        simulatorIn.close();
        simulatorOut.close();
        solverIn.close();
        solverOut.close();
    }

    private static void updateCapsuleState(int requestedRotate, int requestedPower) {
        int newRotate = computeNewLimitedValue(requestedRotate, state.getCapsule().getRotate(), 15);
        state.getCapsule().setRotate(newRotate);

        int newPower = state.getCapsule().getFuel() == 0 ?
                0 : computeNewLimitedValue(requestedPower, state.getCapsule().getPower(), 1);
        state.getCapsule().setPower(newPower);
        state.getCapsule().setFuel(Math.max(state.getCapsule().getFuel() - newPower, 0));

        Point oldPosition = state.getCapsule().getPosition();
        updateCapsulePosition();
        Point currentPosition = state.getCapsule().getPosition();
        updateLandingState(oldPosition, currentPosition);
        log.info("New capsule state: {}", kv("capsule", state.getCapsule()));
        log.info("Capsule position: {} {} {}",
                kv("type", "capsule"),
                kv("x", Math.round(state.getCapsule().getPosition().getX())),
                kv("y", Math.round(state.getCapsule().getPosition().getY()))
        );
        log.info("New landing state: {}", kv("landingState", state.getLandingState()));
    }

    private static int computeNewLimitedValue(int requestedValue, int currentValue, int limit) {
        int newValue;
        if (requestedValue > currentValue) {
            newValue = Math.min(currentValue + limit, requestedValue);
        } else {
            newValue = Math.max(currentValue - limit, requestedValue);
        }
        return newValue;
    }

    private static void updateLandingState(Point oldPosition, Point currentPosition) {
        if (currentPosition.getY() > 3000
                || currentPosition.getY() < 0
                || currentPosition.getX() > 7000
                || currentPosition.getX() < 0) {
            state.setLandingState(LandingState.OUT_OF_MAP);
            return;
        }

        Segment currentPathSegment = new Segment(oldPosition, currentPosition);
        List<Point> marsSurface = state.getMars().getSurface();
        Segment landingArea = marsService.getLandingArea(state.getMars());

        Point lastPoint = marsSurface.get(0);
        for (int i = 1; i < marsSurface.size(); i++) {
            Point currentPoint = marsSurface.get(i);
            Segment currentSurfaceSegment = new Segment(lastPoint, currentPoint);
            if (currentPathSegment.doesIntersect(currentSurfaceSegment)) {
                if (currentSurfaceSegment.equals(landingArea) && isCapsuleAbleToLand()) {
                    state.setLandingState(LandingState.LANDED);
                } else {
                    state.setLandingState(LandingState.CRASHED);
                }
                break;
            }
            lastPoint = currentPoint;
        }
    }

    private static boolean isCapsuleAbleToLand() {
        int rotate = state.getCapsule().getRotate();
        int hSpeed = (int) state.getCapsule().gethSpeed();
        int vSpeed = (int) state.getCapsule().getvSpeed();

        return rotate == 0
                && hSpeed >= -HORIZONTAL_SPEED_THRESHOLD && hSpeed <= HORIZONTAL_SPEED_THRESHOLD
                && vSpeed >= -VERTICAL_SPEED_THRESHOLD && vSpeed <= VERTICAL_SPEED_THRESHOLD;
    }

    private static void updateCapsulePosition() {
        Capsule capsule = state.getCapsule();
        double x = capsule.getPosition().getX();
        double y = capsule.getPosition().getY();
        double hSpeed = capsule.gethSpeed();
        double vSpeed = capsule.getvSpeed();
        int rotate = capsule.getRotate();
        int power = capsule.getPower();

        double theta = 90.0 - Math.abs(rotate);
        double powerVectorX = Math.cos(theta) * power;
        double powerVectorY = Math.sin(theta) * power;
        double newVSpeed = vSpeed + (MARS_GRAVITY + powerVectorY) * TURN_DURATION;
        double newHSpeed = hSpeed + powerVectorX * TURN_DURATION;
        double yDisplacement = vSpeed + 0.5 * (MARS_GRAVITY + powerVectorY) * Math.pow(TURN_DURATION, 2);
        double xDisplacement = hSpeed * TURN_DURATION;

        capsule.sethSpeed(newHSpeed);
        capsule.setvSpeed(newVSpeed);
        capsule.setPosition(new Point(x + xDisplacement, y + yDisplacement));
    }

    private static SimulatorState parseTestCase(String fileName) {
        ClassLoader classLoader = Simulator.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("File " + fileName + " not found!");
        } else {
            try (Scanner in = new Scanner(inputStream)) {

                int surfaceSize = in.nextInt();
                List<Point> marsSurface = new ArrayList<>(surfaceSize);
                for (int i = 0; i < surfaceSize; i++) {
                    marsSurface.add(new Point(in.nextInt(), in.nextInt()));
                }

                int capsuleX = in.nextInt();
                int capsuleY = in.nextInt();
                int capsuleHSpeed = in.nextInt();
                int capsuleVSpeed = in.nextInt();
                int capsuleFuel = in.nextInt();
                int capsuleRotate = in.nextInt();
                int capsulePower = in.nextInt();

                return new SimulatorState(
                        new Mars(marsSurface),
                        new Capsule(
                                capsuleX, capsuleY, capsuleHSpeed, capsuleVSpeed,
                                capsuleFuel, capsuleRotate, capsulePower
                        )
                );
            }
        }
    }
}
