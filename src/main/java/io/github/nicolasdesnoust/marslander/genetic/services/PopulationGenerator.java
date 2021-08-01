package io.github.nicolasdesnoust.marslander.genetic.services;

import java.util.ArrayList;
import java.util.List;

import io.github.nicolasdesnoust.marslander.SolverConfiguration;
import io.github.nicolasdesnoust.marslander.core.GameState;
import io.github.nicolasdesnoust.marslander.genetic.GeneticAlgorithm;
import io.github.nicolasdesnoust.marslander.genetic.IndividualFactory;
import io.github.nicolasdesnoust.marslander.genetic.model.Gene;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.random.RandomNumbersGenerator;

public class PopulationGenerator {
	private static final int FIXED_ROTATE_COUNT = 40;
	private final IndividualFactory individualFactory;

	public PopulationGenerator(IndividualFactory individualFactory) {
		this.individualFactory = individualFactory;
	}

	public List<Individual> generateRandomPopulation(
			SolverConfiguration configuration,
			GameState initialGameState) {

		int populationSize = configuration.getPopulationSize();
		int numberOfGenesPerIndividual = configuration.getNumberOfGenesPerIndividual();

		List<Individual> randomPopulation = new ArrayList<>(populationSize);
		for (int i = 0; i < populationSize - (FIXED_ROTATE_COUNT * 2); i++) {
			Individual individual = generateRandomIndividual(
					initialGameState,
					numberOfGenesPerIndividual);
			randomPopulation.add(individual);
		}
		for (int i = 0; i < FIXED_ROTATE_COUNT; i++) {
			Individual individual = generateRandomIndividualWithFixedRotate(
					initialGameState,
					numberOfGenesPerIndividual,
					-15);
			randomPopulation.add(individual);
			individual = generateRandomIndividualWithFixedRotate(
					initialGameState,
					numberOfGenesPerIndividual,
					15);
			randomPopulation.add(individual);
		}

		return randomPopulation;
	}

	public Individual generateRandomIndividual(GameState initialGameState, int numberOfGenesPerIndividual) {

		Individual randomIndividual = individualFactory.acquire();
		randomIndividual.setId(GeneticAlgorithm.individualCount++);
		Gene[] genes = randomIndividual.getGenes();

		int currentRotate = initialGameState.getCapsule().getRotate();
		int currentPower = initialGameState.getCapsule().getPower();
		for (int j = 0; j < numberOfGenesPerIndividual; j++) {
			updateGeneRandomly(genes[j], currentRotate, currentPower);
			currentRotate += genes[j].getRotateIncrement();
			currentPower += genes[j].getPowerIncrement();
		}

		return randomIndividual;
	}

	public Individual generateRandomIndividualWithFixedRotate(
			GameState initialGameState,
			int numberOfGenesPerIndividual,
			int fixedRotate) {

		Individual randomIndividual = individualFactory.acquire();
		randomIndividual.setId(GeneticAlgorithm.individualCount++);
		Gene[] genes = randomIndividual.getGenes();

		int currentPower = initialGameState.getCapsule().getPower();
		for (int j = 0; j < numberOfGenesPerIndividual; j++) {
			updateGeneRandomlyWithFixedRotate(genes[j], fixedRotate, currentPower);
			currentPower += genes[j].getPowerIncrement();
		}

		return randomIndividual;
	}

	public void updateGeneRandomly(Gene gene) {
		int rotateIncrement = RandomNumbersGenerator.generateRandomIntInRange(-1, 1) * 15;
		int powerIncrement = RandomNumbersGenerator.generateRandomIntInRange(-1, 1);

		gene.setRotateIncrement(rotateIncrement);
		gene.setPowerIncrement(powerIncrement);
	}

	private void updateGeneRandomly(Gene gene, int precedingRotate, int precedingPower) {
		int rotateIncrement = generateRandomIncrementBetween(precedingRotate, -90, 90) * 15;
		int powerIncrement = generateRandomIncrementBetween(precedingPower, 0, 4);

		gene.setRotateIncrement(rotateIncrement);
		gene.setPowerIncrement(powerIncrement);
	}

	private void updateGeneRandomlyWithFixedRotate(Gene gene, int fixedRotate, int precedingPower) {
		int powerIncrement = generateRandomIncrementBetween(precedingPower, 0, 4);

		gene.setRotateIncrement(fixedRotate);
		gene.setPowerIncrement(powerIncrement);
	}

	private int generateRandomIncrementBetween(int precedingIncrement, int lowerBound, int higherBound) {
		int min = -1, max = 1;
		if (precedingIncrement == lowerBound) {
			min = 0;
		} else if (precedingIncrement == higherBound) {
			max = 0;
		}
		return RandomNumbersGenerator.generateRandomIntInRange(min, max);
	}

}