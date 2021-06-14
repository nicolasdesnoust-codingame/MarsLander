package io.github.nicolasdesnoust.marslander.genetic.services;

import io.github.nicolasdesnoust.marslander.genetic.model.Gene;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.random.RandomNumbersGenerator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PopulationSelector {
    public int selectOneIndividualRandomly(List<Individual> population) {
        double randomEvaluation = RandomNumbersGenerator.generateRandomDoubleInRange(0, 1);
        return findGeneWithEvaluationGreaterThan(randomEvaluation, population);
    }

    private int findGeneWithEvaluationGreaterThan(double randomEvaluation, List<Individual> population) {
        Individual searchKey = new Individual(-1, new Gene[0]);
        searchKey.setEvaluation(randomEvaluation);
        int searchResult = Collections.binarySearch(population, searchKey, Comparator.comparingDouble(Individual::getEvaluation));
        int result = (searchResult >= 0) ? searchResult : Math.abs(searchResult) - 1;

        if (result == population.size()) {
            result = population.size() - 1;
        }
        return result;
    }
}
