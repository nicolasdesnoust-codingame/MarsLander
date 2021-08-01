package io.github.nicolasdesnoust.marslander.math;

import java.util.Objects;
import java.util.Optional;

public class Segment {
	private Point p1;
	private Point p2;
	private double epsilon = 0.0001d;
	private int preComputedHashCode = -1;

	public Segment(Point p1, Point p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	public Point getMiddle() {
		return new Point(
				p1.getX() + (p2.getX() - p1.getX()) / 2,
				p1.getY() + (p2.getY() - p1.getY()) / 2);
	}

	// https://lucidar.me/fr/mathematics/check-if-a-point-belongs-on-a-line-segment/
	public boolean doesIntersect(Segment otherSegment) {
		double thisMaxY;
		double otherMinY;
		double thisMinY;
		double otherMaxY;

		if (this.p1.getY() >= this.p2.getY()) {
			thisMaxY = this.p1.getY();
			thisMinY = this.p2.getY();
		} else {
			thisMaxY = this.p2.getY();
			thisMinY = this.p1.getY();
		}

		if (otherSegment.p1.getY() >= otherSegment.p2.getY()) {
			otherMaxY = otherSegment.p1.getY();
			otherMinY = otherSegment.p2.getY();
		} else {
			otherMaxY = otherSegment.p2.getY();
			otherMinY = otherSegment.p1.getY();
		}

		if (thisMaxY < otherMinY || thisMinY > otherMaxY) {
			return false;
		}

		double thisMinX;
		double thisMaxX;
		double otherMinX;
		double otherMaxX;

		if (this.p1.getX() >= this.p2.getX()) {
			thisMaxX = this.p1.getX();
			thisMinX = this.p2.getX();
		} else {
			thisMaxX = this.p2.getX();
			thisMinX = this.p1.getX();
		}

		if (otherSegment.p1.getX() >= otherSegment.p2.getX()) {
			otherMaxX = otherSegment.p1.getX();
			otherMinX = otherSegment.p2.getX();
		} else {
			otherMaxX = otherSegment.p2.getX();
			otherMinX = otherSegment.p1.getX();
		}

		Line line1 = Line.computeLineEquation(this.p1, this.p2);
		Line line2 = Line.computeLineEquation(otherSegment.p1, otherSegment.p2);

		Optional<Point> pointOptional = Line.calculateIntersectionPoint(line1, line2);
		if (pointOptional.isPresent()) {
			double intersectionX = pointOptional.get().getX();

			return intersectionX <= thisMaxX + epsilon
					&& intersectionX <= otherMaxX + epsilon
					&& intersectionX >= thisMinX - epsilon
					&& intersectionX >= otherMinX - epsilon;
		}

		return false;
	}

	public double distanceTo(Point point) {
		Vector p1Point = new Vector(p1, point);
		Vector p1p2 = new Vector(p1, p2);
		double t = p1Point.dot(p1p2) / Math.pow(p1p2.getLength(), 2);

		if (t < 0 || t > 1) {
			t = Math.min(Math.max(0, t), 1);
		}

		Point pointOrthogonalProjection = new Point(
				p1.getX() + p1p2.getX() * t,
				p1.getY() + p1p2.getY() * t);
		Vector distanceVector = new Vector(point, pointOrthogonalProjection);
		return distanceVector.getLength();
	}

	public boolean isHorizontal() {
		return Math.abs(p1.getY() - p2.getY()) < epsilon;
	}

	public boolean isVertical() {
		return Math.abs(p1.getX() - p2.getX()) < epsilon;
	}

	public Point getP1() {
		return p1;
	}

	public void setP1(Point p1) {
		this.p1 = p1;
	}

	public Point getP2() {
		return p2;
	}

	public void setP2(Point p2) {
		this.p2 = p2;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Segment segment = (Segment) o;
		return Objects.equals(p1, segment.p1) && Objects.equals(p2, segment.p2);
	}

	public void preComputeHashCode() {
		this.preComputedHashCode = hashCode();
	}

	@Override
	public int hashCode() {
		if (preComputedHashCode != -1) {
			return preComputedHashCode;
		} else {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((p1 == null) ? 0 : p1.hashCode());
			result = prime * result + ((p2 == null) ? 0 : p2.hashCode());
			return result;
		}
	}

	@Override
	public String toString() {
		return "Segment{" +
				"a=" + p1 +
				", b=" + p2 +
				'}';
	}
}
