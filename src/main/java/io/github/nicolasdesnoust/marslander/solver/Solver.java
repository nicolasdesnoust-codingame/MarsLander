package io.github.nicolasdesnoust.marslander.solver;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import io.github.nicolasdesnoust.marslander.SolverConfiguration;
import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.GameState;
import io.github.nicolasdesnoust.marslander.core.GameStateParser;
import io.github.nicolasdesnoust.marslander.core.LandingState;
import io.github.nicolasdesnoust.marslander.core.Timer;
import io.github.nicolasdesnoust.marslander.math.Segment;
import io.github.nicolasdesnoust.marslander.solver.strategies.GeneticMovementStrategy;
import io.github.nicolasdesnoust.marslander.solver.strategies.MovementStrategy;
import io.github.nicolasdesnoust.marslander.solver.strategies.PidMovementStrategy;

public class Solver {
	private final CapsuleService capsuleService;
	private final GameStateParser gameStateParser;
	private final Map<String, MovementStrategy> movementStrategies;
	private final SolverConfiguration configuration;
	private boolean done = false;
	private Capsule realCapsule;

	public Solver(
			CapsuleService capsuleService,
			GameStateParser gameStateParser,
			Map<String, MovementStrategy> movementStrategies,
			SolverConfiguration configuration) {
		this.capsuleService = capsuleService;
		this.gameStateParser = gameStateParser;
		this.movementStrategies = movementStrategies;
		this.configuration = configuration;
	}

	@SuppressWarnings("squid:S2189")
	public void solve(Scanner in, PrintWriter out) {
		GameState initialGameState = gameStateParser.parseInitialGameState(in);
		MovementStrategy movementStrategy = movementStrategies.get(GeneticMovementStrategy.STRATEGY_KEY);
		realCapsule = initialGameState.getCapsule();

		int currentTurn = 0;
		MovementInstructions movementInstructions = movementStrategy.findMovementInstructions(
				initialGameState.getCapsule(), currentTurn, initialGameState);
		out.println(movementInstructions);
		capsuleService.updateCapsuleState(realCapsule, movementInstructions.getRotate(),
				movementInstructions.getPower(), initialGameState);

		while (true) {
			Timer.startNewTurnTimer();
			currentTurn++;

			Capsule capsule = gameStateParser.parseCurrentTurnState(in);
			Segment landingArea = initialGameState.getLandingArea();
			if (mustPidCapsuleMovement(movementStrategy,
					capsule, landingArea, initialGameState.getMars().getSurface(),
					currentTurn, configuration.getNumberOfGenesPerIndividual())) {
				movementStrategy = movementStrategies.get(PidMovementStrategy.STRATEGY_KEY);
			}

			movementInstructions = movementStrategy.findMovementInstructions(realCapsule, currentTurn,
					initialGameState);

			capsuleService.updateCapsuleState(
					capsule,
					movementInstructions.getRotate(),
					movementInstructions.getPower(),
					initialGameState);
			capsuleService.updateCapsuleState(realCapsule, movementInstructions.getRotate(),
					movementInstructions.getPower(), initialGameState);
			if (capsule.getLandingState() != LandingState.STILL_FLYING
					&& capsule.getLandingState() != LandingState.LANDED) {
				movementInstructions = new MovementInstructions(0, movementInstructions.getPower());
			}

			out.println(movementInstructions);
		}
	}

	private boolean mustPidCapsuleMovement(
			MovementStrategy movementStrategy,
			Capsule capsule,
			Segment landingArea,
			List<Segment> marsSurface,
			int turnCount,
			int numberOfGenesPerIndividual) {

		boolean response = (capsuleService.isCapsuleRightAboveLandingArea(capsule, marsSurface, landingArea)
				&& movementStrategy.doesSolutionNeedAssistance()
				&& (capsuleService.doesCapsuleRespectSpeedLimitations(capsule)
						|| capsule.getPosition().getY() - landingArea.getP1().getY() <= 200));
		if (response || !done) {
			done = true;
		}
		return response;
	}

}