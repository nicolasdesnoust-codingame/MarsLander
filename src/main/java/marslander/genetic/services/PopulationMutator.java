package marslander.genetic.services;

import marslander.genetic.model.Individual;
import marslander.util.RandomNumbersGenerator;

import java.util.List;

public class PopulationMutator {
    private static final double MUTATION_PROBABILITY = 0.01;
    private final PopulationGenerator populationGenerator = new PopulationGenerator();

    public void mutate(List<Individual> population) {
        population.forEach(individual -> {
            double randomDouble = RandomNumbersGenerator.generateRandomDoubleInRange(0, 1);
            if(randomDouble <= MUTATION_PROBABILITY) {
                mutate(individual);
            }
        });
    }

    private void mutate(Individual individual) {
        int numberOfGenes = individual.getGenes().length;
        int geneToMutateIndex = RandomNumbersGenerator.generateRandomIntInRange(0, numberOfGenes - 1);
        individual.getGenes()[geneToMutateIndex] = populationGenerator.generateRandomGene();
    }
}
