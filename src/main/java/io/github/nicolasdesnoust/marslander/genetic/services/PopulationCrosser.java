package io.github.nicolasdesnoust.marslander.genetic.services;

import java.util.Arrays;
import java.util.List;

import io.github.nicolasdesnoust.marslander.genetic.GeneticAlgorithm;
import io.github.nicolasdesnoust.marslander.genetic.IndividualFactory;
import io.github.nicolasdesnoust.marslander.genetic.model.Gene;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.random.RandomNumbersGenerator;

public class PopulationCrosser {
    private final IndividualFactory individualFactory;

    public PopulationCrosser(IndividualFactory individualFactory) {
        this.individualFactory = individualFactory;
    }

    public List<Individual> crossover(Individual parent1, Individual parent2) {
        Individual child1 = individualFactory.acquire();
        Individual child2 = individualFactory.acquire();
        child1.setId(GeneticAlgorithm.individualCount++);
        child2.setId(GeneticAlgorithm.individualCount++);

        Gene[] parent1genes = parent1.getGenes();
        Gene[] parent2genes = parent2.getGenes();
        Gene[] child1genes = child1.getGenes();
        Gene[] child2genes = child2.getGenes();

        int numberOfGenes = parent1genes.length;
        double randomCrossoverRatio = RandomNumbersGenerator.generateRandomDoubleInRange(0, 1);

        for (int i = 0; i < numberOfGenes; i++) {
            crossoverGenes(
                    parent1genes[i], parent2genes[i],
                    child1genes[i], randomCrossoverRatio);
            crossoverGenes(
                    parent2genes[i], parent1genes[i],
                    child2genes[i], randomCrossoverRatio);
        }

        return Arrays.asList(child1, child2);
    }

    private void crossoverGenes(
            Gene parent1Gene, Gene parent2Gene,
            Gene childGene,
            double crossoverRatio) {

        double parent1RotatePart = crossoverRatio * parent1Gene.getRotateIncrement();
        double parent2RotatePart = (1 - crossoverRatio) * parent2Gene.getRotateIncrement();
        int newRotate = (int) Math.round(parent1RotatePart + parent2RotatePart);

        double parent1PowerPart = crossoverRatio * parent1Gene.getPowerIncrement();
        double parent2PowerPart = (1 - crossoverRatio) * parent2Gene.getPowerIncrement();
        int newPower = (int) Math.round(parent1PowerPart + parent2PowerPart);

        childGene.setPowerIncrement(newPower);
        childGene.setRotateIncrement(newRotate);
    }

}