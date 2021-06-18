package io.github.nicolasdesnoust.marslander;

import io.github.nicolasdesnoust.marslander.config.SolverConfiguration;
import io.github.nicolasdesnoust.marslander.solver.SolverMain;

import java.util.Scanner;
import java.io.PrintWriter;

public class Task {
    public void solve(int testNumber, Scanner in, PrintWriter out) {
        SolverConfiguration solverConfiguration = new SolverConfiguration();
        solverConfiguration.setTestCase("test-case-01");
        solverConfiguration.setPopulationSize(70);
        solverConfiguration.setGenerations(150);
        solverConfiguration.setNumberOfSelections(35);
        solverConfiguration.setNumberOfGenesPerIndividual(200);

        SolverMain.solve(in, out, solverConfiguration);
    }
}
