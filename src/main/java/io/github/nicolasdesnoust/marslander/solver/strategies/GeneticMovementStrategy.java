package io.github.nicolasdesnoust.marslander.solver.strategies;

import io.github.nicolasdesnoust.marslander.config.SolverConfiguration;
import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.InitialGameState;
import io.github.nicolasdesnoust.marslander.genetic.GeneticAlgorithm;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.solver.MovementInstructions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static net.logstash.logback.argument.StructuredArguments.kv;

public class GeneticMovementStrategy implements MovementStrategy {
    private static final Logger log = LoggerFactory.getLogger(GeneticMovementStrategy.class);

    private final InitialGameState initialGameState;
    private final SolverConfiguration configuration;
    private Individual solution;

    public GeneticMovementStrategy(InitialGameState initialGameState, SolverConfiguration configuration) {
        this.initialGameState = initialGameState;
        this.configuration = configuration;
    }

    private static Optional<Individual> findBestSolution(List<Individual> population) {
        return population.stream()
                .filter(Individual::isSolution)
                .max(Comparator.comparingDouble(Individual::getEvaluation));
    }

    private static Optional<Individual> findViableSolution(List<Individual> population) {
        return population.stream()
                .max(Comparator.comparingDouble(Individual::getEvaluation));
    }

    private static void logBestSolutionFound(Individual bestSolutionFound) {
        for (int i = 0; i < bestSolutionFound.getCapsules().length; i++) {
            log.info("{}: {} {}",
                    kv("type", "best-solution"),
                    kv("generation", i),
                    kv("capsule", bestSolutionFound.getCapsules()[i]));
        }
    }

    @Override
    public MovementInstructions findMovementInstructions(Capsule capsule, int turn) {
        if (solution == null) {
            solution = findASolution();
            logBestSolutionFound(solution);
        }

        int nextPower = capsule.getPower() + solution.getGenes()[turn].getPowerIncrement();
        nextPower = Math.min(Math.max(nextPower, 0), 4);
        int nextRotate = capsule.getRotate() + solution.getGenes()[turn].getRotateIncrement();
        nextRotate = Math.min(Math.max(nextRotate, -90), 90);

        return new MovementInstructions(nextRotate, nextPower);
    }

    private Individual findASolution() {
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(initialGameState);
        List<Individual> population = geneticAlgorithm.findBestPopulation(configuration);

        return Stream.of(
                findBestSolution(population),
                findViableSolution(population))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No solution found using genetic algorithm"));
    }

    @Override
    public boolean doesSolutionNeedAssistance() {
        if (solution != null) {
            Capsule lastCapsule = solution.getLastCapsule();
            return Math.abs(lastCapsule.getRotate()) > 15
                    || Math.abs(lastCapsule.gethSpeed()) > 20
                    || Math.abs(lastCapsule.getvSpeed()) > 40;
        }
        return false;
    }
}
