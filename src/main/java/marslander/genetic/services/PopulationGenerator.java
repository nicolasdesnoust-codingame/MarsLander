package marslander.genetic.services;

import marslander.genetic.model.Gene;
import marslander.genetic.model.Individual;
import marslander.util.RandomNumbersGenerator;

import java.util.ArrayList;
import java.util.List;

public class PopulationGenerator {
    private static final int[] rotateChanges = { -15, 0, 15 };

    public List<Individual> generateRandomPopulation(int size, int numberOfGenesPerIndividual) {
        List<Individual> randomPopulation = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            Gene[] individualGenes = new Gene[numberOfGenesPerIndividual];
            for(int j = 0; j < numberOfGenesPerIndividual; j++) {
                individualGenes[j] = generateRandomGene();
            }
            randomPopulation.add(new Individual(individualGenes));
        }

        return randomPopulation;
    }

    public Gene generateRandomGene() {
        int randomRotate = RandomNumbersGenerator.generateRandomIntInRange(-15, 15);
        int randomPower = RandomNumbersGenerator.generateRandomIntInRange(-1, 1);
        return new Gene(randomRotate, randomPower);
    }
}
