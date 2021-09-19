package io.github.nicolasdesnoust.marslander.genetic.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import io.github.nicolasdesnoust.marslander.SolverConfiguration;
import io.github.nicolasdesnoust.marslander.core.GameState;
import io.github.nicolasdesnoust.marslander.genetic.IndividualFactory;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
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
	private final PopulationGenerator generator;

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
			PopulationGenerator generator) {
		this.crosser = crosser;
		this.mutator = mutator;
		this.individualFactory = individualFactory;
		this.evaluator = evaluator;
		this.normalizer = normalizer;
		this.selector = selector;
		this.processor = processor;
		this.pathFinder = pathFinder;
		this.generator = generator;
	}

	public List<Point> updateInitialGameState(GameState initialGameState) {
		this.pathToFollow = pathFinder.findPath(initialGameState.getCapsule(), initialGameState);
		return pathToFollow;
	}

	/**
	 * Représente l'évolution d'une population sur une génération.
	 */
	public List<Individual> evolve(
			List<Individual> population,
			int currentGeneration,
			GameState initialGameState,
			SolverConfiguration configuration) {

		boolean rankBased = true;
		if (currentGeneration == 100) {
			rankBased = false;
		}

		List<Individual> newIndividuals = makeNewIndividuals(
				population,
				initialGameState,
				rankBased);
		evaluator.evaluate(newIndividuals, pathToFollow, initialGameState);

		return mergeIndividuals(population, newIndividuals, configuration, initialGameState);
	}

	private List<Individual> makeNewIndividuals(
			List<Individual> population,
			GameState initialGameState,
			boolean rankBased) {

		normalizer.normalizeEvaluations(population, rankBased);

		int selectionCount = population.size() / 2;
		List<Individual> newIndividuals = new ArrayList<>(selectionCount * 2);
		for (int j = 0; j < selectionCount; j++) {
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
			List<Individual> newIndividuals,
			SolverConfiguration configuration,
			GameState gameState) {

		int populationSize = population.size();
		int keepCount = (int) Math.round(populationSize * OLD_INDIVIDUALS_TO_KEEP_RATIO);
		individualFactory.release(population.subList(0, populationSize - keepCount));
		List<Individual> oldIndividualsToKeep = population.subList(populationSize - keepCount, populationSize);

		TreeSet<Individual> tree = new TreeSet<>(Comparator.comparingDouble(Individual::getEvaluation));
		oldIndividualsToKeep.forEach(individual -> addOrRelease(tree, individual));
		newIndividuals.forEach(individual -> addOrRelease(tree, individual));

		for (int i = tree.size(); i < configuration.getPopulationSize(); i++) {
			Individual randomIndividual = generator.generateRandomIndividual(
					gameState,
					configuration.getNumberOfGenesPerIndividual());
			processor.processIndividual(randomIndividual, gameState);
			evaluator.evaluateIndividual(randomIndividual, pathToFollow, gameState);
			addOrRelease(tree, randomIndividual);
		}

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
