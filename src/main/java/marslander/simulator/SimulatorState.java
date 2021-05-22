package marslander.simulator;

import marslander.Capsule;
import marslander.Mars;

import java.util.Objects;

public class SimulatorState {
    private Mars mars;
    private Capsule capsule;
    private LandingState landingState;

    public SimulatorState(Mars mars, Capsule capsule) {
        this.mars = mars;
        this.capsule = capsule;
        this.landingState = LandingState.STILL_FLYING;
    }

    public Mars getMars() {
        return mars;
    }

    public void setMars(Mars mars) {
        this.mars = mars;
    }

    public Capsule getCapsule() {
        return capsule;
    }

    public void setCapsule(Capsule capsule) {
        this.capsule = capsule;
    }

    public LandingState getLandingState() {
        return landingState;
    }

    public void setLandingState(LandingState landingState) {
        this.landingState = landingState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimulatorState that = (SimulatorState) o;
        return Objects.equals(mars, that.mars) && Objects.equals(capsule, that.capsule) && landingState == that.landingState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mars, capsule, landingState);
    }

    @Override
    public String toString() {
        return "SimulatorState{" +
                "mars=" + mars +
                ", capsule=" + capsule +
                ", landingState=" + landingState +
                '}';
    }
}
