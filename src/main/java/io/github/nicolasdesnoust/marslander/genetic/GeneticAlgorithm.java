package io.github.nicolasdesnoust.marslander.genetic;

import io.github.nicolasdesnoust.marslander.config.SolverConfiguration;
import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.InitialGameState;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import io.github.nicolasdesnoust.marslander.genetic.services.IndividualProcessor;
import io.github.nicolasdesnoust.marslander.genetic.services.PopulationEvolver;
import io.github.nicolasdesnoust.marslander.genetic.services.PopulationGenerator;
import io.github.nicolasdesnoust.marslander.logs.LoggableGene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

public class GeneticAlgorithm {
    private static final Logger log = LoggerFactory.getLogger(GeneticAlgorithm.class);
    public static int individualCount = 0;
    private final PopulationGenerator generator = new PopulationGenerator();
    private final PopulationEvolver evolver;
    private final IndividualProcessor processor = new IndividualProcessor();

    private final InitialGameState initialGameState;

    public GeneticAlgorithm(InitialGameState initialGameState) {
        this.initialGameState = initialGameState;
        evolver = new PopulationEvolver(initialGameState);
    }

    public List<Individual> findBestPopulation(SolverConfiguration configuration) {
        individualCount = 0;
        List<Individual> population = generator.generateRandomPopulation(configuration, initialGameState);
        population.forEach(individual -> processor.process(individual, initialGameState));

        int currentGeneration = 0;
        logPopulation(population, currentGeneration);

        boolean solutionFound = false;
        while (!solutionFound && currentGeneration <= configuration.getGenerations()) {
            log.info("Current generation : {}", currentGeneration);
            population = evolver.evolve(population, configuration.getNumberOfSelections(), currentGeneration);
            log.info("Evolve done");

            if (hasASolution(population)) {
                System.err.println("Solution found !");
                solutionFound = true;
            }

            currentGeneration++;
            logPopulation(population, currentGeneration);
        }

        return population;
    }

    public void logPopulation(List<Individual> population, int generation) {
        if (generation % 10 != 0) {
            return;
        }
        for (Individual individual : population) {
            Capsule[] capsules = individual.getCapsules();

            List<LoggableGene> genes = new ArrayList<>(capsules.length);
            LoggableGene previousGene = null;
            for (int i = 0; i < capsules.length; i++) {
                Capsule capsule = capsules[i];
                LoggableGene gene = new LoggableGene();
                gene.setIndex(i);
                gene.setX((int) Math.round(capsule.getPosition().getX()));
                gene.setY((int) Math.round(capsule.getPosition().getY()));
                if (previousGene != null
                        && gene.getX() == previousGene.getX()
                        && gene.getY() == previousGene.getY()) {
                    break;
                }
                genes.add(gene);
                previousGene = gene;
            }
            log.debug("Individual {}: {} {} {} {}",
                    individual.getId(),
                    kv("generation", generation),
                    kv("type", "individual-" + individual.getId()),
                    kv("evaluation", individual.getEvaluation()),
                    kv("genes", genes));
        }
    }

    private boolean hasASolution(List<Individual> population) {
        for (Individual individual : population) {
            if (individual.isSolution()) {
                return true;
            }
        }
        return false;
    }

}
