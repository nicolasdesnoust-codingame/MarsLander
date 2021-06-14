package io.github.nicolasdesnoust.marslander.genetic.services;

import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.random.RandomNumbersGenerator;

import java.util.List;

public class PopulationMutator {
    private static final double MUTATION_PROBABILITY = 0.1;
    private final PopulationGenerator populationGenerator = new PopulationGenerator();

    public void mutate(List<Individual> population) {
        population.forEach(this::mutate);
    }

    private void mutate(Individual individual) {
        for (int i = 0; i < individual.getGenes().length; i++) {
            double randomDouble = RandomNumbersGenerator.generateRandomDoubleInRange(0, 1);
            if (randomDouble <= MUTATION_PROBABILITY) {
                individual.getGenes()[i] = populationGenerator.generateRandomGene();
            }
        }
    }
}
