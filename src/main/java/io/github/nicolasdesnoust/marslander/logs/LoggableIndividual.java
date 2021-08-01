package io.github.nicolasdesnoust.marslander.logs;

import java.util.ArrayList;
import java.util.List;

import io.github.nicolasdesnoust.marslander.genetic.model.Individual;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
final class LoggableIndividual {
	private Integer individualId;
	private List<LoggableCapsule> capsules = new ArrayList<>();

	private Double evaluation;

	public LoggableIndividual(Individual individual) {
		this.individualId = individual.getId();
	}
	
	void addCapsule(LoggableCapsule capsule) {
		capsules.add(capsule);
	}
}
