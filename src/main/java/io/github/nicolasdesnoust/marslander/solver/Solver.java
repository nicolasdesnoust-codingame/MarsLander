package io.github.nicolasdesnoust.marslander.solver;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.GameState;
import io.github.nicolasdesnoust.marslander.core.GameStateParser;
import io.github.nicolasdesnoust.marslander.core.Timer;
import io.github.nicolasdesnoust.marslander.solver.strategies.GeneticMovementStrategy;
import io.github.nicolasdesnoust.marslander.solver.strategies.MovementStrategy;

public class Solver {
	private final CapsuleService capsuleService;
	private final GameStateParser gameStateParser;
	private final Map<String, MovementStrategy> movementStrategies;

	public Solver(
			CapsuleService capsuleService,
			GameStateParser gameStateParser,
			Map<String, MovementStrategy> movementStrategies) {
		this.capsuleService = capsuleService;
		this.gameStateParser = gameStateParser;
		this.movementStrategies = movementStrategies;
	}

	@SuppressWarnings("squid:S2189")
	public void solve(Scanner in, PrintWriter out) {
		GameState initialGameState = gameStateParser.parseInitialGameState(in);
		MovementStrategy movementStrategy = movementStrategies.get(GeneticMovementStrategy.STRATEGY_KEY);
		Capsule realCapsule = initialGameState.getCapsule();
		Capsule initialCapsule = new Capsule(initialGameState.getCapsule());
		List<MovementInstructions> instructions = new ArrayList<>();

		int currentTurn = 0;
		MovementInstructions movementInstructions = movementStrategy.findMovementInstructions(
				initialGameState.getCapsule(),
				currentTurn,
				initialGameState);
		instructions.add(movementInstructions);
		out.println(movementInstructions);

		capsuleService.updateCapsuleState(
				realCapsule, 
				movementInstructions.getRotate(),
				movementInstructions.getPower(), 
				initialGameState);

		while (true) {
			Timer.startNewTurnTimer();
			currentTurn++;

			// Inputs are ignored because real decimal values are rounded to integers
			gameStateParser.parseCurrentTurnState(in);

			movementInstructions = movementStrategy.findMovementInstructions(
					realCapsule, 
					currentTurn,
					initialGameState);

			capsuleService.updateCapsuleState(
					realCapsule, 
					movementInstructions.getRotate(),
					movementInstructions.getPower(), 
					initialGameState);

			instructions.add(movementInstructions);
			
			if(realCapsule.getLandingState().isTerminalState()) {
				System.err.println(realCapsule.getLandingState() + " " + realCapsule.getFuel());
				
				for (MovementInstructions instruction : instructions) {
					int power = Math.min(Math.max(instruction.getPower() - initialCapsule.getPower(), -1), 1);
					int rotate = Math.min(Math.max(instruction.getRotate() - initialCapsule.getRotate(), -15), 15);
					initialCapsule.setPower(initialCapsule.getPower() + power);
					initialCapsule.setRotate(initialCapsule.getRotate() + rotate);
					System.err.print("new Gene(" + rotate + ", " + power + "), ");
				}
				for(int i = instructions.size(); i < 90; i++) {
					System.err.print("new Gene(0, 0), ");
				}
			}
			
			out.println(movementInstructions);
		}
	}

}