package io.github.nicolasdesnoust.marslander.genetic.services;

import io.github.nicolasdesnoust.marslander.config.SolverConfiguration;
import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.InitialGameState;
import io.github.nicolasdesnoust.marslander.genetic.GeneticAlgorithm;
import io.github.nicolasdesnoust.marslander.genetic.model.Gene;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.random.RandomNumbersGenerator;

import java.util.ArrayList;
import java.util.List;

public class PopulationGenerator {
    public List<Individual> generateRandomPopulation(SolverConfiguration configuration, InitialGameState initialGameState) {
        int size = configuration.getPopulationSize();
        int numberOfGenesPerIndividual = configuration.getNumberOfGenesPerIndividual();

        List<Individual> randomPopulation = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Individual individual = generateRandomIndividual(initialGameState, numberOfGenesPerIndividual);
            randomPopulation.add(individual);
        }

        return randomPopulation;
    }

    public Individual generateRandomIndividual(InitialGameState initialGameState, int numberOfGenesPerIndividual) {
        Gene[] randomGenes = new Gene[numberOfGenesPerIndividual];

        int currentRotate = initialGameState.getCapsule().getRotate();
        int currentPower = initialGameState.getCapsule().getPower();
        for (int j = 0; j < numberOfGenesPerIndividual; j++) {
            randomGenes[j] = generateRandomGene(currentRotate, currentPower);
            currentRotate += randomGenes[j].getRotateIncrement();
            currentPower += randomGenes[j].getPowerIncrement();
        }

        return new Individual(GeneticAlgorithm.individualCount++, randomGenes);
    }

    public Gene generateRandomGene() {
        return generateRandomGene(0, 0);
    }

    public Gene generateRandomGene(int precedingRotate, int precedingPower) {
        int rotateMin = precedingRotate == -90 ? 0 : -1;
        int rotateMax = precedingRotate == 90 ? 0 : 1;
        int rotateIncrement = RandomNumbersGenerator.generateRandomIntInRange(rotateMin, rotateMax) * 15;
        //int newRotate = precedingRotate + rotateIncrement;
        //newRotate = Math.min(Math.max(newRotate, -90), 90);

        int powerMin = precedingPower == 0 ? 0 : -1;
        int powerMax = precedingPower == 4 ? 0 : 1;
        int powerIncrement = RandomNumbersGenerator.generateRandomIntInRange(powerMin, powerMax);
        //int newPower = precedingPower + powerIncrement;

        return new Gene(rotateIncrement, powerIncrement);
    }
}
