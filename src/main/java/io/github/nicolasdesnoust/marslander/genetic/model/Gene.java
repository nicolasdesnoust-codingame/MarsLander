package io.github.nicolasdesnoust.marslander.genetic.model;

public class Gene {
	private int rotateIncrement;
	private int powerIncrement;

	public Gene() {
	}

	public Gene(int rotateIncrement, int powerIncrement) {
		this.rotateIncrement = rotateIncrement;
		this.powerIncrement = powerIncrement;
	}

	public Gene(Gene gene) {
		this.rotateIncrement = gene.rotateIncrement;
		this.powerIncrement = gene.powerIncrement;
	}

	public int getRotateIncrement() {
		return rotateIncrement;
	}

	public void setRotateIncrement(int rotateIncrement) {
		this.rotateIncrement = rotateIncrement;
	}

	public int getPowerIncrement() {
		return powerIncrement;
	}

	public void setPowerIncrement(int powerIncrement) {
		this.powerIncrement = powerIncrement;
	}

	public String toString() {
		return "Gene{" +
				"rotateIncrement=" + rotateIncrement +
				", powerIncrement=" + powerIncrement +
				'}';
	}
}