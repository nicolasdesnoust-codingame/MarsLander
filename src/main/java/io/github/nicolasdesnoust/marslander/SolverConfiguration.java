package io.github.nicolasdesnoust.marslander;

public class SolverConfiguration {
    private String testCase;
    private int populationSize;
    private int numberOfGenesPerIndividual;
	private int restartGenericTurn;
	private boolean restartGeneric;
    
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
	public int getNumberOfGenesPerIndividual() {
		return numberOfGenesPerIndividual;
	}
	public void setNumberOfGenesPerIndividual(int numberOfGenesPerIndividual) {
		this.numberOfGenesPerIndividual = numberOfGenesPerIndividual;
	}
	public int getRestartGenericTurn() {
		return restartGenericTurn;
	}
	public void setRestartGenericTurn(int restartGenericTurn) {
		this.restartGenericTurn = restartGenericTurn;
	}
	public boolean isRestartGeneric() {
		return restartGeneric;
	}
	public void setRestartGeneric(boolean restartGeneric) {
		this.restartGeneric = restartGeneric;
	}
}
