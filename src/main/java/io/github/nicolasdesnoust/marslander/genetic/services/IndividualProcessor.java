package io.github.nicolasdesnoust.marslander.genetic.services;

import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.InitialGameState;
import io.github.nicolasdesnoust.marslander.genetic.model.Gene;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.solver.CapsuleService;

public class IndividualProcessor {
    private final CapsuleService capsuleService = new CapsuleService();

    public void process(Individual individual, InitialGameState initialGameState) {
        Capsule capsuleCopy = new Capsule(initialGameState.getCapsule());

        for (int i = 0; i < individual.getGenes().length; i++) {
            Gene gene = individual.getGenes()[i];
            int requestedRotate = capsuleCopy.getRotate() + gene.getRotateIncrement();
            int requestedPower = capsuleCopy.getPower() + gene.getPowerIncrement();

            capsuleService.updateCapsuleState(capsuleCopy, requestedRotate, requestedPower, initialGameState);
            individual.getCapsules()[i] = capsuleCopy;
            capsuleCopy = new Capsule(capsuleCopy);
        }
    }
}
