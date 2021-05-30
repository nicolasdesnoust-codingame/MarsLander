package marslander.simulator;

import marslander.core.InitialGameState;
import marslander.core.GameStateParser;
import marslander.util.TestCaseParser;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulatorMain {
    private static final String TEST_CASE_NAME = "test-case-04";
    public static final AtomicInteger currentTurn = new AtomicInteger(0);
    public static final GameStateParser gameStateParser = new GameStateParser();

    public static void main(String[] args) throws IOException, InterruptedException {
        org.slf4j.MDC.put("turn", String.valueOf(currentTurn));
        try (
                final PipedInputStream simulatorIn = new PipedInputStream();
                final PipedOutputStream simulatorOut = new PipedOutputStream();
                final PipedInputStream solverIn = new PipedInputStream();
                final PipedOutputStream solverOut = new PipedOutputStream();
        ) {
            simulatorIn.connect(solverOut);
            solverIn.connect(simulatorOut);

            Thread solverThread = new Thread(new SolverRunnable(solverIn, solverOut), "Solver");
            InitialGameState initialGameState = TestCaseParser
                    .parseFileContent(TEST_CASE_NAME, gameStateParser::parseInitialGameState);
            Thread simulatorThread = new Thread(
                    new SimulatorRunnable(simulatorIn, simulatorOut, initialGameState),
                    "Simulator"
            );

            solverThread.start();
            simulatorThread.start();

            simulatorThread.join();
            solverThread.interrupt();
            solverThread.join();
        }
    }
}
