package marslander.genetic.model;

import marslander.core.Capsule;

import java.util.Objects;

public class GeneMetadata {
    private Capsule capsule;

    public GeneMetadata(Capsule capsule) {
        this.capsule = capsule;
    }

    public Capsule getCapsule() {
        return capsule;
    }

    public void setCapsule(Capsule capsule) {
        this.capsule = capsule;
    }

    @Override
    public String toString() {
        return "GeneMetadata{" +
                "capsule=" + capsule +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneMetadata that = (GeneMetadata) o;
        return Objects.equals(capsule, that.capsule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(capsule);
    }
}
