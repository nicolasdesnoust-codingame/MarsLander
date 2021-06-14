package io.github.nicolasdesnoust.marslander.genetic.services;

import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.InitialGameState;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.math.Point;
import io.github.nicolasdesnoust.marslander.math.Segment;
import io.github.nicolasdesnoust.marslander.solver.CapsuleService;
import io.github.nicolasdesnoust.marslander.solver.PathFinder;

import java.util.List;

public class PopulationEvaluator {
    private static final double DISTANCE_RATIO_MAXIMUM = 0.75;
    private final PathFinder pathFinder = new PathFinder();
    private final CapsuleService capsuleService = new CapsuleService();

    /**
     * - distance du lieu du crash par rapport à la zone d'atterissage - vitesses
     * une fois dans la zone d'atterissage - % de suivi du bon cheminement
     * pré-calculé : - pour 60 genes, si l'individu dévie du chemin (déviation min/
     * max à définir) 31 fois => % de suivi = 31 * 100 / 60 = 51.67% deux solutions
     * pourront ensuite être départagées en comparant leurs quantités de fuel
     * utilisées.
     * <p>
     * pas suffisant : prendre en compte la progression dans le cheminement (le
     * nombre de points dépassés).
     */
    public void evaluate(List<Individual> population, List<Point> pathToFollow, InitialGameState initialGameState,
                         int currentGeneration) {
        for (Individual individual : population) {
            evaluateIndividual(individual, pathToFollow, initialGameState);
        }
    }

    private void evaluateIndividual(
            Individual individual,
            List<Point> pathToFollow,
            InitialGameState initialGameState) {
        int geneCount = individual.getGenes().length;
        Capsule lastCapsule = individual.getCapsules()[geneCount - 1];
        Segment landingArea = initialGameState.getLandingArea();
        double distanceRatio;
        double speedAndRotateRatio;

        double distanceCoeff = computeDistanceCoeff(pathToFollow, landingArea);

        if (capsuleService.isCapsuleAboveLandingArea(lastCapsule, landingArea)) {
            distanceRatio = DISTANCE_RATIO_MAXIMUM;
            speedAndRotateRatio = computeSpeedAndRotateRatio(individual, initialGameState, distanceRatio);
        } else {
            distanceRatio = distanceCoeff * computeDistanceRatio(individual, pathToFollow, initialGameState);
            speedAndRotateRatio = 0;
        }

        individual.setEvaluation(distanceRatio + speedAndRotateRatio);
    }

    private double computeDistanceCoeff(List<Point> pathToFollow, Segment landingArea) {
        double initialDistance = Point.computeDistance(pathToFollow);
        double crashDistance = Point.distance(landingArea.getP1(), landingArea.getP2()) / 2.0;
        return DISTANCE_RATIO_MAXIMUM * (1.0 - crashDistance / initialDistance);
    }

    /**
     * Si le chemin mene à la zone d'atterissage calculer le distanceRatio le plus
     * éloigné du centre dans cette zone
     */
    private double computeDistanceRatio(Individual individual, List<Point> pathToFollow,
                                        InitialGameState initialGameState) {
        Capsule capsuleRightBeforeCrash = getCapsuleRightBeforeTerminalState(individual);
        List<Point> path = pathFinder.findPath(capsuleRightBeforeCrash, initialGameState);
        double initialDistance = Point.computeDistance(pathToFollow);
        double crashDistance = Point.computeDistance(path);
        return 1.0 - Math.min(crashDistance, initialDistance) / initialDistance;
    }

    private double computeSpeedAndRotateRatio(Individual individual, InitialGameState initialGameState,
                                              double distanceRatio) {
        int geneCount = individual.getGenes().length;
        Capsule lastCapsule = individual.getCapsules()[geneCount - 1];

        double availableRatio = 1 - distanceRatio;

        double speedMax = 200;
        double rotateMax = 90;

        double deltaH = Math.abs(Math.min(20.0 - Math.abs(lastCapsule.gethSpeed()), 0));
        double deltaV = Math.abs(Math.min(40.0 - Math.abs(lastCapsule.getvSpeed()), 0)); // 53.90
        double deltaRotate = Math.abs(Math.min(15 - Math.abs(lastCapsule.getRotate()), 0));

        double deltaHRatio = 1 - deltaH / (speedMax - 20); // 1
        double deltaVRatio = 1 - deltaV / (speedMax - 40); // 0.66
        double deltaRotateRatio = 1 - deltaRotate / (rotateMax - 15); // 1

        double ratio = deltaHRatio * 0.5 + deltaVRatio * 0.5 + deltaRotateRatio * 0; // 0.45 + 0.45 + 0.066
        return ratio * availableRatio;
    }

    private Capsule getCapsuleRightBeforeTerminalState(Individual individual) {
        Capsule[] capsules = individual.getCapsules();

        int currentIndex = capsules.length - 1;
        Capsule currentCapsule = capsules[currentIndex];
        while (currentIndex > 0 && currentCapsule.getLandingState().isTerminalState()) {
            currentIndex--;
            currentCapsule = capsules[currentIndex];
        }
        return currentCapsule;
    }

}