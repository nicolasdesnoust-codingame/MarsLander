package io.github.nicolasdesnoust.marslander.solver;

public class MovementInstructions {
	private final int rotate;
	private final int power;
	
	public MovementInstructions(int rotate, int power) {
		this.rotate = rotate;
		this.power = power;
	}
	public int getRotate() {
		return rotate;
	}
	public int getPower() {
		return power;
	}
	@Override
	public String toString() {
		return rotate + " " + power;
	}
	
}
