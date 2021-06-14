package io.github.nicolasdesnoust.marslander.genetic.services;

import static net.logstash.logback.argument.StructuredArguments.kv;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nicolasdesnoust.marslander.core.InitialGameState;
import io.github.nicolasdesnoust.marslander.genetic.GeneticAlgorithm;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.math.Point;
import io.github.nicolasdesnoust.marslander.solver.PathFinder;

public class PopulationEvolver {
	private static final Logger log = LoggerFactory.getLogger(PopulationEvolver.class);

	private static final double OLD_INDIVIDUALS_TO_KEEP_RATIO = 0.1;

	private final PopulationEvaluator evaluator = new PopulationEvaluator();
	private final PopulationSelector selector = new PopulationSelector();
	private final PopulationCrosser crosser = new PopulationCrosser();
	private final PopulationMutator mutator = new PopulationMutator();
	private final IndividualProcessor processor = new IndividualProcessor();

	private final InitialGameState initialGameState;
	private final List<Point> pathToFollow;

	public PopulationEvolver(InitialGameState initialGameState) {
		this.initialGameState = initialGameState;
		this.pathToFollow = new PathFinder().findPath(initialGameState.getCapsule(), initialGameState);
	}

	/**
	 * Représente l'évolution d'une population sur une génération.
	 */
	public List<Individual> evolve(List<Individual> population, int numberOfSelections, int currentGeneration) {
		System.err.print("Initial population eval : ");
		population.stream()
				.sorted(Comparator.comparingDouble(Individual::getEvaluation).reversed())
				.forEach(e -> System.err.print(String.format("[%.2g %d] ", e.getEvaluation(), e.getId())));
		System.err.println();

		if (currentGeneration == 0) {
			evaluator.evaluate(population, pathToFollow, initialGameState, currentGeneration);
		}
		System.err.print("Initial population eval2 : ");
		population.stream()
				.sorted(Comparator.comparingDouble(Individual::getEvaluation).reversed())
				.forEach(e -> System.err.print(String.format("[%.2g %d] ", e.getEvaluation(), e.getId())));
		System.err.println();

		population.sort(Comparator.comparingDouble(Individual::getEvaluation));
		List<Individual> newIndividuals = makeNewIndividuals(population, numberOfSelections);
		evaluator.evaluate(newIndividuals, pathToFollow, initialGameState, currentGeneration);

		return mergeIndividuals(population, newIndividuals);
	}

	private List<Individual> makeNewIndividuals(List<Individual> population, int numberOfSelections) {
		List<Individual> newIndividuals = new ArrayList<>(population.size());
		for (int j = 0; j < numberOfSelections; j++) {
			int parent1Index = selector.selectOneIndividualRandomly(population);
			int parent2Index;
			do {
				parent2Index = selector.selectOneIndividualRandomly(population);
			} while (parent2Index == parent1Index);

			newIndividuals.addAll(crosser.crossover(
					population.get(parent1Index),
					population.get(parent2Index)));
		}
		mutator.mutate(newIndividuals);
		newIndividuals.forEach(newIndividual -> processor.process(newIndividual, initialGameState));
		return newIndividuals;
	}

	private List<Individual> mergeIndividuals(List<Individual> population, List<Individual> newIndividuals) {
		int newPopulationSize = population.size();

		System.err.print("Population eval :         ");
		population.stream()
				.sorted(Comparator.comparingDouble(Individual::getEvaluation).reversed())
				.forEach(e -> System.err.print(String.format("[%.2g %d] ", e.getEvaluation(), e.getId())));
		System.err.println();

		Stream<Individual> oldPopulationToKeep = population.stream()
				.sorted(Comparator.comparingDouble(Individual::getEvaluation).reversed())
				.limit(Math.round(population.size() * OLD_INDIVIDUALS_TO_KEEP_RATIO));

		System.err.print("Population keep :         ");
		population.stream()
				.sorted(Comparator.comparingDouble(Individual::getEvaluation).reversed())
				.limit(Math.round(population.size() * OLD_INDIVIDUALS_TO_KEEP_RATIO))
				.forEach(e -> {
					System.err.print(String.format("[%.2g %d] ", e.getEvaluation(), e.getId()));
					System.err.print(" " + e.getGenes()[e.getGenes().length - 1] + "\n");
				});
		System.err.println();

		List<Individual> newPop = Stream.concat(oldPopulationToKeep, newIndividuals.stream())
				.sorted(Comparator.comparingDouble(Individual::getEvaluation).reversed())
				.limit(newPopulationSize)
				.collect(Collectors.toList());

		System.err.print("New population :          ");
		newPop.stream()
				.forEach(e -> System.err.print(String.format("[%.2g %d] ", e.getEvaluation(), e.getId())));
		System.err.println();

		return newPop;
	}
}
