package io.github.nicolasdesnoust.marslander.solver;

import static net.logstash.logback.argument.StructuredArguments.kv;

import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nicolasdesnoust.marslander.config.SolverConfiguration;
import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.GameStateParser;
import io.github.nicolasdesnoust.marslander.core.InitialGameState;
import io.github.nicolasdesnoust.marslander.genetic.GeneticAlgorithm;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.simulator.SimulatorService;

public class SolverMain {
    private static final Logger log = LoggerFactory.getLogger(SolverMain.class);

    private static final CapsuleService capsuleService = new CapsuleService();
    private static final PidService pidService = new PidService();
    private static final GameStateParser gameStateParser = new GameStateParser();

    private SolverMain() {
    }

    @SuppressWarnings("squid:S2189")
    public static void solve(Scanner in, PrintWriter out, SolverConfiguration configuration) {
        InitialGameState initialGameState = gameStateParser.parseInitialGameState(in);

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(initialGameState);
        List<Individual> population = geneticAlgorithm.findBestPopulation(configuration);
        Optional<Individual> bestSolutionFoundOptional = findBestSolution(initialGameState, geneticAlgorithm,
                population);

        Individual solution;
        boolean isARealSolution = false;
        if (bestSolutionFoundOptional.isPresent()) {
			isARealSolution = true;
            solution = bestSolutionFoundOptional.get();
        } else {
            Optional<Individual> viableSolutionFoundOptional = findViableSolution(population);
            if (viableSolutionFoundOptional.isPresent()) {
                solution = viableSolutionFoundOptional.get();
                System.err.println(solution);
            } else {
                throw new RuntimeException("No solution found using genetic algorithm");
            }
        }
        logBestSolutionFound(solution);

        int nextPower = solution.getCapsules()[0].getPower();
        int nextRotate = solution.getCapsules()[0].getRotate();
        nextPower = Math.max(Math.min(nextPower, 4), 0);
        nextRotate = Math.max(Math.min(nextRotate, 90), -90);
        out.println(nextRotate + " " + nextPower);

        String strategy = "genetic";
        int turnCount = 1;
        // game loop
        while (true) {
            org.slf4j.MDC.put("turn", String.valueOf(SimulatorService.currentTurn));
            Capsule capsule = gameStateParser.parseCurrentTurnState(in);

            if (capsuleService.isCapsuleAboveLandingArea(capsule, initialGameState.getLandingArea())
                    && (isARealSolution && capsuleService.couldCapsuleLand(capsule)
					|| !isARealSolution
					&& capsule.getPosition().getY() - initialGameState.getLandingArea().getP1().getY() >= 200)) {
                strategy = "pid";
            }

            if (strategy.equals("genetic")) {
                nextPower = solution.getCapsules()[turnCount].getPower();
                nextRotate = solution.getCapsules()[turnCount].getRotate();
                nextPower = Math.max(Math.min(nextPower, 4), 0);
                nextRotate = Math.max(Math.min(nextRotate, 90), -90);
            } else {
                nextPower = pidService.pidNextPower(capsule, initialGameState);
                nextRotate = pidService.pidNextRotate(capsule, initialGameState);
            }

            capsuleService.updateCapsuleState(capsule, nextRotate, nextPower, initialGameState);
            if(capsule.getPosition().getY() <= initialGameState.getLandingArea().getP1().getY()) {
                nextRotate = 0;
            }
            // 2 integers: rotate power. rotate is the desired rotation angle (should be 0
            // for level 1), power is the desired thrust power (0 to 4).
            out.println(nextRotate + " " + nextPower);
            turnCount++;
        }
    }

    private static Optional<Individual> findBestSolution(InitialGameState initialGameState,
                                                         GeneticAlgorithm geneticAlgorithm,
                                                         List<Individual> population) {
        return population.stream()
                .filter(individual -> geneticAlgorithm.isTheIndividualASolution(
                        individual,
                        initialGameState.getLandingArea()))
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
}
