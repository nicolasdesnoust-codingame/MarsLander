package io.github.nicolasdesnoust.marslander;

import java.io.PrintWriter;
import java.util.Scanner;

import io.github.nicolasdesnoust.marslander.core.Timer;
import io.github.nicolasdesnoust.marslander.solver.Solver;
import io.github.nicolasdesnoust.marslander.solver.SolverFactory;

public class CodingameStarter {
    public void solve(int testNumber, Scanner in, PrintWriter out) {
        Timer.startFirstTurnTimer();
        SolverConfiguration configuration = new SolverConfiguration();
        configuration.setNumberOfGenesPerIndividual(85);
        configuration.setNumberOfSelections(60);
        configuration.setPopulationSize(140);
        configuration.setRestartGeneric(true);
        configuration.setRestartGenericTurn(15);
        
        SolverFactory solverFactory = new SolverFactory();
        Solver solver = solverFactory.createSolver(configuration);
        solver.solve(in, out);
    }
}
