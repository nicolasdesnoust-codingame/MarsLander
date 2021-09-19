package io.github.nicolasdesnoust.marslander.genetic.services;

import java.util.List;

import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.GameState;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.math.Point;
import io.github.nicolasdesnoust.marslander.math.Segment;
import io.github.nicolasdesnoust.marslander.solver.CapsuleService;
import io.github.nicolasdesnoust.marslander.solver.PathFinder;

public class PopulationEvaluator {
	private static final double ROTATE_THRESHOLD = 0;
	private static final int MAXIMUM_ACHIEVABLE_SPEED = 200;
	private static final double MAXIMUM_ACHIEVABLE_ROTATE = 90;
	private static final int MAXIMUM_EXTRA_V_SPEED = MAXIMUM_ACHIEVABLE_SPEED - CapsuleService.VERTICAL_SPEED_THRESHOLD;
	private static final int MAXIMUM_EXTRA_H_SPEED = MAXIMUM_ACHIEVABLE_SPEED - CapsuleService.HORIZONTAL_SPEED_THRESHOLD;
	private static final double MAXIMUM_EXTRA_ROTATE = MAXIMUM_ACHIEVABLE_ROTATE - ROTATE_THRESHOLD;

	private final PathFinder pathFinder;
	private final CapsuleService capsuleService;

	public PopulationEvaluator(
			CapsuleService capsuleService,
			PathFinder pathFinder) {
		this.capsuleService = capsuleService;
		this.pathFinder = pathFinder;
	}

	public void evaluate(
			List<Individual> population,
			List<Point> pathToFollow,
			GameState initialGameState) {

		for (Individual individual : population) {
			evaluateIndividual(individual, pathToFollow, initialGameState);
		}
	}

	public void evaluateIndividual(
			Individual individual,
			List<Point> pathToFollow,
			GameState initialGameState) {

		Segment landingArea = initialGameState.getLandingArea();
		double distanceRatio;
		double speedRatio = 0.0;
		double rotateRatio = 0.0;
		double fuelRatio = 0.0;

		Capsule lastCapsule = individual.getCapsule();
		List<Segment> marsSurface = initialGameState.getMars().getSurface();
		if (capsuleService.isCapsuleRightAboveLandingArea(lastCapsule, marsSurface, landingArea)
				&& lastCapsule.getLandingState().isTerminalState()) {
			distanceRatio = 1.0;
			if (Math.abs(lastCapsule.gethSpeed()) <= CapsuleService.HORIZONTAL_SPEED_THRESHOLD
					&& Math.abs(lastCapsule.getvSpeed()) <= CapsuleService.VERTICAL_SPEED_THRESHOLD
					&& lastCapsule.getRotate() == ROTATE_THRESHOLD) {
				speedRatio = 1;
				rotateRatio = 0.05;
				fuelRatio = computeFuelRatio(individual, initialGameState);
			} else {
				speedRatio = computeSpeedRatio(individual);
				rotateRatio = computeRotateRatio(individual);
			}
		} else {
			Capsule secondLastCapsule = new Capsule(lastCapsule);
			capsuleService.rollbackPositionAndSpeedUpdate(secondLastCapsule);
			List<Point> pathToLandingArea = pathFinder.findPath(secondLastCapsule, initialGameState);
			distanceRatio = computeDistanceRatio(pathToFollow, pathToLandingArea);
		}

		individual.setEvaluation(distanceRatio + speedRatio + rotateRatio + fuelRatio);
	}

	private double computeDistanceRatio(List<Point> pathToFollow, List<Point> path) {
		double initialDistance = Point.computeDistance(pathToFollow);
		double crashDistance = Point.computeDistance(path);

		return 1.0 - Math.min(crashDistance, (initialDistance - 0.000001)) / initialDistance;
	}

	private double computeSpeedRatio(Individual individual) {
		Capsule lastCapsule = individual.getCapsule();

		double absoluteHSpeed = Math.abs(lastCapsule.gethSpeed());
		double absoluteVSpeed = Math.abs(lastCapsule.getvSpeed());

		double extraHSpeed = Math.max(absoluteHSpeed - CapsuleService.HORIZONTAL_SPEED_THRESHOLD, 0);
		double extraVSpeed = Math.max(absoluteVSpeed - CapsuleService.VERTICAL_SPEED_THRESHOLD, 0);

		double deltaHRatio = 1 - extraHSpeed / MAXIMUM_EXTRA_H_SPEED;
		double deltaVRatio = 1 - extraVSpeed / MAXIMUM_EXTRA_V_SPEED;

		return deltaHRatio * 0.5 + deltaVRatio * 0.5;
	}

	private double computeRotateRatio(Individual individual) {
		Capsule lastCapsule = individual.getCapsule();

		double absoluteRotate = Math.abs(lastCapsule.getRotate());
		double extraRotate = Math.max(absoluteRotate - ROTATE_THRESHOLD, 0);
		double deltaRotateRatio = 1 - extraRotate / MAXIMUM_EXTRA_ROTATE;

		return deltaRotateRatio * 0.05;
	}

	private double computeFuelRatio(Individual individual, GameState gameState) {
		double initialFuel = gameState.getCapsule().getFuel();
		double lastFuel = individual.getCapsule().getFuel();

		return lastFuel / initialFuel;
	}

}
