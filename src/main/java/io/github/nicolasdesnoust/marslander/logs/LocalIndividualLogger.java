package io.github.nicolasdesnoust.marslander.logs;

import static net.logstash.logback.argument.StructuredArguments.kv;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.genetic.model.Individual;

public class LocalIndividualLogger implements IndividualLogger {
	private static final int LOG_INTERVAL = 10;
	private static final Logger log = LoggerFactory.getLogger(LocalIndividualLogger.class);

	private Map<Integer, LoggableIndividual> loggableIndividualMap = new HashMap<>();

	@Override
	@SuppressWarnings("java:S3457")
	public void logGeneration(List<Individual> individuals, int generation) {
		List<Integer> ids = individuals.stream()
				.map(Individual::getId)
				.collect(Collectors.toList());

		if (generation % LOG_INTERVAL == 0) {
			List<Individual> topIndividuals = individuals.stream()
					.sorted(Comparator.comparingDouble(Individual::getEvaluation).reversed())
					.limit(3)
					.collect(Collectors.toList());
			log.info("Generation {} evaluations : {}",
					generation,
					topIndividuals.stream().map(Individual::getEvaluation).collect(Collectors.toList()));
			log.info("Generation {} fuels :       {}",
					generation,
					topIndividuals.stream().map(i -> i.getCapsule().getFuel()).collect(Collectors.toList()));

			ids.forEach(id -> {
				LoggableIndividual loggableIndividual = loggableIndividualMap.get(id);
				log.debug("Individual {}: {}",
						loggableIndividual.getIndividualId(),
						loggableIndividual.getEvaluation(),
						kv("generation", generation),
						kv("type", "individual"),
						kv("individual", loggableIndividual));
			});
		}

		Set<Integer> idSet = new HashSet<>(ids);
		loggableIndividualMap.entrySet().removeIf(entry -> !idSet.contains(entry.getKey()));
	}

	@Override
	public void storeIndividual(Individual individual) {
		loggableIndividualMap.put(individual.getId(), new LoggableIndividual(individual));
	}

	@Override
	public void storeCapsuleOf(Integer individualId, Capsule capsule) {
		if (loggableIndividualMap.containsKey(individualId)) {
			LoggableIndividual individual = loggableIndividualMap.get(individualId);
			int capsulesSize = individual.getCapsules().size();
			individual.addCapsule(new LoggableCapsule(capsule, capsulesSize, false));
		}
	}

	@Override
	public void addMissingEvaluations(List<Individual> individuals) {
		individuals.forEach(i -> {
			LoggableIndividual loggableIndividual = loggableIndividualMap.get(i.getId());
			loggableIndividual.setEvaluation(i.getEvaluation());
		});
	}

}
