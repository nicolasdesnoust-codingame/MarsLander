package io.github.nicolasdesnoust.marslander.solver.strategies;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import io.github.nicolasdesnoust.marslander.SolverConfiguration;
import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.GameState;
import io.github.nicolasdesnoust.marslander.core.LandingState;
import io.github.nicolasdesnoust.marslander.genetic.GeneticAlgorithm;
import io.github.nicolasdesnoust.marslander.genetic.IndividualFactory;
import io.github.nicolasdesnoust.marslander.genetic.model.Gene;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.solver.CapsuleService;
import io.github.nicolasdesnoust.marslander.solver.MovementInstructions;

public class GeneticMovementStrategy implements MovementStrategy {
	public static final String STRATEGY_KEY = "GENETIC";
	private final SolverConfiguration configuration;
	private final GeneticAlgorithm geneticAlgorithm;
	private final CapsuleService capsuleService;
	private final IndividualFactory individualFactory;
	private Individual currentSolution;
	private List<Individual> currentPopulation;

	private int attemptCount = 0;
	private int currentGeneIndex = 0;

	public GeneticMovementStrategy(
			SolverConfiguration configuration,
			GeneticAlgorithm geneticAlgorithm,
			CapsuleService capsuleService,
			IndividualFactory individualFactory) {
		this.configuration = configuration;
		this.geneticAlgorithm = geneticAlgorithm;
		this.capsuleService = capsuleService;
		this.individualFactory = individualFactory;
	}

	public MovementInstructions findMovementInstructions(Capsule capsule, int turn, GameState initialGameState) {
		int restartGenericTurn = configuration.getRestartGenericTurn();

		if (turn == 0) {
			currentSolution = new Individual(findATemporarySolution(initialGameState));
			// System.err.println("Initial solution evaluation : "
			// + currentSolution.getEvaluation()
			// + ", fuel: " + currentSolution.getCapsule().getFuel()
			// + ", ls: " + currentSolution.getCapsule().getLandingState());
		} else if (configuration.isRestartGeneric()) {
			if (turn == 1 + restartGenericTurn * attemptCount) {
				currentPopulation = initializeNewPopulation(initialGameState, capsule, turn);
				improveNewPopulation(initialGameState, currentPopulation);
			} else if (turn >= 2 + restartGenericTurn * attemptCount
					&& turn <= restartGenericTurn * (attemptCount + 1) - 1) {
				improveNewPopulation(initialGameState, currentPopulation);
			} else if (turn == restartGenericTurn * (attemptCount + 1)) {
				Individual newSolution = new Individual(findASolutionIn(currentPopulation));
				// System.err.println("Old solution evaluation : " +
				// currentSolution.getEvaluation() + ", fuel: " +
				// currentSolution.getCapsule().getFuel());
				// System.err.println("New solution evaluation : " + newSolution.getEvaluation()
				// + ", fuel: " + newSolution.getCapsule().getFuel());
				currentSolution = getBestOf(currentSolution, newSolution);
				attemptCount++;
			}
		}
		return getMovementInstructionsFrom(capsule, currentSolution, turn);
	}

	private Individual findATemporarySolution(GameState initialGameState) {
		List<Individual> population = geneticAlgorithm.initializePopulation(configuration, initialGameState);
		population = geneticAlgorithm.improvePopulation(population, initialGameState, configuration);

		return findASolutionIn(population);
	}

	private List<Individual> initializeNewPopulation(GameState initialGameState, Capsule capsule, int turn) {
		Capsule newInitialCapsule = new Capsule(capsule);
		Gene[] genes = currentSolution.getGenes();
		int restartGenericTurn = configuration.getRestartGenericTurn();
		for (int i = currentGeneIndex; i < currentGeneIndex + restartGenericTurn - 1; i++) {
			Gene gene = genes[i];
			int requestedRotate = newInitialCapsule.getRotate() + gene.getRotateIncrement();
			int requestedPower = newInitialCapsule.getPower() + gene.getPowerIncrement();
			capsuleService.updateCapsuleState(newInitialCapsule, requestedRotate, requestedPower, initialGameState);
		}
		initialGameState.setCapsule(newInitialCapsule);
//		Individual individualToInject = new Individual(currentSolution, currentGeneIndex + restartGenericTurn - 1);
		return geneticAlgorithm.initializePopulation(configuration, initialGameState, null);
	}

	private Individual findASolutionIn(List<Individual> population) {
		Individual solution = findBestSolution(population)
				.orElseThrow(() -> new RuntimeException("No solution found"));
		individualFactory.release(population);
		return solution;
	}

	private static Optional<Individual> findBestSolution(List<Individual> population) {
		return population.stream()
				.max(Comparator.comparingDouble(Individual::getEvaluation));
	}

	private void improveNewPopulation(GameState initialGameState, List<Individual> population) {
		currentPopulation = geneticAlgorithm.improvePopulation(
				population,
				initialGameState,
				configuration);
	}

	private MovementInstructions getMovementInstructionsFrom(Capsule capsule, Individual solution, int turn) {
		Gene currentGene = solution.getGenes()[currentGeneIndex];
		int nextPower = capsule.getPower() + currentGene.getPowerIncrement();
		nextPower = Math.min(Math.max(nextPower, 0), 4);
		int nextRotate = capsule.getRotate() + currentGene.getRotateIncrement();
		nextRotate = Math.min(Math.max(nextRotate, -90), 90);
		currentGeneIndex++;
		return new MovementInstructions(nextRotate, nextPower);
	}

	private Individual getBestOf(Individual solution1, Individual solution2) {
		if (solution1.getCapsule().getLandingState() == LandingState.LANDED
				&& solution2.getCapsule().getLandingState() != LandingState.LANDED) {
			return solution1;
		} else if (solution1.getCapsule().getLandingState() != LandingState.LANDED
				&& solution2.getCapsule().getLandingState() == LandingState.LANDED) {
			currentGeneIndex = 0;
			return solution2;
		}
		if (solution1.getEvaluation() >= 2.05 && solution2.getEvaluation() >= 2.05) {
			if (solution1.getCapsule().getFuel() >= solution2.getCapsule().getFuel()) {
				return solution1;
			} else {
				currentGeneIndex = 0;
				return solution2;
			}
		} else if (solution1.getEvaluation() >= solution2.getEvaluation()) {
			return solution1;
		} else {
			currentGeneIndex = 0;
			return solution2;
		}
	}

	public boolean doesSolutionNeedAssistance() {
		return currentSolution.getCapsule().getLandingState() != LandingState.LANDED;
	}

}