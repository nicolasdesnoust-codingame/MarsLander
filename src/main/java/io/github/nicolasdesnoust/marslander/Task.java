package io.github.nicolasdesnoust.marslander;

import java.io.PrintWriter;
import java.util.Scanner;

import io.github.nicolasdesnoust.marslander.config.SolverConfiguration;
import io.github.nicolasdesnoust.marslander.solver.SolverMain;

public class Task {
	public void solve(int testNumber, Scanner in, PrintWriter out) {
		SolverConfiguration solverConfiguration = new SolverConfiguration();
		solverConfiguration.setTestCase("test-case-01");
		solverConfiguration.setPopulationSize(50);
		solverConfiguration.setGenerations(200);
		solverConfiguration.setNumberOfSelections(25);
		solverConfiguration.setNumberOfGenesPerIndividual(100);

		SolverMain.solve(in, out, solverConfiguration);
	}
}
