package io.github.nicolasdesnoust.marslander.genetic.services;

import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.InitialGameState;
import io.github.nicolasdesnoust.marslander.genetic.model.Gene;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.math.Segment;
import io.github.nicolasdesnoust.marslander.solver.CapsuleService;
import io.github.nicolasdesnoust.marslander.solver.MarsService;

public class IndividualProcessor {
    private final CapsuleService capsuleService = new CapsuleService();
    private final MarsService marsService = new MarsService();
    private double heightWhereThereIsNoObstacleToLand;

    public void process(Individual individual, InitialGameState initialGameState) {
        if (heightWhereThereIsNoObstacleToLand == 0.0) {
            this.heightWhereThereIsNoObstacleToLand = marsService.findHeightWhereThereIsNoObstacleToLand(
                    initialGameState.getMars().getSurface(),
                    initialGameState.getLandingArea());
        }

        Capsule capsuleCopy = new Capsule(initialGameState.getCapsule());

        for (int i = 0; i < individual.getGenes().length; i++) {
            Gene gene = individual.getGenes()[i];
            int requestedRotate = capsuleCopy.getRotate() + gene.getRotateIncrement();
            int requestedPower = capsuleCopy.getPower() + gene.getPowerIncrement();

            capsuleService.updateCapsuleState(capsuleCopy, requestedRotate, requestedPower, initialGameState);
            individual.setSolution(isCapsuleASolution(capsuleCopy, initialGameState.getLandingArea()));
            individual.getCapsules()[i] = capsuleCopy;
        }
    }

    public boolean isCapsuleASolution(Capsule capsule, Segment landingArea) {
        return capsuleService.isCapsuleAboveLandingArea(capsule, landingArea)
                && capsule.getPosition().getY() < heightWhereThereIsNoObstacleToLand
                && capsuleService.couldCapsuleLand(capsule);
    }
}
