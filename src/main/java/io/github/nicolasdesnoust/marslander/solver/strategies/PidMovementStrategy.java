package io.github.nicolasdesnoust.marslander.solver.strategies;

import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.GameState;
import io.github.nicolasdesnoust.marslander.solver.MovementInstructions;
import io.github.nicolasdesnoust.marslander.solver.PidService;

public class PidMovementStrategy implements MovementStrategy {
    public static final String STRATEGY_KEY = "PID";
    private PidService pidService = new PidService();

    public MovementInstructions findMovementInstructions(Capsule capsule, int turn, GameState initialGameState) {
        int nextPower = pidService.pidNextPower(capsule, initialGameState);
        int nextRotate = pidService.pidNextRotate(capsule, initialGameState);

        return new MovementInstructions(nextRotate, nextPower);
    }

    public boolean doesSolutionNeedAssistance() {
        return false;
    }

}