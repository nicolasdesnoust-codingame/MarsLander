package io.github.nicolasdesnoust.marslander.genetic.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.random.RandomNumbersGenerator;

public class PopulationSelector {
	private static final int MAXIMUM_SELECTION_ATTEMPTS = 500;
	private final List<Individual> reusableList;

	public PopulationSelector() {
		reusableList = new ArrayList<>(2);
		reusableList.add(null);
		reusableList.add(null);
	}

	public List<Individual> selectTwoIndividualsRandomly(List<Individual> population) {
		int parent1Index = selectOneIndividualIndexRandomly(population);
		int parent2Index;

		if (population.size() == 1) {
			reusableList.set(0, population.get(0));
			reusableList.set(1, population.get(0));

			return reusableList;
		}

		int selectionAttempts = 0;
		do {
			parent2Index = selectOneIndividualIndexRandomly(population);
		} while (parent2Index == parent1Index && ++selectionAttempts <= MAXIMUM_SELECTION_ATTEMPTS);

		if (selectionAttempts > MAXIMUM_SELECTION_ATTEMPTS) {
			List<Double> normalizedEvaluations = population.stream()
					.map(Individual::getNormalizedEvaluation)
					.collect(Collectors.toList());

			throw new RuntimeException("Cannot select two distinct individuals in " + normalizedEvaluations.toString());
		}

		reusableList.set(0, population.get(parent1Index));
		reusableList.set(1, population.get(parent2Index));

		return reusableList;
	}

	private int selectOneIndividualIndexRandomly(List<Individual> population) {
		double randomEvaluation = RandomNumbersGenerator.generateRandomDoubleInRange(0, 1);
		return findGeneWithEvaluationGreaterThan(randomEvaluation, population);
	}

	private int findGeneWithEvaluationGreaterThan(double randomEvaluation, List<Individual> population) {
		Individual searchKey = new Individual();
		searchKey.setNormalizedEvaluation(randomEvaluation);

		int searchResult = Collections.binarySearch(
				population,
				searchKey,
				Comparator.comparingDouble(Individual::getNormalizedEvaluation));
		int geneIndex = (searchResult >= 0) ? searchResult : Math.abs(searchResult) - 1;

		if (geneIndex == population.size()) {
			geneIndex = population.size() - 1;
		}
		return geneIndex;
	}

}