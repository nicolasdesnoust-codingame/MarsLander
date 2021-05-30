package marslander.core;

import marslander.math.Point;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameStateParser {
    public InitialGameState parseInitialGameState(InputStream gameStateAsInputStream) {
        try (Scanner in = new Scanner(gameStateAsInputStream)) {
            return parseInitialGameState(in);
        }
    }

    public InitialGameState parseInitialGameState(Scanner in) {
        int surfaceSize = in.nextInt();
        List<Point> marsSurface = new ArrayList<>(surfaceSize);
        for (int i = 0; i < surfaceSize; i++) {
            marsSurface.add(new Point(in.nextInt(), in.nextInt()));
        }

        Capsule initialCapsule = parseCurrentTurnState(in);

        return new InitialGameState(new Mars(marsSurface), initialCapsule);
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
