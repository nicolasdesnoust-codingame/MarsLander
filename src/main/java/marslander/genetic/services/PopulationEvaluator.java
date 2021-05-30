package marslander.genetic.services;

import marslander.core.Capsule;
import marslander.core.LandingState;
import marslander.core.Mars;
import marslander.genetic.model.GeneMetadata;
import marslander.genetic.model.Individual;
import marslander.math.Point;
import marslander.math.Segment;
import marslander.solver.CapsuleService;
import marslander.solver.MarsService;
import marslander.solver.PathFinder;

import java.util.List;

public class PopulationEvaluator {
    private static final double DEVIATION_THRESHOLD = 150; // in meters

    private final CapsuleService capsuleService = new CapsuleService();
    private final MarsService marsService = new MarsService();
    private final PathFinder pathFinder = new PathFinder();

    /**
     * - distance du lieu du crash par rapport à la zone d'atterissage
     * - vitesses une fois dans la zone d'atterissage
     * - % de suivi du bon cheminement pré-calculé :
     * - pour 60 genes, si l'individu dévie du chemin (déviation min/ max à définir)
     * 31 fois => % de suivi = 31 * 100 / 60 =  51.67%
     * deux solutions pourront ensuite être départagées en comparant leurs quantités de fuel utilisées.
     * <p>
     * pas suffisant : prendre en compte la progression dans le cheminement (le nombre de points dépassés).
     */
    public void evaluate(List<Individual> population, List<Point> pathToFollow, Capsule capsule, Mars mars) {
        capsule = new Capsule(capsule);

        for (Individual individual : population) {
            evaluateIndividual(individual, pathToFollow, mars, capsule);
        }
    }

    private void evaluateIndividual(Individual individual, List<Point> pathToFollow, Mars mars, Capsule initialCapsule) {
        int genesFollowingPathCount = 0;
        int bestPointReached = 0;

        for (GeneMetadata geneMetadata : individual.getGenesMetadata()) {
            Capsule capsule = geneMetadata.getCapsule();
            if (doesCapsuleFollowPath(capsule, pathToFollow)
                    && capsule.getLandingState() != LandingState.CRASHED
                    && capsule.getLandingState() != LandingState.OUT_OF_MAP) {
                genesFollowingPathCount++;
            }

            Point nearestPoint = capsule.getPosition().findNearestPoint(pathToFollow);
            if (Point.distance(capsule.getPosition(), nearestPoint) <= DEVIATION_THRESHOLD) {
                bestPointReached = Math.max(bestPointReached, pathToFollow.indexOf(nearestPoint));
            }
        }

        Capsule rightBeforeCrash = individual.getGenesMetadata()[individual.getGenesMetadata().length - 1].getCapsule();
        List<Point> path = pathFinder.findPath(rightBeforeCrash, mars);
        double realCrashDistance = computeDistance(path);
        //Point crashPosition = individual.getGenesMetadata()[individual.getGenesMetadata().length - 1].getCapsule().getPosition();
        Point landingPoint = marsService.findLandingArea(mars).getMiddle();
        //double crashDistance = Point.distance(crashPosition, landingPoint);
        double initialDistance = Point.distance(initialCapsule.getPosition(), landingPoint);

        double distanceRatio = 1.0 - Math.min(realCrashDistance, initialDistance) / initialDistance;
        double evaluation = computeIndividualEvaluation(individual, mars, genesFollowingPathCount, bestPointReached, distanceRatio);
        individual.setEvaluation(evaluation);
    }

    private double computeDistance(List<Point> path) {
        double distance = 0;
        Point lastPoint = path.get(0);
        for(int i = 1; i < path.size(); i++) {
            distance += Point.distance(lastPoint, path.get(i));
            lastPoint = path.get(i);
        }
        return distance;
    }

    private double computeIndividualEvaluation(Individual individual, Mars mars, int genesFollowingPathCount, int bestPointReached, double distanceRatio) {
        double genesFollowingPathCountRatio = (double) genesFollowingPathCount / individual.getGenes().length;
        double bestPointReachedRatio = (double) bestPointReached / mars.getSurface().size();
        System.err.println("Individual evaluation: [genesFollowingPathCount: " + genesFollowingPathCount
                + ", bestPointReached: " + bestPointReached
                + ", genesFollowingPathCountRatio: " + genesFollowingPathCountRatio
                + ", bestPointReachedRatio: " + bestPointReachedRatio
                + ", evaluation: " + distanceRatio + "]");
        return distanceRatio * 0.5 + genesFollowingPathCountRatio * 0.5;
    }

    private boolean doesCapsuleFollowPath(Capsule capsule, List<Point> pathToFollow) {
        Segment nearestSegment = findNearestSegment(capsule, pathToFollow);
        double distance = nearestSegment.distanceTo(capsule.getPosition());
        //if (distance <= DEVIATION_THRESHOLD) {
        //System.err.println("Segment " + nearestSegment + " is the nearest to " + capsule.getPosition() + "with a distance of " + distance + " ls: " + capsule.getLandingState());
        //}
        return distance <= DEVIATION_THRESHOLD;
    }

    private Segment findNearestSegment(Capsule capsule, List<Point> pathToFollow) {
        Segment currentPathSegment = new Segment(pathToFollow.get(0), pathToFollow.get(1));
        Segment nearestSegment = currentPathSegment;
        double nearestSegmentDistance = currentPathSegment.distanceTo(capsule.getPosition());
        //System.err.println("Distance between segment " + currentPathSegment + " and " + capsule.getPosition() + " is " + nearestSegmentDistance);

        for (int i = 1; i < pathToFollow.size() - 1; i++) {
            currentPathSegment = new Segment(pathToFollow.get(i), pathToFollow.get(i + 1));
            double currentPathSegmentDistance = currentPathSegment.distanceTo(capsule.getPosition());
            //System.err.println("Distance between segment " + currentPathSegment + " and " + capsule.getPosition() + " is " + currentPathSegmentDistance);
            if (currentPathSegmentDistance < nearestSegmentDistance) {
                nearestSegmentDistance = currentPathSegmentDistance;
                nearestSegment = currentPathSegment;
            }
        }

        return nearestSegment;
    }

    /*public void normalize(List<Individual> population) {
        double evaluationsSum = population.stream()
                .mapToDouble(Individual::getEvaluation)
                .sum();
        for (Individual individual : population) {
            individual.setEvaluation(individual.getEvaluation() / evaluationsSum);
        }
        population.sort(Comparator.comparingDouble(Individual::getEvaluation));

        Individual[] myArray = new Individual[population.size()];
        population.toArray(myArray);
        Arrays.parallelPrefix(myArray, (pair1, pair2) -> {
            pair1.setEvaluation(pair1.getEvaluation() + pair2.getEvaluation());
            return pair1;
        });
        List<Individual> list2 = Arrays.asList(myArray);

        normalizedEvaluations = Arrays.stream(list2)
                .mapToDouble(pair -> pair.evaluation)
                .toArray();
    }*/
}
