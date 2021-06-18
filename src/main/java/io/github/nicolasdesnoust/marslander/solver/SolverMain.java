package io.github.nicolasdesnoust.marslander.solver;

import static net.logstash.logback.argument.StructuredArguments.kv;

import java.io.PrintWriter;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nicolasdesnoust.marslander.config.SolverConfiguration;
import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.GameStateParser;
import io.github.nicolasdesnoust.marslander.core.InitialGameState;
import io.github.nicolasdesnoust.marslander.math.Point;
import io.github.nicolasdesnoust.marslander.math.Segment;
import io.github.nicolasdesnoust.marslander.simulator.SimulatorService;
import io.github.nicolasdesnoust.marslander.solver.strategies.GeneticMovementStrategy;
import io.github.nicolasdesnoust.marslander.solver.strategies.MovementStrategy;
import io.github.nicolasdesnoust.marslander.solver.strategies.PidMovementStrategy;

public class SolverMain {
	private static final Logger log = LoggerFactory.getLogger(SolverMain.class);

	private static final CapsuleService capsuleService = new CapsuleService();
	private static final GameStateParser gameStateParser = new GameStateParser();

	private SolverMain() {
	}

	@SuppressWarnings("squid:S2189")
	public static void solve(Scanner in, PrintWriter out, SolverConfiguration configuration) {
		InitialGameState initialGameState = gameStateParser.parseInitialGameState(in);
		for (Map.Entry<Integer, Point> pathPoint : initialGameState.getPathPoints().entrySet()) {
			log.info("Path point: {} {} {} {}",
				kv("type", "point"),
				kv("index", pathPoint.getKey()),
				kv("x", Math.round(pathPoint.getValue().getX())),
				kv("y", Math.round(pathPoint.getValue().getY())));
		}
		MovementStrategy movementStrategy = new GeneticMovementStrategy(initialGameState, configuration);

		int turnCount = 0;
		MovementInstructions movementInstructions = movementStrategy.findMovementInstructions(
				initialGameState.getCapsule(), turnCount);
		out.println(movementInstructions);

		// game loop
		while (true) {
			turnCount++;
			org.slf4j.MDC.put("turn", String.valueOf(SimulatorService.currentTurn));
			Capsule capsule = gameStateParser.parseCurrentTurnState(in);

			Segment landingArea = initialGameState.getLandingArea();
			if (mustPidCapsuleMovement(movementStrategy, capsule, landingArea)) {
				log.info("Switching to Pid Strategy");
				movementStrategy = new PidMovementStrategy(initialGameState);
			}

			movementInstructions = movementStrategy.findMovementInstructions(capsule, turnCount);
			capsuleService.updateCapsuleState(capsule, movementInstructions.getRotate(),
					movementInstructions.getPower(), initialGameState);
			if (doesCapsuleHitLandingArea(capsule, landingArea)) {
				movementInstructions = new MovementInstructions(0, movementInstructions.getPower());
			}

			out.println(movementInstructions);
		}
	}

	private static boolean doesCapsuleHitLandingArea(Capsule capsule, Segment landingArea) {
		return capsule.getPosition().getY() <= landingArea.getP1().getY();
	}

	private static boolean mustPidCapsuleMovement(MovementStrategy movementStrategy, Capsule capsule,
			Segment landingArea) {
		return capsuleService.isCapsuleAboveLandingArea(capsule, landingArea)
				&& movementStrategy.doesSolutionNeedAssistance()
				&& (capsuleService.couldCapsuleLand(capsule)
						|| capsule.getPosition().getY() - landingArea.getP1().getY() >= 200);
	}
}
