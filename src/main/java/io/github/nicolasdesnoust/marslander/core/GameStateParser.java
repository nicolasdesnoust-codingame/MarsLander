package io.github.nicolasdesnoust.marslander.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import io.github.nicolasdesnoust.marslander.math.Point;
import io.github.nicolasdesnoust.marslander.math.Segment;
import io.github.nicolasdesnoust.marslander.solver.MarsService;

public class GameStateParser {
	private final MarsService marsService = new MarsService();
	
    public InitialGameState parseInitialGameState(InputStream gameStateAsInputStream) {
        try (Scanner in = new Scanner(gameStateAsInputStream)) {
            return parseInitialGameState(in);
        }
    }

    public InitialGameState parseInitialGameState(Scanner in) {
        int surfaceSize = in.nextInt();
        List<Segment> marsSurface = new ArrayList<>(surfaceSize);
        Point previousPoint = new Point(in.nextInt(), in.nextInt());
        for (int i = 1; i < surfaceSize; i++) {
        	Point currentPoint = new Point(in.nextInt(), in.nextInt());
            marsSurface.add(new Segment(previousPoint, currentPoint));
            previousPoint = currentPoint;
        }

        Capsule initialCapsule = parseCurrentTurnState(in);
        Mars mars = new Mars(marsSurface);	
        Segment landingArea = marsService.findLandingArea(mars);
        return new InitialGameState(mars, initialCapsule, landingArea);
    }

    public Capsule parseCurrentTurnState(Scanner in) {
        int x = in.nextInt();
        int y = in.nextInt();
        int hSpeed = in.nextInt(); // the horizontal speed (in m/s), can be negative.
        int vSpeed = in.nextInt(); // the vertical speed (in m/s), can be negative.
        int fuel = in.nextInt(); // the quantity of remaining fuel in liters.
        int rotate = in.nextInt(); // the rotation angle in degrees (-90 to 90).
        int power = in.nextInt(); // the thrust power (0 to 4).

        return new Capsule(x, y, hSpeed, vSpeed, fuel, rotate, power);
    }
}
