package io.github.nicolasdesnoust.marslander.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import io.github.nicolasdesnoust.marslander.math.Point;
import io.github.nicolasdesnoust.marslander.math.Segment;
import io.github.nicolasdesnoust.marslander.solver.MarsService;

public class GameStateParser {
    private final MarsService marsService;

    public GameStateParser(MarsService marsService) {
        this.marsService = marsService;
    }
    
    public GameState parseInitialGameState(InputStream gameStateAsInputStream) {
        try (Scanner in = new Scanner(gameStateAsInputStream)) {
            return parseInitialGameState(in);
        }
    }

    public GameState parseInitialGameState(Scanner in) {
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
  
        return new GameState(mars, initialCapsule, landingArea);
    }

    public Capsule parseCurrentTurnState(Scanner in) {
        int x = in.nextInt();
        int y = in.nextInt();
        int hSpeed = in.nextInt(); 
        int vSpeed = in.nextInt(); 
        int fuel = in.nextInt();
        int rotate = in.nextInt();
        int power = in.nextInt();

        return new Capsule(x, y, hSpeed, vSpeed, fuel, rotate, power);
    }
}
