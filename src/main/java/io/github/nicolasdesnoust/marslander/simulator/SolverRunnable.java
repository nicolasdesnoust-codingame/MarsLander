package io.github.nicolasdesnoust.marslander.simulator;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nicolasdesnoust.marslander.SolverConfiguration;
import io.github.nicolasdesnoust.marslander.solver.Solver;
import io.github.nicolasdesnoust.marslander.solver.SolverFactory;

public class SolverRunnable implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(SolverRunnable.class);

    private final PipedInputStream inputStream;
    private final PipedOutputStream outputStream;
    private final SolverConfiguration configuration;

    public SolverRunnable(
            PipedInputStream inputStream,
            PipedOutputStream outputStream,
            SolverConfiguration configuration
    ) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.configuration = configuration;
    }

    @Override
    public void run() {
        org.slf4j.MDC.put("turn", String.valueOf(SimulatorService.currentTurn));
        try (
                Scanner in = new Scanner(inputStream);
                PrintWriter out = new PrintWriter(outputStream, true)
        ) {
            log.debug("Solver thread started");
            SolverFactory solverFactory = new SolverFactory();
            Solver solver = solverFactory.createSolver(configuration);
            solver.solve(in, out);
        } catch (NoSuchElementException e) {
            log.debug("Solver thread ended");
        }
    }
}
