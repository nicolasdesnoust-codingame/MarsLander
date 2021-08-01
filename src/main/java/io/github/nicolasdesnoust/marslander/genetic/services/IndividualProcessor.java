package io.github.nicolasdesnoust.marslander.genetic.services;

import java.util.List;

import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.GameState;
import io.github.nicolasdesnoust.marslander.genetic.model.Gene;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.logs.IndividualLogger;
import io.github.nicolasdesnoust.marslander.solver.CapsuleService;

public class IndividualProcessor {
	private final CapsuleService capsuleService;
	private final IndividualLogger individualLogger;

	public IndividualProcessor(
			CapsuleService capsuleService, 
			IndividualLogger individualLogger) {
		this.capsuleService = capsuleService;
		this.individualLogger = individualLogger;
	}

	public void process(List<Individual> population, GameState initialGameState) {
		population.forEach(individual -> processIndividual(individual, initialGameState));
	}

	private void processIndividual(
			Individual individual,
			GameState initialGameState) {

		Capsule capsuleCopy = new Capsule(initialGameState.getCapsule());
		
		individualLogger.storeIndividual(individual);

		Gene[] genes = individual.getGenes();
		for (int i = 0; i < genes.length; i++) {
			Gene gene = genes[i];
			int requestedRotate = capsuleCopy.getRotate() + gene.getRotateIncrement();
			int requestedPower = capsuleCopy.getPower() + gene.getPowerIncrement();

			capsuleService.updateCapsuleState(capsuleCopy, requestedRotate, requestedPower, initialGameState);

			individualLogger.storeCapsuleOf(individual.getId(), capsuleCopy);
			
			if (capsuleCopy.getLandingState().isTerminalState()) {
				break;
			}
		}
		individual.setCapsule(capsuleCopy);
	}

}
