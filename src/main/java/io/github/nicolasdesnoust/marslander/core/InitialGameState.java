package io.github.nicolasdesnoust.marslander.core;

import java.util.Objects;

import io.github.nicolasdesnoust.marslander.math.Segment;

public class InitialGameState {
    private final Mars mars;
    private final Capsule capsule;
    private final Segment landingArea;

    public InitialGameState(Mars mars, Capsule capsule, Segment landingArea) {
        this.mars = mars;
        this.capsule = capsule;
        this.landingArea = landingArea;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InitialGameState initialGameState = (InitialGameState) o;
        return Objects.equals(mars, initialGameState.mars) && Objects.equals(capsule, initialGameState.capsule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mars, capsule);
    }
}
