package io.github.nicolasdesnoust.marslander.genetic;

import java.util.Comparator;
import java.util.List;

import io.github.nicolasdesnoust.marslander.SolverConfiguration;
import io.github.nicolasdesnoust.marslander.core.GameState;
import io.github.nicolasdesnoust.marslander.core.Timer;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.genetic.services.IndividualProcessor;
import io.github.nicolasdesnoust.marslander.genetic.services.PopulationEvaluator;
import io.github.nicolasdesnoust.marslander.genetic.services.PopulationEvolver;
import io.github.nicolasdesnoust.marslander.genetic.services.PopulationGenerator;
import io.github.nicolasdesnoust.marslander.genetic.services.PopulationMutator;
import io.github.nicolasdesnoust.marslander.logs.IndividualLogger;
import io.github.nicolasdesnoust.marslander.math.Point;

public class GeneticAlgorithm {
	public static int individualCount = 0;
	private int currentGeneration;
	private final PopulationGenerator generator;
	private final PopulationEvolver evolver;
	private final IndividualProcessor processor;
	private final IndividualLogger individualLogger;
	private final PopulationEvaluator evaluator;
	private final PopulationMutator mutator;

	public GeneticAlgorithm(
			PopulationGenerator generator,
			PopulationEvolver evolver,
			IndividualProcessor processor,
			IndividualLogger individualLogger,
			PopulationEvaluator evaluator,
			PopulationMutator mutator) {
		this.generator = generator;
		this.evolver = evolver;
		this.processor = processor;
		this.individualLogger = individualLogger;
		this.evaluator = evaluator;
		this.mutator = mutator;
	}
	
	public List<Individual> initializePopulation(
			SolverConfiguration configuration,
			GameState initialGameState) {

		return initializePopulation(configuration, initialGameState, null);
	}

	public List<Individual> initializePopulation(
			SolverConfiguration configuration,
			GameState initialGameState,
			Individual individualToInject) {

		List<Point> pathToFollow = evolver.updateInitialGameState(initialGameState);
		List<Individual> population = generator.generateRandomPopulation(configuration, initialGameState);
		if(individualToInject != null) {
			population.add(individualToInject);
		}
		processor.process(population, initialGameState);

		evaluator.evaluate(population, pathToFollow, initialGameState);
		individualLogger.addMissingEvaluations(population);
		population.sort(Comparator.comparingDouble(Individual::getEvaluation));

		individualLogger.addMissingEvaluations(population);
		individualLogger.logGeneration(population, currentGeneration);

		currentGeneration++;
		population = evolver.evolve(
				population,
				currentGeneration,
				initialGameState, 
				configuration);

		individualLogger.addMissingEvaluations(population);
		individualLogger.logGeneration(population, currentGeneration);

		return population;
	}

	public List<Individual> improvePopulation(
			List<Individual> population,
			GameState initialGameState,
			SolverConfiguration configuration) {

		while (Timer.stillHaveTime()) {
			currentGeneration++;
			population = evolver.evolve(
					population,
					currentGeneration,
					initialGameState,
					configuration);

			individualLogger.addMissingEvaluations(population);
			individualLogger.logGeneration(population, currentGeneration);

			// if(currentGeneration % 10 == 0)
			// System.err.println(currentGeneration);
		}

		return population;
	}
}
