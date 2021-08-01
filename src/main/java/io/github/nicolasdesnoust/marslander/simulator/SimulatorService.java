package io.github.nicolasdesnoust.marslander.simulator;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import io.github.nicolasdesnoust.marslander.SolverConfiguration;
import io.github.nicolasdesnoust.marslander.core.GameState;
import io.github.nicolasdesnoust.marslander.core.GameStateParser;
import io.github.nicolasdesnoust.marslander.core.TestCaseParser;
import io.github.nicolasdesnoust.marslander.solver.MarsService;

@Service
public class SimulatorService {
	public static final AtomicInteger currentTurn = new AtomicInteger(0);
	private static final MarsService marsService = new MarsService();
	public static final GameStateParser gameStateParser = new GameStateParser(marsService);

	public void runSimulation(SolverConfiguration configuration) throws IOException, InterruptedException {
		org.slf4j.MDC.put("turn", String.valueOf(currentTurn));

		GameState initialGameState = TestCaseParser.parseFileContent(
				configuration.getTestCase(),
				gameStateParser::parseInitialGameState);

		try (
				final PipedInputStream simulatorIn = new PipedInputStream();
				final PipedOutputStream simulatorOut = new PipedOutputStream();
				final PipedInputStream solverIn = new PipedInputStream();
				final PipedOutputStream solverOut = new PipedOutputStream();
		) {
			simulatorIn.connect(solverOut);
			solverIn.connect(simulatorOut);

			Thread solverThread = new Thread(new SolverRunnable(solverIn, solverOut, configuration), "Solver");
			Thread simulatorThread = new Thread(new SimulatorRunnable(simulatorIn, simulatorOut, initialGameState), "Simulator");

			solverThread.start();
			simulatorThread.start();

			simulatorThread.join();
			solverThread.interrupt();
		}
	}
}
