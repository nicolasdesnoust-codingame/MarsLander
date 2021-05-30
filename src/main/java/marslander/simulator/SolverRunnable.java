package marslander.simulator;

import marslander.solver.SolverMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class SolverRunnable implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(SolverRunnable.class);

    private final PipedInputStream inputStream;
    private final PipedOutputStream outputStream;

    public SolverRunnable(PipedInputStream inputStream, PipedOutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        org.slf4j.MDC.put("turn", String.valueOf(SimulatorMain.currentTurn));

        Scanner in = new Scanner(inputStream);
        PrintWriter out = new PrintWriter(outputStream, true);
        log.debug("Solver thread started");
        try {
            SolverMain.solve(in, out);
        } catch (NoSuchElementException e) {
            log.debug("Solver thread ended");
        }
    }
}
