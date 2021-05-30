package marslander.genetic;

import marslander.core.Capsule;
import marslander.core.Mars;
import marslander.genetic.model.GeneMetadata;
import marslander.genetic.model.Individual;
import marslander.genetic.services.IndividualProcessor;
import marslander.genetic.services.PopulationCrosser;
import marslander.genetic.services.PopulationEvaluator;
import marslander.genetic.services.PopulationGenerator;
import marslander.genetic.services.PopulationMutator;
import marslander.genetic.services.PopulationSelector;
import marslander.math.Point;
import marslander.math.Segment;
import marslander.solver.CapsuleService;
import marslander.solver.MarsService;
import marslander.solver.PathFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.logstash.logback.argument.StructuredArguments.kv;

public class GeneticAlgorithm {
    private static final double OLD_INDIVIDUALS_TO_KEEP_RATIO = 0.1;
    private static final Logger log = LoggerFactory.getLogger(GeneticAlgorithm.class);

    private final PopulationGenerator populationGenerator = new PopulationGenerator();
    private final PopulationEvaluator populationEvaluator = new PopulationEvaluator();
    private final PopulationSelector populationSelector = new PopulationSelector();
    private final PopulationCrosser populationCrosser = new PopulationCrosser();
    private final PopulationMutator populationMutator = new PopulationMutator();
    private final CapsuleService capsuleService = new CapsuleService();
    private final MarsService marsService = new MarsService();
    private final PathFinder pathFinder = new PathFinder();
    private final IndividualProcessor individualProcessor = new IndividualProcessor();

    public List<Individual> findBestPopulation(
            Capsule initialCapsule,
            int maxGenerations,
            int numberOfSelections,
            Mars mars
    ) {
        List<Individual> population = populationGenerator.generateRandomPopulation(125, 100);
        population.forEach(individual -> individualProcessor.process(individual, initialCapsule, mars));

        List<Point> pathToFollow = pathFinder.findPath(initialCapsule, mars);

        int currentGeneration = 0;
        logPopulation(population, currentGeneration);

        boolean solutionFound = false;
        while (!solutionFound && currentGeneration <= maxGenerations) {
            population = evolvePopulation(population, numberOfSelections, initialCapsule, mars, pathToFollow);

            if (hasASolution(population, mars)) {
                log.info("Solution found !");
                solutionFound = true;
            }

            currentGeneration++;
            logPopulation(population, currentGeneration);
        }

        return population;
    }

    private void logPopulation(List<Individual> population, int generation) {
        for (int i = 0; i < population.size(); i++) {
            GeneMetadata[] genesMetadata = population.get(i).getGenesMetadata();

            for (int j = 0; j < genesMetadata.length; j++) {
                Point capsulePosition = genesMetadata[j].getCapsule().getPosition();
                log.info("Individual {} point {}: {} {} {} {}",
                        i, j,
                        kv("generation", generation),
                        kv("type", "individual-" + i),
                        kv("x", Math.round(capsulePosition.getX())),
                        kv("y", Math.round(capsulePosition.getY()))
                );
            }
        }
    }

    /**
     * Représente l'évolution d'une population sur une génération.
     */
    private List<Individual> evolvePopulation(List<Individual> population, int numberOfSelections, Capsule capsule, Mars mars, List<Point> pathToFollow) {
        populationEvaluator.evaluate(population, pathToFollow, capsule, mars);
        List<Individual> newIndividuals = new ArrayList<>(population.size());
        for (int j = 0; j < numberOfSelections; j++) {
            int parent1Index = populationSelector.selectOneIndividualRandomly(population);
            int parent2Index;
            do {
                parent2Index = populationSelector.selectOneIndividualRandomly(population);
            } while (parent2Index == parent1Index);

            newIndividuals.addAll(populationCrosser.crossover(
                    population.get(parent1Index),
                    population.get(parent2Index)
            ));
        }
        populationMutator.mutate(newIndividuals);
        newIndividuals.forEach(newIndividual -> individualProcessor.process(newIndividual, capsule, mars));
        populationEvaluator.evaluate(newIndividuals, pathToFollow, capsule, mars);

        int newPopulationSize = population.size();
        Stream<Individual> oldPopulationToKeep = population.stream()
                .sorted(Comparator.comparingDouble(Individual::getEvaluation).reversed())
                .limit(Math.round(population.size() * OLD_INDIVIDUALS_TO_KEEP_RATIO));

        return Stream.concat(oldPopulationToKeep, newIndividuals.stream())
                .sorted(Comparator.comparingDouble(Individual::getEvaluation).reversed())
                .limit(newPopulationSize)
                .collect(Collectors.toList());
    }

    /**
     * Vérifie si l'un des individus de la population forme une solution au problème.
     * Un individu est une solution si la capsule survole la zone d'atterissage avec
     * des vitesses horizontales et verticales comprises entre les bornes imposées.
     * <p>
     * NB: l'angle de rotation est négligé puisqu'il peut être corrigé manuellement
     * par la suite.
     */
    private boolean hasASolution(List<Individual> population, Mars mars) {
        Segment landingArea = marsService.findLandingArea(mars);

        for (Individual individual : population) {
            if (isTheIndividualASolution(individual, landingArea)) {
                return true;
            }
        }

        return false;
    }

    public boolean isTheIndividualASolution(Individual individual, Segment landingArea) {
        for (int i = 0; i < individual.getGenes().length; i++) {
            Capsule capsule = individual.getGenesMetadata()[i].getCapsule();
            if (capsuleService.isCapsuleAboveLandingArea(capsule, landingArea)
                    && capsuleService.isCapsuleAbleToLand(capsule)) {
                return true;
            }
        }
        return false;
    }
}
