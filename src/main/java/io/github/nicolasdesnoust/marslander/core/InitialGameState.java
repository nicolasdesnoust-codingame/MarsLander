package io.github.nicolasdesnoust.marslander.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.github.nicolasdesnoust.marslander.math.Line;
import io.github.nicolasdesnoust.marslander.math.Point;
import io.github.nicolasdesnoust.marslander.math.Segment;

public class InitialGameState {
	private final Mars mars;
	private final Capsule capsule;
	private final Segment landingArea;
	private final Segment landingAreaReversed;
	private final List<Segment> marsSurfaceReversed;
	private final Map<Integer, Point> pathPoints;

	public InitialGameState(Mars mars, Capsule capsule, Segment landingArea) {
		this.mars = mars;
		this.capsule = capsule;
		this.landingArea = landingArea;
		this.marsSurfaceReversed = reverseMarsSurface(mars.getSurface());
		this.landingAreaReversed = reverseSegment(landingArea);
		this.pathPoints = findPathPoints(mars.getSurface());
	}

	private Map<Integer, Point> findPathPoints(List<Segment> surface) {
		Map<Integer, Point> pathPoints = new HashMap<>();

		for (int i = 0; i < surface.size() - 1; i++) {
			Segment currentSurfaceSegment = surface.get(i);
			Point nextPoint = surface.get(i + 1).getP2();
			Line bisectingLine = Line.getBisectingLine(
					currentSurfaceSegment.getP1(),
					currentSurfaceSegment.getP2(),
					nextPoint);

			Point newPathPointAbove = bisectingLine.getPointAbove(currentSurfaceSegment.getP2(), 200);
			Point newPathPointBelow = bisectingLine.getPointBelow(currentSurfaceSegment.getP2(), 200);

			Point newPathPoint;
			if (i > 0) {
				int index = i;
				do {
					newPathPoint = getPointToAvoidACollisionWith(pathPoints, i,
							currentSurfaceSegment, newPathPointAbove,
							newPathPointBelow);
					currentSurfaceSegment = surface.get(--index);
				} while (newPathPoint == null && index > 0);

				if (newPathPoint == null) {
					throw new RuntimeException(
							"Unable to choose between above and below points for index " + "'" + (i + 1) + "'");
				}
			} else {
				newPathPoint = newPathPointAbove;
			}
			pathPoints.put(i + 1, newPathPoint);
		}

		return pathPoints;
	}

	private Point getPointToAvoidACollisionWith(Map<Integer, Point> pathPoints, int i,
			Segment surfaceSegment, Point newPathPointAbove, Point newPathPointBelow) {
		Point previousPathPoint = pathPoints.get(i);
		Segment aboveSegment = new Segment(previousPathPoint, newPathPointAbove);
		Segment belowSegment = new Segment(previousPathPoint, newPathPointBelow);

		Point newPathPoint = null;
		if (aboveSegment.doesIntersect(surfaceSegment)) {
			newPathPoint = newPathPointBelow;
		} else if (belowSegment.doesIntersect(surfaceSegment)) {
			newPathPoint = newPathPointAbove;
		}

		return newPathPoint;
	}

	private List<Segment> reverseMarsSurface(List<Segment> surface) {
		List<Segment> reverseMarsSurface = new ArrayList<>(surface.size());

		for (int i = surface.size() - 1; i >= 0; i--) {
			Segment surfaceSegment = surface.get(i);
			reverseMarsSurface.add(reverseSegment(surfaceSegment));
		}

		return reverseMarsSurface;
	}

	public Map<Integer, Point> getPathPoints() {
		return pathPoints;
	}

	public Segment getLandingAreaReversed() {
		return landingAreaReversed;
	}

	public List<Segment> getMarsSurfaceReversed() {
		return marsSurfaceReversed;
	}

	private Segment reverseSegment(Segment landingArea) {
		return new Segment(landingArea.getP2(), landingArea.getP1());
	}

	public Mars getMars() {
		return mars;
	}

	public Capsule getCapsule() {
		return capsule;
	}

	public Segment getLandingArea() {
		return landingArea;
	}

	@Override
	public String toString() {
		return "GameState{" +
				"mars=" + mars +
				", capsule=" + capsule +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		InitialGameState initialGameState = (InitialGameState) o;
		return Objects.equals(mars, initialGameState.mars) && Objects.equals(capsule, initialGameState.capsule);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mars, capsule);
	}
}
