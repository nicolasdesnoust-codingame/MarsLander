package io.github.nicolasdesnoust.marslander.core;

import java.time.Duration;
import java.time.Instant;

public class Timer {
    private static final long SAFE_MARGIN = -55;
    private static final long FIRST_TURN_DURATION_IN_MS = 1000;
    private static final long TURN_DURATION_IN_MS = 100;
    private static long currentTurnDuration = FIRST_TURN_DURATION_IN_MS;
    private static Instant turnStart = Instant.now();

    private Timer() {}
    
    public static void startFirstTurnTimer() {
        turnStart = Instant.now();
    }

    public static void startNewTurnTimer() {
        currentTurnDuration = TURN_DURATION_IN_MS;
        turnStart = Instant.now();
    }

    public static boolean stillHaveTime() {
        Instant now = Instant.now();
        long timeElapsed = Duration.between(turnStart, now).toMillis();
        if (timeElapsed >= currentTurnDuration - SAFE_MARGIN) {
            System.err.println("OUT OF TIME! " + timeElapsed + " ms");
            return false;
        }
        return true;
    }

    public static void logElapsedTime(String message) {
        Instant now = Instant.now();
        long timeElapsed = Duration.between(turnStart, now).toMillis();
        System.err.println(message + " - Elapsed time: " + timeElapsed + " ms");
    }
}