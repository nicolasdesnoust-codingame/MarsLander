package io.github.nicolasdesnoust.marslander.genetic;

import io.github.nicolasdesnoust.marslander.config.SolverConfiguration;
import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.InitialGameState;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.genetic.services.IndividualProcessor;
import io.github.nicolasdesnoust.marslander.genetic.services.PopulationEvolver;
import io.github.nicolasdesnoust.marslander.genetic.services.PopulationGenerator;
import io.github.nicolasdesnoust.marslander.math.Point;
import io.github.nicolasdesnoust.marslander.math.Segment;
import io.github.nicolasdesnoust.marslander.solver.CapsuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

public class GeneticAlgorithm {
    private static final Logger log = LoggerFactory.getLogger(GeneticAlgorithm.class);
    public static int individualCount = 0;
    private final PopulationGenerator generator = new PopulationGenerator();
    private final PopulationEvolver evolver;
    private final IndividualProcessor processor = new IndividualProcessor();
    private final CapsuleService capsuleService = new CapsuleService();
    private final InitialGameState initialGameState;

    public GeneticAlgorithm(InitialGameState initialGameState) {
        this.initialGameState = initialGameState;
        evolver = new PopulationEvolver(initialGameState);
    }

    public List<Individual> findBestPopulation(SolverConfiguration configuration) {
        individualCount = 0;
        List<Individual> population = generator.generateRandomPopulation(configuration, initialGameState);
        population.forEach(individual -> processor.process(individual, initialGameState));

        int currentGeneration = 0;
        logPopulation(population, currentGeneration);

        boolean solutionFound = false;
        while (!solutionFound && currentGeneration <= configuration.getGenerations()) {
            population = evolver.evolve(population, configuration.getNumberOfSelections(), currentGeneration);

            if (hasASolution(population, initialGameState)) {
                solutionFound = true;
            }

            currentGeneration++;
            logPopulation(population, currentGeneration);
        }

        return population;
    }

    public void logPopulation(List<Individual> population, int generation) {
        if (generation % 10 != 0) {
            return;
        }
        for (Individual individual : population) {
            Capsule[] capsules = individual.getCapsules();
            log.debug("Individual {}: {} {} {}",
                    kv("index", individual.getId()),
                    kv("type", "evaluation"),
                    kv("generation", generation),
                    kv("evaluation", individual.getEvaluation())
            );

            for (int j = 0; j < capsules.length; j++) {
                Point capsulePosition = capsules[j].getPosition();
                log.debug("Individual {} point {}: {} {} {} {} {}",
                        individual.getId(),
                        kv("geneIndex", j),
                        kv("generation", generation),
                        kv("type", "individual-" + individual.getId()),
                        kv("x", Math.round(capsulePosition.getX())),
                        kv("y", Math.round(capsulePosition.getY())),
                        kv("capsule", capsules[j])
                );
            }
        }
    }

    /**
     * Vérifie si l'un des individus de la population forme une solution au problème.
     * Un individu est une solution si la capsule survole la zone d'atterissage avec
     * des vitesses horizontales et verticales comprises entre les bornes imposées.
     * <p>
     * NB: l'angle de rotation est négligé puisqu'il peut être corrigé manuellement
     * par la suite.
     */
    private boolean hasASolution(List<Individual> population, InitialGameState initialGameState) {
        Segment landingArea = initialGameState.getLandingArea();

        for (Individual individual : population) {
            if (isTheIndividualASolution(individual, landingArea)) {
                return true;
            }
        }

        return false;
    }

    public boolean isTheIndividualASolution(Individual individual, Segment landingArea) {
        for (Capsule capsule : individual.getCapsules()) {
            if (capsuleService.isCapsuleAboveLandingArea(capsule, landingArea)
                    && capsuleService.couldCapsuleLand(capsule)) {
                return true;
            }
        }
        return false;
    }
}
