package io.github.nicolasdesnoust.marslander.genetic.services;

import io.github.nicolasdesnoust.marslander.genetic.GeneticAlgorithm;
import io.github.nicolasdesnoust.marslander.genetic.model.Gene;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.random.RandomNumbersGenerator;

import java.util.Arrays;
import java.util.List;

public class PopulationCrosser {
    public List<Individual> crossover(Individual parent1, Individual parent2) {
        int numberOfGenes = parent1.getGenes().length;
        Gene[] child1Genes = new Gene[numberOfGenes];
        Gene[] child2Genes = new Gene[numberOfGenes];

        double randomCrossoverRatio = RandomNumbersGenerator.generateRandomDoubleInRange(0, 1);

        for (int i = 0; i < numberOfGenes; i++) {
            Gene child1Gene = crossoverGenes(parent1.getGenes()[i], parent2.getGenes()[i], randomCrossoverRatio);
            child1Genes[i] = child1Gene;
            Gene child2Gene = crossoverGenes(parent2.getGenes()[i], parent1.getGenes()[i], randomCrossoverRatio);
            child2Genes[i] = child2Gene;
        }

        return Arrays.asList(
                new Individual(GeneticAlgorithm.individualCount++, child1Genes),
                new Individual(GeneticAlgorithm.individualCount++, child2Genes)
        );
    }

    private Gene crossoverGenes(Gene parent1Gene, Gene parent2Gene, double crossoverRatio) {
        double parent1RotatePart = crossoverRatio * parent1Gene.getRotateIncrement();
        double parent2RotatePart = (1 - crossoverRatio) * parent2Gene.getRotateIncrement();
        int newRotate = (int) Math.round(parent1RotatePart + parent2RotatePart);

        double parent1PowerPart = crossoverRatio * parent1Gene.getPowerIncrement();
        double parent2PowerPart = (1 - crossoverRatio) * parent2Gene.getPowerIncrement();
        int newPower = (int) Math.round(parent1PowerPart + parent2PowerPart);

        return new Gene(newRotate, newPower);
    }
}
