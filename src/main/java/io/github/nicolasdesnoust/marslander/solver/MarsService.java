package io.github.nicolasdesnoust.marslander.solver;

import java.util.List;
import java.util.Optional;

import io.github.nicolasdesnoust.marslander.core.Mars;
import io.github.nicolasdesnoust.marslander.math.Line;
import io.github.nicolasdesnoust.marslander.math.Point;
import io.github.nicolasdesnoust.marslander.math.Segment;

public class MarsService {
	private double cachedMaxHeight;

	public Segment findLandingArea(Mars mars) {
		List<Segment> surface = mars.getSurface();

		for (Segment segment : surface) {
			if (segment.isHorizontal()) {
				return segment;
			}
		}

		return null;
	}

	public double findMaxHeight(Mars mars) {
		if (cachedMaxHeight != 0.0) {
			return cachedMaxHeight;
		} else {
			List<Segment> surface = mars.getSurface();

			double max = surface.stream()
					.map(Segment::getP1)
					.mapToDouble(Point::getY)
					.max()
					.orElseThrow(() -> new RuntimeException("Mars surface cannot be empty"));
			max = Double.max(max, surface.get(surface.size() - 1).getP2().getY());

			cachedMaxHeight = max;
			return max;
		}
	}

	public double findHeightWhereThereIsNoObstacleToLand(List<Segment> marsSurface, Segment landingArea) {
		final double minX = landingArea.getP1().getX();
		final double maxX = landingArea.getP2().getX();
		final double landingAreaY = landingArea.getP1().getY();

		return marsSurface.stream()
				.filter(segment -> !segment.equals(landingArea))
				.map(segment -> findMinHeightBetween(segment, minX, maxX))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.filter(height -> Math.round(height) != landingAreaY)
				.reduce(Double.MAX_VALUE, Math::min);
	}

	private Optional<Double> findMinHeightBetween(Segment segment, double minX, double maxX) {
		Point p1 = segment.getP1();
		Point p2 = segment.getP2();

		if (p2.getX() < minX || p1.getX() > maxX) {
			return Optional.empty();
		}

		Line line = Line.computeLineEquation(segment);

		double p1PrimeX = Math.max(p1.getX(), minX);
		double p1PrimeY = line.getA() * p1PrimeX + line.getB();
		double p2PrimeX = Math.min(p2.getX(), maxX);
		double p2PrimeY = line.getA() * p2PrimeX + line.getB();

		return Optional.of(Math.min(p1PrimeY, p2PrimeY));
	}
}