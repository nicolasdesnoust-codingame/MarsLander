package marslander.genetic.model;

import java.util.Arrays;

public class Individual {
    private Gene[] genes;
    private GeneMetadata[] genesMetadata;
    private double evaluation;

    public Individual(Gene[] genes) {
        this.genes = genes;
        this.genesMetadata = new GeneMetadata[genes.length];
        this.evaluation = 0.0;
    }

    public Gene[] getGenes() {
        return genes;
    }

    public void setGenes(Gene[] genes) {
        this.genes = genes;
    }

    public GeneMetadata[] getGenesMetadata() {
        return genesMetadata;
    }

    public void setGenesMetadata(GeneMetadata[] genesMetadata) {
        this.genesMetadata = genesMetadata;
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
        return Arrays.equals(genes, that.genes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(genes);
    }

    @Override
    public String toString() {
        return "Individual{" +
                "genes=" + Arrays.toString(genes) +
                ", evaluation=" + evaluation +
                '}';
    }
}
