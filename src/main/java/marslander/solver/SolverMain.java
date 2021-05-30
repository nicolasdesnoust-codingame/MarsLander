package marslander.solver;

import marslander.core.GameStateParser;
import marslander.core.InitialGameState;
import marslander.genetic.GeneticAlgorithm;
import marslander.genetic.model.Individual;
import marslander.math.Point;
import marslander.simulator.SimulatorMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import static net.logstash.logback.argument.StructuredArguments.kv;

public class SolverMain {
    private static final Logger log = LoggerFactory.getLogger(SolverMain.class);
    private static final MarsService marsService = new MarsService();
    private static final GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();

    private SolverMain() {
    }

    @SuppressWarnings("squid:S2189")
    public static void solve(Scanner in, PrintWriter out) {
        GameStateParser gameStateParser = new GameStateParser();
        InitialGameState initialGameState = gameStateParser.parseInitialGameState(in);

        List<Individual> population = geneticAlgorithm.findBestPopulation(
                initialGameState.getCapsule(),
                50,
                125,
                initialGameState.getMars()
        );

        Individual bestSolutionFound = population.stream()
                .filter(individual -> geneticAlgorithm.isTheIndividualASolution(
                        individual,
                        marsService.findLandingArea(initialGameState.getMars())
                        )
                )
                .max(Comparator.comparingDouble(Individual::getEvaluation))
                .get();

        int nextPower = bestSolutionFound.getGenes()[0].getPower();
        int nextRotate = bestSolutionFound.getGenes()[0].getRotate();
        out.println(nextRotate + " " + nextPower);

        int turnCount = 1;
        // game loop
        while (true) {
            org.slf4j.MDC.put("turn", String.valueOf(SimulatorMain.currentTurn));
            gameStateParser.parseCurrentTurnState(in);

            nextPower = bestSolutionFound.getGenes()[turnCount].getPower();
            nextRotate = bestSolutionFound.getGenes()[turnCount].getRotate();

            // 2 integers: rotate power. rotate is  the desired rotation angle (should be 0 for level 1), power is the desired thrust power (0 to 4).
            out.println(nextRotate + " " + nextPower);
            turnCount++;
        }
    }
}
