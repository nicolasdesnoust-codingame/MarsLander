package marslander.genetic.services;

import marslander.core.Capsule;
import marslander.solver.CapsuleService;
import marslander.core.Mars;
import marslander.genetic.model.Gene;
import marslander.genetic.model.GeneMetadata;
import marslander.genetic.model.Individual;

public class IndividualProcessor {
    private final CapsuleService capsuleService = new CapsuleService();

    public void process(Individual individual, Capsule capsule, Mars mars) {
        Capsule capsuleCopy = new Capsule(capsule);

        for (int i = 0; i < individual.getGenes().length; i++) {
            Gene gene = individual.getGenes()[i];
            int requestedRotate = capsuleCopy.getRotate() + gene.getRotate();
            int requestedPower = capsuleCopy.getPower() + gene.getPower();

            capsuleService.updateCapsuleState(capsuleCopy, requestedRotate, requestedPower, mars);
            individual.getGenesMetadata()[i] = new GeneMetadata(capsuleCopy);
            capsuleCopy = new Capsule(capsuleCopy);
        }
    }
}
