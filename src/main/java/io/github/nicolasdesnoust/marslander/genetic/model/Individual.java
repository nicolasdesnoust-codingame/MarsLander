package io.github.nicolasdesnoust.marslander.genetic.model;

import io.github.nicolasdesnoust.marslander.core.Capsule;

import java.util.Arrays;
import java.util.Objects;

public class Individual {
    private Integer id;
    private Gene[] genes;
    private Capsule capsule;
    private double evaluation;
    private double normalizedEvaluation;

    public Individual(int id, Gene[] genes) {
        this.id = id;
        this.genes = genes;
        this.evaluation = 0.0;
    }

    public double getNormalizedEvaluation() {
        return normalizedEvaluation;
    }

    public void setNormalizedEvaluation(double normalizedEvaluation) {
        this.normalizedEvaluation = normalizedEvaluation;
    }

    public Individual(Gene[] genes) {
        this.genes = genes;
    }

    public Individual(Individual individual) {
        this.id = individual.id;
        this.genes = new Gene[individual.genes.length];
        for (int i = 0; i < individual.genes.length; i++) {
            this.genes[i] = new Gene(individual.genes[i]);
        }
        this.capsule = new Capsule(individual.capsule);
        this.evaluation = individual.evaluation;
        this.normalizedEvaluation = individual.normalizedEvaluation;
    }

    public Individual() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Gene[] getGenes() {
        return genes;
    }

    public Capsule getCapsule() {
        return capsule;
    }

    public void setCapsule(Capsule capsule) {
        this.capsule = capsule;
    }

    public double getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(double evaluation) {
        this.evaluation = evaluation;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Individual that = (Individual) o;
        return id.equals(that.id);
    }

    public int hashCode() {
        return Objects.hash(id);
    }

    public String toString() {
        return "Individual{" +
                "id=" + id +
                ", genes=" + Arrays.toString(genes) +
                ", evaluation=" + evaluation +
                '}';
    }
}