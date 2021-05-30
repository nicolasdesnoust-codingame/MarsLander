package marslander.genetic.services;

import marslander.genetic.model.Gene;
import marslander.genetic.model.Individual;
import marslander.util.RandomNumbersGenerator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PopulationSelector {
    public int selectOneIndividualRandomly(List<Individual> population) {
        double randomEvaluation = RandomNumbersGenerator.generateRandomDoubleInRange(0, 1);
        return findGeneWithEvaluationGreaterThan(randomEvaluation, population);
    }

    private int findGeneWithEvaluationGreaterThan(double randomEvaluation, List<Individual> population) {
        Individual searchKey = new Individual(new Gene[0]);
        searchKey.setEvaluation(randomEvaluation);
        int searchResult = Collections.binarySearch(population, searchKey, Comparator.comparingDouble(Individual::getEvaluation));
        int result;
        if (searchResult >= 0) {
            result = searchResult;
        } else {
            int insertionPoint = searchResult * -1 - 1;

            if (insertionPoint == population.size()) {
                result = population.size() - 1;
            } else {
                result = insertionPoint + 1;
            }
        }

        return result;
    }
}
