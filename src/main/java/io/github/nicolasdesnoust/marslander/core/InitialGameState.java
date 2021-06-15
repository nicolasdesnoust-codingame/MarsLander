package io.github.nicolasdesnoust.marslander.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.nicolasdesnoust.marslander.math.Segment;

public class InitialGameState {
	private final Mars mars;
	private final Capsule capsule;
	private final Segment landingArea;
	private final Segment landingAreaReversed;
	private final List<Segment> marsSurfaceReversed;

	public InitialGameState(Mars mars, Capsule capsule, Segment landingArea) {
		this.mars = mars;
		this.capsule = capsule;
		this.landingArea = landingArea;
		this.marsSurfaceReversed = reverseMarsSurface(mars.getSurface());
		this.landingAreaReversed = reverseSegment(landingArea);
	}

	private List<Segment> reverseMarsSurface(List<Segment> surface) {
		List<Segment> reverseMarsSurface = new ArrayList<>(surface.size());

		for (int i = surface.size() - 1; i >= 0; i--) {
			Segment surfaceSegment = surface.get(i);
			reverseMarsSurface.add(reverseSegment(surfaceSegment));
		}

		return reverseMarsSurface;
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
