package io.github.nicolasdesnoust.marslander.simulator;

import static net.logstash.logback.argument.StructuredArguments.kv;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.GameState;
import io.github.nicolasdesnoust.marslander.math.Point;
import io.github.nicolasdesnoust.marslander.math.Segment;
import io.github.nicolasdesnoust.marslander.solver.CapsuleService;
import io.github.nicolasdesnoust.marslander.solver.MarsService;
import io.github.nicolasdesnoust.marslander.solver.SegmentChunks;
import io.github.nicolasdesnoust.marslander.solver.Solver;

public class SimulatorRunnable implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(SimulatorRunnable.class);

	private final PipedInputStream inputStream;
	private final PipedOutputStream outputStream;
	private final GameState state;
	private final CapsuleService capsuleService = new CapsuleService(
			new SegmentChunks(),
			new MarsService());

	public SimulatorRunnable(
			PipedInputStream inputStream,
			PipedOutputStream outputStream,
			GameState state) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.state = state;
	}

	@Override
	public void run() {
		org.slf4j.MDC.put("turn", String.valueOf(SimulatorService.currentTurn));

		log.debug("Simulator thread started");
		log.info("Initial capsule state: {}", kv("capsule", state.getCapsule()));
		log.info("Capsule position: {} {} {}",
				kv("type", "capsule"),
				kv("x", Math.round(state.getCapsule().getPosition().getX())),
				kv("y", Math.round(state.getCapsule().getPosition().getY())));

		Scanner in = new Scanner(inputStream);
		PrintWriter out = new PrintWriter(outputStream, true);

		sendMarsSurfaceToSolver(out);
		boolean isSolverAnswering = true;
		while (!state.getCapsule().getLandingState().isTerminalState() && isSolverAnswering) {
			sendCapsuleToSolver(out);
			log.debug("Waiting solver answer..");
			isSolverAnswering = processSolverResponse(in);

			SimulatorService.currentTurn.incrementAndGet();
			org.slf4j.MDC.put("turn", String.valueOf(SimulatorService.currentTurn));

			// log.info("Capsule position: {} {}",
			// kv("type", "real-capsule"),
			// kv("capsule", state.getCapsule()));
		}
		log.info("Last capsule state: {}", state.getCapsule());
	}

	private void sendMarsSurfaceToSolver(PrintWriter out) {
		List<Segment> marsSurface = state.getMars().getSurface();

		out.println(marsSurface.size() + 1);
		int index = 0;
		for (Segment segment : marsSurface) {
			sendPointToSolver(out, segment.getP1(), index++);
		}
		sendPointToSolver(out, marsSurface.get(marsSurface.size() - 1).getP2(), index);
	}

	private void sendPointToSolver(PrintWriter out, Point point, int index) {
		out.println(Math.round(point.getX()) + " " + Math.round(point.getY()));

		log.info("Surface point: {} {} {} {}",
				kv("type", "surface"),
				kv("index", index),
				kv("x", Math.round(point.getX())),
				kv("y", Math.round(point.getY())));
	}

	private void sendCapsuleToSolver(PrintWriter out) {
		Capsule capsule = state.getCapsule();
		out.println(String.format(
				"%d %d %d %d %d %d %d",
				Math.round(capsule.getPosition().getX()),
				Math.round(capsule.getPosition().getY()),
				Math.round(capsule.gethSpeed()),
				Math.round(capsule.getvSpeed()),
				Math.round(capsule.getFuel()),
				capsule.getRotate(),
				capsule.getPower()));
	}

	private boolean processSolverResponse(Scanner in) {
		if (in.hasNextLine()) {
			int rotate = in.nextInt();
			int power = in.nextInt();
			log.debug("Received {} {}", kv("rotate", rotate), kv("power", power));

			capsuleService.updateCapsuleState(state.getCapsule(), rotate, power, state);
			// log.info("New landing state: {}", kv("landingState",
			// state.getCapsule().getLandingState()));
			return true;
		} else {
			return false;
		}
	}
}
