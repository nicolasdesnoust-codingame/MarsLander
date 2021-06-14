package io.github.nicolasdesnoust.marslander.simulator;

import io.github.nicolasdesnoust.marslander.core.InitialGameState;
import io.github.nicolasdesnoust.marslander.core.TestCaseParser;
import io.github.nicolasdesnoust.marslander.config.SolverConfiguration;
import io.github.nicolasdesnoust.marslander.core.GameStateParser;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SimulatorService {
    public static final AtomicInteger currentTurn = new AtomicInteger(0);
    public static final GameStateParser gameStateParser = new GameStateParser();

    public void runSimulation(SolverConfiguration configuration) throws IOException, InterruptedException {
        org.slf4j.MDC.put("turn", String.valueOf(currentTurn));
        
        InitialGameState initialGameState = TestCaseParser.parseFileContent(
        		configuration.getTestCase(), 
        		gameStateParser::parseInitialGameState
        );
        
        try (
                final PipedInputStream simulatorIn = new PipedInputStream();
                final PipedOutputStream simulatorOut = new PipedOutputStream();
                final PipedInputStream solverIn = new PipedInputStream();
                final PipedOutputStream solverOut = new PipedOutputStream();
        ) {
            simulatorIn.connect(solverOut);
            solverIn.connect(simulatorOut);
            
            Thread solverThread = new Thread(new SolverRunnable(solverIn, solverOut, configuration), "Solver");
            Thread simulatorThread = new Thread(
                    new SimulatorRunnable(simulatorIn, simulatorOut, initialGameState),
                    "Simulator"
            );

            solverThread.start();
            simulatorThread.start();

            simulatorThread.join();
            solverThread.interrupt();
        }
    }
}
