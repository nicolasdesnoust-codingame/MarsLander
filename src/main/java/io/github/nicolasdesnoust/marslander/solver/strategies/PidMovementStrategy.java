package io.github.nicolasdesnoust.marslander.solver.strategies;

import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.InitialGameState;
import io.github.nicolasdesnoust.marslander.solver.MovementInstructions;
import io.github.nicolasdesnoust.marslander.solver.PidService;

public class PidMovementStrategy implements MovementStrategy {

	private InitialGameState initialGameState;
	private PidService pidService = new PidService();
	
	public PidMovementStrategy(InitialGameState initialGameState) {
		this.initialGameState = initialGameState;
	}

	@Override
	public MovementInstructions findMovementInstructions(Capsule capsule, int turn) {
		int nextPower = pidService.pidNextPower(capsule, initialGameState);
		int nextRotate = pidService.pidNextRotate(capsule, initialGameState);

		return new MovementInstructions(nextRotate, nextPower);
	}

	@Override
	public boolean doesSolutionNeedAssistance() {
		return false;
	}

}
