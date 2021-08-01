package io.github.nicolasdesnoust.marslander.genetic.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import io.github.nicolasdesnoust.marslander.core.GameState;
import io.github.nicolasdesnoust.marslander.genetic.IndividualFactory;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.logs.IndividualLogger;
import io.github.nicolasdesnoust.marslander.math.Point;
import io.github.nicolasdesnoust.marslander.solver.PathFinder;

public class PopulationEvolver {
	private static final double OLD_INDIVIDUALS_TO_KEEP_RATIO = 0.1;

	private final PopulationEvaluator evaluator;
	private final EvaluationNormalizer normalizer;
	private final PopulationSelector selector;
	private final PopulationCrosser crosser;
	private final PopulationMutator mutator;
	private final IndividualProcessor processor;
	private final IndividualFactory individualFactory;
	private final PathFinder pathFinder;
	private final IndividualLogger individualLogger;

	private List<Point> pathToFollow;

	public PopulationEvolver(
			PopulationCrosser crosser,
			PopulationMutator mutator,
			IndividualFactory individualFactory,
			PopulationEvaluator evaluator,
			EvaluationNormalizer normalizer,
			PopulationSelector selector,
			IndividualProcessor processor,
			PathFinder pathFinder, 
			IndividualLogger individualLogger) {
		this.crosser = crosser;
		this.mutator = mutator;
		this.individualFactory = individualFactory;
		this.evaluator = evaluator;
		this.normalizer = normalizer;
		this.selector = selector;
		this.processor = processor;
		this.pathFinder = pathFinder;
		this.individualLogger = individualLogger;
	}

	public void updateInitialGameState(GameState initialGameState) {
		this.pathToFollow = pathFinder.findPath(initialGameState.getCapsule(), initialGameState);
	}

	/**
	 * Représente l'évolution d'une population sur une génération.
	 */
	public List<Individual> evolve(
			List<Individual> population,
			int numberOfSelections,
			int currentGeneration,
			GameState initialGameState,
			boolean needEvaluation) {

		if (needEvaluation) {
			evaluator.evaluate(population, pathToFollow, initialGameState);
			individualLogger.addMissingEvaluations(population);
			population.sort(Comparator.comparingDouble(Individual::getEvaluation));
		}

		boolean rankBased = true;
		if (currentGeneration == 100) {
			rankBased = false;
		}

		List<Individual> newIndividuals = makeNewIndividuals(
				population,
				numberOfSelections,
				initialGameState,
				rankBased);
		evaluator.evaluate(newIndividuals, pathToFollow, initialGameState);
		individualLogger.addMissingEvaluations(newIndividuals);
		
		List<Individual> newPopulation = mergeIndividuals(population, newIndividuals);
		individualLogger.logGeneration(newPopulation, currentGeneration);
		return newPopulation;
	}

	private List<Individual> makeNewIndividuals(
			List<Individual> population,
			int numberOfSelections,
			GameState initialGameState,
			boolean rankBased) {

		normalizer.normalizeEvaluations(population, rankBased);

		List<Individual> newIndividuals = new ArrayList<>(numberOfSelections * 2);
		for (int j = 0; j < numberOfSelections; j++) {
			List<Individual> selectedIndividuals = selector.selectTwoIndividualsRandomly(population);
			newIndividuals.addAll(crosser.crossover(
					selectedIndividuals.get(0),
					selectedIndividuals.get(1)));
		}
		mutator.mutate(newIndividuals);
		processor.process(newIndividuals, initialGameState);
		return newIndividuals;
	}

	private List<Individual> mergeIndividuals(
			List<Individual> population,
			List<Individual> newIndividuals) {

		int populationSize = population.size();
		int keepCount = (int) Math.round(populationSize * OLD_INDIVIDUALS_TO_KEEP_RATIO);
		individualFactory.release(population.subList(0, populationSize - keepCount));
		List<Individual> oldIndividualsToKeep = population.subList(populationSize - keepCount, populationSize);

		TreeSet<Individual> tree = new TreeSet<>(Comparator.comparingDouble(Individual::getEvaluation));
		oldIndividualsToKeep.forEach(individual -> addOrRelease(tree, individual));
		newIndividuals.forEach(individual -> addOrRelease(tree, individual));

		List<Individual> sortedIndividuals = new ArrayList<>(tree);
		int sortedIndividualsSize = sortedIndividuals.size();
		if (sortedIndividualsSize <= populationSize) {
			return sortedIndividuals;
		} else {
			individualFactory.release(sortedIndividuals.subList(0, sortedIndividualsSize - populationSize));
			List<Individual> newPopulation = sortedIndividuals.subList(sortedIndividualsSize - populationSize,
					sortedIndividualsSize);
			return new ArrayList<>(newPopulation);
		}
	}

	private void addOrRelease(TreeSet<Individual> tree, Individual individual) {
		boolean added = tree.add(individual);
		if (!added) {
			individualFactory.release(individual);
		}
	}
}
