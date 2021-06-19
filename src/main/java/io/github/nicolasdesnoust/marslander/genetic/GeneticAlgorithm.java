package io.github.nicolasdesnoust.marslander.genetic;

import static net.logstash.logback.argument.StructuredArguments.kv;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nicolasdesnoust.marslander.config.SolverConfiguration;
import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.InitialGameState;
import io.github.nicolasdesnoust.marslander.core.LandingState;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.genetic.services.IndividualProcessor;
import io.github.nicolasdesnoust.marslander.genetic.services.PopulationEvolver;
import io.github.nicolasdesnoust.marslander.genetic.services.PopulationGenerator;
import io.github.nicolasdesnoust.marslander.logs.LoggableGene;
import io.github.nicolasdesnoust.marslander.math.Segment;
import io.github.nicolasdesnoust.marslander.solver.CapsuleService;
import io.github.nicolasdesnoust.marslander.solver.MarsService;

public class GeneticAlgorithm {
	private static final Logger log = LoggerFactory.getLogger(GeneticAlgorithm.class);
	public static int individualCount = 0;
	private final PopulationGenerator generator = new PopulationGenerator();
	private final PopulationEvolver evolver;
	private final IndividualProcessor processor = new IndividualProcessor();
	private final CapsuleService capsuleService = new CapsuleService();
	private final MarsService marsService = new MarsService();

	private final InitialGameState initialGameState;
	private final double heightWhereThereIsNoObstacleToLand;

	public GeneticAlgorithm(InitialGameState initialGameState) {
		this.initialGameState = initialGameState;
		evolver = new PopulationEvolver(initialGameState);
		this.heightWhereThereIsNoObstacleToLand = marsService.findHeightWhereThereIsNoObstacleToLand(
				initialGameState.getMars().getSurface(),
				initialGameState.getLandingArea());
	}

	public List<Individual> findBestPopulation(SolverConfiguration configuration) {
		individualCount = 0;
		List<Individual> population = generator.generateRandomPopulation(configuration, initialGameState);
		population.forEach(individual -> processor.process(individual, initialGameState));

		int currentGeneration = 0;
		logPopulation(population, currentGeneration);

		boolean solutionFound = false;
		while (!solutionFound && currentGeneration <= configuration.getGenerations()) {
			log.info("Current generation : {}", currentGeneration);
			population = evolver.evolve(population, configuration.getNumberOfSelections(), currentGeneration);
			log.info("Evolve done");

			if (hasASolution(population, initialGameState)) {
				System.err.println("Solution found !");
				solutionFound = true;
			}

			currentGeneration++;
			logPopulation(population, currentGeneration);
		}

		return population;
	}

	public void logPopulation(List<Individual> population, int generation) {
		if (generation % 10 != 0) {
			return;
		}
		for (Individual individual : population) {
			Capsule[] capsules = individual.getCapsules();

			List<LoggableGene> genes = new ArrayList<>(capsules.length);
			LoggableGene previousGene = null;
			for (int i = 0; i < capsules.length; i++) {
				Capsule capsule = capsules[i];
				LoggableGene gene = new LoggableGene();
				gene.setIndex(i);
				gene.setX((int) Math.round(capsule.getPosition().getX()));
				gene.setY((int) Math.round(capsule.getPosition().getY()));
				if (previousGene != null
						&& gene.getX() == previousGene.getX()
						&& gene.getY() == previousGene.getY()) {
					break;
				}
				genes.add(gene);
				previousGene = gene;
			}
			log.debug("Individual {}: {} {} {} {}",
					individual.getId(),
					kv("generation", generation),
					kv("type", "individual-" + individual.getId()),
					kv("evaluation", individual.getEvaluation()),
					kv("genes", genes));
		}
	}

	/**
	 * Vérifie si l'un des individus de la population forme une solution au
	 * problème. Un individu est une solution si la capsule survole la zone
	 * d'atterissage avec des vitesses horizontales et verticales comprises entre
	 * les bornes imposées.
	 * <p>
	 * NB: l'angle de rotation est négligé puisqu'il peut être corrigé manuellement
	 * par la suite.
	 */
	private boolean hasASolution(List<Individual> population, InitialGameState initialGameState) {
		Segment landingArea = initialGameState.getLandingArea();

		for (Individual individual : population) {
			if (isTheIndividualASolution(individual, landingArea)) {
				return true;
			}
		}

		return false;
	}

	private Capsule getCapsuleRightBeforeTerminalState(Individual individual) {
		Capsule[] capsules = individual.getCapsules();

		int currentIndex = capsules.length - 1;
		Capsule currentCapsule = capsules[currentIndex];
		while (currentIndex > 0 && currentCapsule.getLandingState().isTerminalState()) {
			currentIndex--;
			currentCapsule = capsules[currentIndex];
		}
		return currentCapsule;
	}

	public boolean isTheIndividualASolution(Individual individual, Segment landingArea) {
		for (Capsule capsule : individual.getCapsules()) {
			//List<Point> path = pathFinder.findPath(capsule, initialGameState);

			if (capsuleService.isCapsuleAboveLandingArea(capsule, landingArea)
					&& capsule.getPosition().getY() < heightWhereThereIsNoObstacleToLand
					&& capsuleService.couldCapsuleLand(capsule)) {
				return true;
			}
		}
		return false;
	}

	private Capsule getSecondToLastCapsule(Individual individual) {
		return individual.getCapsules()[individual.getCapsules().length - 1];
	}

	private LandingState getTerminalState(Individual individual) {
		return getSecondToLastCapsule(individual).getLandingState();
	}
}
