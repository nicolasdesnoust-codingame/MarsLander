package io.github.nicolasdesnoust.marslander.solver;

import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.InitialGameState;
import io.github.nicolasdesnoust.marslander.math.Segment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public class PidService {
    private static final int HORIZONTAL_SPEED_THRESHOLD = 15;
    private static final int VERTICAL_SPEED_THRESHOLD = 32;

    public int pidNextPower(Capsule capsule, InitialGameState initialGameState) {
        List<Optional<Integer>> powerOptionals = new ArrayList<>();
        powerOptionals.add(getIdealPowerForVerticalSpeed(capsule));
        powerOptionals.add(getIdealPowerForHorizontalSpeed(capsule));
        powerOptionals.add(getIdealPowerForHorizontalPosition(capsule, initialGameState.getLandingArea()));

        OptionalDouble averagePower = powerOptionals.stream()
                .filter(Optional::isPresent)
                .mapToInt(Optional::get)
                .average();

        return (int) Math.round(averagePower.orElse(0));
    }

    private Optional<Integer> getIdealPowerForVerticalSpeed(Capsule capsule) {
        if (capsule.getvSpeed() > VERTICAL_SPEED_THRESHOLD) {
            return Optional.of(0);
        } else if (capsule.getvSpeed() < -VERTICAL_SPEED_THRESHOLD) {
            return Optional.of(4);
        }
        return Optional.empty();
    }

    private Optional<Integer> getIdealPowerForHorizontalSpeed(Capsule capsule) {
        if (capsule.gethSpeed() > HORIZONTAL_SPEED_THRESHOLD
                || capsule.gethSpeed() < -HORIZONTAL_SPEED_THRESHOLD) {
            return Optional.of(4);
        }

        return Optional.empty();
    }

    private Optional<Integer> getIdealPowerForHorizontalPosition(Capsule capsule, Segment landingArea) {
        if (capsule.getPosition().getX() < landingArea.getP1().getX() + 10
                || capsule.getPosition().getX() > landingArea.getP2().getX() - 10) {
            return Optional.of(4);
        }

        return Optional.empty();
    }

    public int pidNextRotate(Capsule capsule, InitialGameState initialGameState) {
        List<Optional<Integer>> rotateOptionals = new ArrayList<>();
        rotateOptionals.add(getIdealRotateForVerticalSpeed(capsule));
        rotateOptionals.add(getIdealRotateForHorizontalSpeed(capsule));
        rotateOptionals.add(getIdealRotateForVerticalPosition(capsule, initialGameState.getLandingArea()));
        rotateOptionals.add(getIdealRotateForHorizontalPosition(capsule, initialGameState.getLandingArea()));

        OptionalDouble averageRotate = rotateOptionals.stream()
                .filter(Optional::isPresent)
                .mapToInt(Optional::get)
                .average();

        return (int) Math.round(averageRotate.orElse(0));
    }

    private Optional<Integer> getIdealRotateForVerticalSpeed(Capsule capsule) {
        if (capsule.getvSpeed() > VERTICAL_SPEED_THRESHOLD
                || capsule.getvSpeed() < -VERTICAL_SPEED_THRESHOLD) {
            return Optional.of(0);
        }

        return Optional.empty();
    }

    private Optional<Integer> getIdealRotateForHorizontalSpeed(Capsule capsule) {
        if (capsule.gethSpeed() > 40) {
            return Optional.of(90);
        } else if (capsule.gethSpeed() < -40) {
            return Optional.of(-90);
        } else if (capsule.gethSpeed() > HORIZONTAL_SPEED_THRESHOLD) {
            return Optional.of(30);
        } else if (capsule.gethSpeed() < -HORIZONTAL_SPEED_THRESHOLD) {
            return Optional.of(-30);
        }
        return Optional.empty();
    }

    private Optional<Integer> getIdealRotateForHorizontalPosition(Capsule capsule, Segment landingArea) {
        if (capsule.getPosition().getX() < landingArea.getP1().getX() + 60) {
            return Optional.of(30);
        } else if (capsule.getPosition().getX() > landingArea.getP2().getX() - 60) {
            return Optional.of(-30);
        } else if (capsule.getPosition().getX() < landingArea.getP1().getX() + 30) {
            return Optional.of(-90);
        } else if (capsule.getPosition().getX() > landingArea.getP2().getX() - 30) {
            return Optional.of(90);
        }
        return Optional.empty();
    }

    private Optional<Integer> getIdealRotateForVerticalPosition(Capsule capsule,
                                                                Segment landingArea) {
        if (capsule.getPosition().getY() - landingArea.getP1().getY() <= 80) {
            return Optional.of(0);
        }
        return Optional.empty();
    }
}
