package io.github.nicolasdesnoust.marslander.solver.strategies;

import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.GameState;
import io.github.nicolasdesnoust.marslander.solver.MovementInstructions;

public interface MovementStrategy {
	MovementInstructions findMovementInstructions(Capsule capsule, int turn, GameState initialGameState);
	boolean doesSolutionNeedAssistance();
}
