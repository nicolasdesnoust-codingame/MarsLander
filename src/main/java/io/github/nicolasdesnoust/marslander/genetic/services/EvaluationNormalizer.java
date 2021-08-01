package io.github.nicolasdesnoust.marslander.genetic.services;

import java.util.List;

import io.github.nicolasdesnoust.marslander.genetic.model.Individual;

public class EvaluationNormalizer {

	public void normalizeEvaluations(List<Individual> population, boolean rankBased) {
		double[] temp = new double[population.size()];

		if (rankBased) {
			setupRankBasedNormalization(population, temp);
		} else {
			setupStandardNormalization(population, temp);
		}

		applyCumulativeSum(population, temp);
	}

	private void setupStandardNormalization(List<Individual> population, double[] temp) {
		double sum = population.stream()
				.map(Individual::getEvaluation)
				.reduce(0.0, Double::sum);

		for (int i = 0; i < population.size(); i++) {
			temp[i] = population.get(i).getEvaluation() / sum;
		}
	}

	private void setupRankBasedNormalization(List<Individual> population, double[] temp) {
		double sum = sumIntegersFromOneTo(population.size());
		for (int rank = 1; rank <= population.size(); rank++) {
			temp[rank - 1] = rank / sum;
		}
	}

	private int sumIntegersFromOneTo(int n) {
		return n * (n + 1) / 2;
	}

	private void applyCumulativeSum(List<Individual> population, double[] evaluations) {
		double sum = 0.0;
		for (int i = 0; i < population.size(); i++) {
			sum += evaluations[i];
			population.get(i).setNormalizedEvaluation(sum);
		}
	}

}
