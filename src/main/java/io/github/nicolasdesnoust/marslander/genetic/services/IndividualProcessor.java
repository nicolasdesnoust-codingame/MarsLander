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

	public void processIndividual(
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

	/**
	 * Met à jour les gènes fournis de sorte à ce qu'ils mènent à un angle de
	 * rotation de 0.
	 */
//	private int patchLandingRotate(int landingRotate, Gene[] genes, int terminalStateIndex) {
//		int totalGain = 0;
//		int currentGeneIndex = terminalStateIndex;
//
//		while (landingRotate - totalGain != 0) {
//			Gene currentGene = genes[currentGeneIndex];
//
//			int oldRotateIncrement = currentGene.getRotateIncrement();
//			int newRotateIncrement = findBestRotateIncrement(landingRotate - totalGain);
//
//			int localGain = toGain(oldRotateIncrement, newRotateIncrement);
//			currentGene.setRotateIncrement(newRotateIncrement);
//
//			currentGeneIndex--;
//			totalGain += localGain;
//		}
//		
//		// retourner l'indice du gène jusqu'au quel le patching a du remonter dans le tableau.
//		// L'état de la capsule pourra etre mis à jour en conséquence.
//	}

	/**
	 * oldRotateIncrement = 10 newRotateIncrement = -15 lastRotate = 90
	 * 
	 * -15 - 10 = -25 -> abs(-25)
	 *
	 * oldRotateIncrement = -10 newRotateIncrement = 15 lastRotate = -90
	 * 
	 * 15 - -10 = 25 -> abs(25)
	 * 
	 */
//	private int toGain(int oldRotateIncrement, int newRotateIncrement) {
//		return Math.abs(newRotateIncrement - oldRotateIncrement);
//	}

	/**
	 * Cherche l'incrément compris dans [-15; 15] qui permet de rapprocher le plus
	 * possible de 0 l'angle de rotation donné.
	 */
//	private int findBestRotateIncrement(int rotate) {
//		if (rotate > 0) {
//			return Math.max(-15, -rotate);
//		} else if (rotate < 0) {
//			return Math.max(15, -rotate);
//		} else {
//			return 0;
//		}
//	}

}
