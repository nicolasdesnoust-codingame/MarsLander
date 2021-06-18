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
		int index = 0;
		for(Point point : this.pathToFollow) {
			log.info("Path point: {} {} {} {}",
				kv("type", "path"),
				kv("index", index++),
				kv("x", Math.round(point.getX())),
				kv("y", Math.round(point.getY())));
		}
	}

	/**
	 * Représente l'évolution d'une population sur une génération.
	 */
	public List<Individual> evolve(List<Individual> population, int numberOfSelections, int currentGeneration) {
		if (currentGeneration == 0) {
			evaluator.evaluate(population, pathToFollow, initialGameState, currentGeneration);
			log.info("Evaluate done");
		}

		population.sort(Comparator.comparingDouble(Individual::getEvaluation));
		List<Individual> newIndividuals = makeNewIndividuals(population, numberOfSelections);
		log.info("Make new individuals done");

		evaluator.evaluate(newIndividuals, pathToFollow, initialGameState, currentGeneration);
		log.info("Evaluate done (2)");

		return mergeIndividuals(population, newIndividuals);
	}

	private List<Individual> makeNewIndividuals(List<Individual> population, int numberOfSelections) {
		log.info("in evolver");
		List<Individual> newIndividuals = new ArrayList<>(population.size());
		List<Double> evaluations = population.stream()
				.map(Individual::getEvaluation)
				.collect(Collectors.toList());
		log.info("{}", evaluations);

		for (int j = 0; j < numberOfSelections; j++) {
			int parent1Index = selector.selectOneIndividualRandomly(population);
			int parent2Index;
			do {
				parent2Index = selector.selectOneIndividualRandomly(population);
				// log.info("selection: parent1Index:{} parent2Index:{}", parent1Index,
				// parent2Index);
			} while (parent2Index == parent1Index);

			newIndividuals.addAll(crosser.crossover(
					population.get(parent1Index),
					population.get(parent2Index)));
		}
		log.info("mutating");

		mutator.mutate(newIndividuals);
		log.info("processing");

		newIndividuals.forEach(newIndividual -> processor.process(newIndividual, initialGameState));
		return newIndividuals;
	}

	private List<Individual> mergeIndividuals(List<Individual> population, List<Individual> newIndividuals) {
		int newPopulationSize = population.size();

		Stream<Individual> oldPopulationToKeep = population.stream()
				.sorted(Comparator.comparingDouble(Individual::getEvaluation).reversed())
				.limit(Math.round(population.size() * OLD_INDIVIDUALS_TO_KEEP_RATIO));

		return Stream.concat(oldPopulationToKeep, newIndividuals.stream())
				.sorted(Comparator.comparingDouble(Individual::getEvaluation).reversed())
				.limit(newPopulationSize)
				.collect(Collectors.toList());
	}
}
