package io.github.nicolasdesnoust.marslander.genetic;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;

import io.github.nicolasdesnoust.marslander.genetic.model.Gene;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;

public class IndividualFactory {
    private final Queue<Individual> freeIndividuals;

    public IndividualFactory(int initialCapacity, int geneCount) {
        this.freeIndividuals = new ArrayDeque<>(initialCapacity);

        for (int i = 0; i < initialCapacity; i++) {
            Gene[] genes = new Gene[geneCount];
            for (int j = 0; j < geneCount; j++) {
                genes[j] = new Gene();
            }
            this.freeIndividuals.add(new Individual(genes));
        }
    }

    public void release(Individual individual) {
        this.freeIndividuals.add(individual);
    }

    public void release(Collection<Individual> individuals) {
        this.freeIndividuals.addAll(individuals);
    }

    public Individual acquire() {
        if (freeIndividuals.isEmpty()) {
            throw new RuntimeException("Individual factory is empty");
        }
        Individual individual = this.freeIndividuals.poll();
        reset(individual);

        return individual;
    }

    private void reset(Individual individual) {
        individual.setEvaluation(0.0);
        individual.setNormalizedEvaluation(0.0);
    }

}