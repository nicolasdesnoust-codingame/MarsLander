package io.github.nicolasdesnoust.marslander.logs;

import java.util.List;

import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;

public interface IndividualLogger {

	void addMissingEvaluations(List<Individual> individuals);

	void storeIndividual(Individual individual);
	
	void logGeneration(List<Individual> individuals, int generation);

	void storeCapsuleOf(Integer individualId, Capsule capsule);

}
