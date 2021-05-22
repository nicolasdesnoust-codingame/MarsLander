package marslander;

import marslander.math.Point;
import marslander.simulator.Simulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static net.logstash.logback.argument.StructuredArguments.kv;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    @SuppressWarnings("squid:S2189")
    public static void solve(Scanner in, PrintWriter out) {
        int surfaceN = in.nextInt(); // the number of points used to draw the surface of Mars.
        List<Point> marsSurface = new ArrayList<>(surfaceN);
        for (int i = 0; i < surfaceN; i++) {
            int landX = in.nextInt(); // X coordinate of a surface point. (0 to 6999)
            int landY = in.nextInt(); // Y coordinate of a surface point. By linking all the points together in a sequential fashion, you form the surface of Mars.
            marsSurface.add(new Point(landX, landY));
        }
        log.info("Mars surface: {}", marsSurface);
        Mars mars = new Mars(marsSurface);
        CapsuleService capsuleService = new CapsuleService(mars);
        boolean firstTurn = true;

        // game loop
        while (true) {
            org.slf4j.MDC.put("turn", String.valueOf(Simulator.currentTurn));
            int X = in.nextInt();
            int Y = in.nextInt();
            int hSpeed = in.nextInt(); // the horizontal speed (in m/s), can be negative.
            int vSpeed = in.nextInt(); // the vertical speed (in m/s), can be negative.
            int fuel = in.nextInt(); // the quantity of remaining fuel in liters.
            int rotate = in.nextInt(); // the rotation angle in degrees (-90 to 90).
            int power = in.nextInt(); // the thrust power (0 to 4).

            Capsule capsule = new Capsule(X, Y, hSpeed, vSpeed, fuel, rotate, power);
            if (firstTurn) {
                firstTurn = false;
                List<Point> path = new PathFinder().findPath(capsule, mars);
                path.forEach(point -> {
                    log.info("Path point: {} {} {}",
                            kv("type", "path"),
                            kv("x", Math.round(point.getX())),
                            kv("y", Math.round(point.getY()))
                    );
                });
            }

            int nextPower = capsuleService.getNextPower(capsule);
            int nextRotate = capsuleService.getNextRotate(capsule);

            // 2 integers: rotate power. rotate is the desired rotation angle (should be 0 for level 1), power is the desired thrust power (0 to 4).
            out.println(nextRotate + " " + nextPower);
        }
    }
}
