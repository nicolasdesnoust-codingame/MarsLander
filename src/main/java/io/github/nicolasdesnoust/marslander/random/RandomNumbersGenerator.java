package io.github.nicolasdesnoust.marslander.random;

import java.util.SplittableRandom;

public class RandomNumbersGenerator {
	private static final SplittableRandom splittableRandom = new SplittableRandom();

    private RandomNumbersGenerator() {
    }

    public static int generateRandomIntInRange(int min, int max) {
        return splittableRandom.nextInt(min, max + 1);
    }

    public static double generateRandomDoubleInRange(double min, double max) {
        return splittableRandom.nextDouble(min, max);
    }
}
