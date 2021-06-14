package io.github.nicolasdesnoust.marslander.config;

public class SolverConfiguration {
    private String testCase;
    private int populationSize;
    private int generations;
    private int numberOfSelections;
    private int numberOfGenesPerIndividual;
    
	public String getTestCase() {
		return testCase;
	}
	public void setTestCase(String testCase) {
		this.testCase = testCase;
	}
	public int getPopulationSize() {
		return populationSize;
	}
	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}
	public int getGenerations() {
		return generations;
	}
	public void setGenerations(int generations) {
		this.generations = generations;
	}
	public int getNumberOfSelections() {
		return numberOfSelections;
	}
	public void setNumberOfSelections(int numberOfSelections) {
		this.numberOfSelections = numberOfSelections;
	}
	public int getNumberOfGenesPerIndividual() {
		return numberOfGenesPerIndividual;
	}
	public void setNumberOfGenesPerIndividual(int numberOfGenesPerIndividual) {
		this.numberOfGenesPerIndividual = numberOfGenesPerIndividual;
	}
}
