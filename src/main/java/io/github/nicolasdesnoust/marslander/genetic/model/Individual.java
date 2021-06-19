package io.github.nicolasdesnoust.marslander.genetic.model;

import io.github.nicolasdesnoust.marslander.core.Capsule;

import java.util.Arrays;
import java.util.Objects;

public class Individual {
    private final Integer id;
    private Gene[] genes;
    private Capsule[] capsules;
    private double evaluation;
    private boolean solution;

    public Individual(int id, Gene[] genes) {
        this.id = id;
        this.genes = genes;
        this.capsules = new Capsule[genes.length];
        this.evaluation = 0.0;
    }

    public Integer getId() {
        return id;
    }

    public Gene[] getGenes() {
        return genes;
    }

    public void setGenes(Gene[] genes) {
        this.genes = genes;
    }

    public Capsule[] getCapsules() {
        return capsules;
    }

    public void setCapsules(Capsule[] capsules) {
        this.capsules = capsules;
    }

    public double getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(double evaluation) {
        this.evaluation = evaluation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Individual that = (Individual) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Individual{" +
                "id=" + id +
                ", genes=" + Arrays.toString(genes) +
                ", evaluation=" + evaluation +
                '}';
    }

    public Capsule getLastCapsule() {
        return capsules[capsules.length - 1];
    }

    public boolean isSolution() {
        return solution;
    }

    public void setSolution(boolean solution) {
        this.solution = solution;
    }
}
