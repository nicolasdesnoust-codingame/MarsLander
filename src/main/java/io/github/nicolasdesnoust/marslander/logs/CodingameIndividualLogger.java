package io.github.nicolasdesnoust.marslander.logs;

import java.util.List;

import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;

public class CodingameIndividualLogger implements IndividualLogger {

	@Override
	public void addMissingEvaluations(List<Individual> individuals) {
		// Vide pour ne pas impacter les performances de l'application sur Codingame
	}

	@Override
	public void storeIndividual(Individual individual) {
		// Vide pour ne pas impacter les performances de l'application sur Codingame
	}

	@Override
	public void logGeneration(List<Individual> individuals, int generation) {
		// Vide pour ne pas impacter les performances de l'application sur Codingame
	}

	@Override
	public void storeCapsuleOf(Integer individualId, Capsule capsule) {
		// Vide pour ne pas impacter les performances de l'application sur Codingame		
	}
	
}
