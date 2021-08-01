package io.github.nicolasdesnoust.marslander.genetic;

import java.util.List;

import io.github.nicolasdesnoust.marslander.SolverConfiguration;
import io.github.nicolasdesnoust.marslander.core.GameState;
import io.github.nicolasdesnoust.marslander.core.Timer;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.genetic.services.IndividualProcessor;
import io.github.nicolasdesnoust.marslander.genetic.services.PopulationEvolver;
import io.github.nicolasdesnoust.marslander.genetic.services.PopulationGenerator;

public class GeneticAlgorithm {
	public static int individualCount = 0;
	private int currentGeneration;
	private final PopulationGenerator generator;
	private final PopulationEvolver evolver;
	private final IndividualProcessor processor;

	public GeneticAlgorithm(
			PopulationGenerator generator,
			PopulationEvolver evolver,
			IndividualProcessor processor) {
		this.generator = generator;
		this.evolver = evolver;
		this.processor = processor;
	}

	public List<Individual> initializePopulation(
			SolverConfiguration configuration,
			GameState initialGameState) {
		
		evolver.updateInitialGameState(initialGameState);
		List<Individual> population = generator.generateRandomPopulation(configuration, initialGameState);
		processor.process(population, initialGameState);
		
		population = evolver.evolve(
				population,
				configuration.getNumberOfSelections(),
				currentGeneration,
				initialGameState,
				true);

		currentGeneration++;
		return population;
	}

	public List<Individual> improvePopulation(
			List<Individual> population,
			SolverConfiguration configuration,
			GameState initialGameState) {

		while (Timer.stillHaveTime()) {
			population = evolver.evolve(
					population,
					configuration.getNumberOfSelections(),
					currentGeneration,
					initialGameState,
					false);

			// if(currentGeneration % 10 == 0)
			// System.err.println(currentGeneration);
			currentGeneration++;
		}

		return population;
	}

}
