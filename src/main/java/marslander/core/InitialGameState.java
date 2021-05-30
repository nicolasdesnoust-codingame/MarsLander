package marslander.core;

import java.util.Objects;

public class InitialGameState {
    private final Mars mars;
    private final Capsule capsule;

    public InitialGameState(Mars mars, Capsule capsule) {
        this.mars = mars;
        this.capsule = capsule;
    }

    public Mars getMars() {
        return mars;
    }

    public Capsule getCapsule() {
        return capsule;
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
