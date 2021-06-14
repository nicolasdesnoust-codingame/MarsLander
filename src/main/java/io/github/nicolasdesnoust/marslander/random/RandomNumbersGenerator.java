package io.github.nicolasdesnoust.marslander.random;

import java.util.Random;

public class RandomNumbersGenerator {
    private static final Random random = new Random();

    private RandomNumbersGenerator() {
    }

    public static int generateRandomIntInRange(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public static double generateRandomDoubleInRange(int min, int max) {
        return min + (max - min) * random.nextDouble();
    }
}
