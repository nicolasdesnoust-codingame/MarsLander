package io.github.nicolasdesnoust.marslander.genetic.services;

import java.util.List;

import io.github.nicolasdesnoust.marslander.genetic.model.Gene;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.random.RandomNumbersGenerator;

public class PopulationMutator {
    private static final double MUTATION_PROBABILITY = 0.01;
    private final PopulationGenerator populationGenerator;

    public PopulationMutator(PopulationGenerator populationGenerator) {
        this.populationGenerator = populationGenerator;
    }

    public void mutate(List<Individual> population) {
        population.forEach(this::mutate);
    }

    private void mutate(Individual individual) {
        Gene[] genes = individual.getGenes();
        for (int i = 0; i < genes.length; i++) {
            double randomDouble = RandomNumbersGenerator.generateRandomDoubleInRange(0, 1);
            if (randomDouble <= MUTATION_PROBABILITY) {
//                populationGenerator.updateGeneRandomly(genes[i]);
            	int randomPowerIncrement = RandomNumbersGenerator.generateRandomIntInRange(-1, 1);
            	int randomRotateIncrement = RandomNumbersGenerator.generateRandomIntInRange(-5, 5);

            	genes[i].setPowerIncrement(randomPowerIncrement);
            	genes[i].setRotateIncrement(
            			Math.min(Math.max(genes[i].getRotateIncrement() + randomRotateIncrement, -15), 15));
            }
        }
    }
}
