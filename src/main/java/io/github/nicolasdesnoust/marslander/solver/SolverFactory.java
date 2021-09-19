package io.github.nicolasdesnoust.marslander.solver;

import java.util.HashMap;
import java.util.Map;

import io.github.nicolasdesnoust.marslander.SolverConfiguration;
import io.github.nicolasdesnoust.marslander.core.GameStateParser;
import io.github.nicolasdesnoust.marslander.genetic.GeneticAlgorithm;
import io.github.nicolasdesnoust.marslander.genetic.IndividualFactory;
import io.github.nicolasdesnoust.marslander.genetic.services.EvaluationNormalizer;
import io.github.nicolasdesnoust.marslander.genetic.services.IndividualProcessor;
import io.github.nicolasdesnoust.marslander.genetic.services.PopulationCrosser;
import io.github.nicolasdesnoust.marslander.genetic.services.PopulationEvaluator;
import io.github.nicolasdesnoust.marslander.genetic.services.PopulationEvolver;
import io.github.nicolasdesnoust.marslander.genetic.services.PopulationGenerator;
import io.github.nicolasdesnoust.marslander.genetic.services.PopulationMutator;
import io.github.nicolasdesnoust.marslander.genetic.services.PopulationSelector;
import io.github.nicolasdesnoust.marslander.logs.CodingameIndividualLogger;
import io.github.nicolasdesnoust.marslander.logs.IndividualLogger;
import io.github.nicolasdesnoust.marslander.logs.LocalIndividualLogger;
import io.github.nicolasdesnoust.marslander.solver.strategies.GeneticMovementStrategy;
import io.github.nicolasdesnoust.marslander.solver.strategies.MovementStrategy;

public class SolverFactory {

	public Solver createSolver(SolverConfiguration configuration) {
		SegmentChunks segmentChunks = new SegmentChunks();
		MarsService marsService = new MarsService();
		CapsuleService capsuleService = new CapsuleService(segmentChunks, marsService);
		PathFinder pathFinder = new PathFinder();

		GameStateParser gameStateParser = new GameStateParser(marsService);

		Map<String, MovementStrategy> movementStrategies = new HashMap<>();

		IndividualLogger individualLogger = new LocalIndividualLogger();

		IndividualFactory individualFactory = new IndividualFactory(
				configuration.getPopulationSize() * 20,
				configuration.getNumberOfGenesPerIndividual());
		PopulationGenerator generator = new PopulationGenerator(individualFactory);
		PopulationCrosser crosser = new PopulationCrosser(individualFactory);
		PopulationMutator mutator = new PopulationMutator(generator);
		PopulationSelector selector = new PopulationSelector();
		IndividualProcessor processor = new IndividualProcessor(capsuleService, individualLogger);
		PopulationEvaluator evaluator = new PopulationEvaluator(capsuleService, pathFinder);
		EvaluationNormalizer evaluationNormalizer = new EvaluationNormalizer();
		PopulationEvolver evolver = new PopulationEvolver(
				crosser,
				mutator,
				individualFactory,
				evaluator,
				evaluationNormalizer,
				selector,
				processor,
				pathFinder, 
				generator);
		GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(
				generator, 
				evolver, 
				processor, 
				individualLogger, 
				evaluator,
				mutator);
		
		GeneticMovementStrategy geneticMovementStrategy = new GeneticMovementStrategy(configuration, geneticAlgorithm,
				capsuleService, individualFactory);
		movementStrategies.put(GeneticMovementStrategy.STRATEGY_KEY, geneticMovementStrategy);
		return new Solver(
				capsuleService,
				gameStateParser,
				movementStrategies);
	}

}
