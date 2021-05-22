package marslander.math;

import java.util.Objects;
import java.util.Optional;

public class Segment {
    private Point p1;
    private Point p2;

    public Segment(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Point getMiddle() {
        return new Point(
                p1.getX() + (p2.getX() - p1.getX()) / 2,
                p1.getY() + (p2.getY() - p1.getY()) / 2
        );
    }

    // https://lucidar.me/fr/mathematics/check-if-a-point-belongs-on-a-line-segment/
    public boolean doesIntersect(Segment otherSegment) {
        Line line1 = Line.computeLineEquation(this);
        Line line2 = Line.computeLineEquation(otherSegment);

        Optional<Point> pointOptional = Line.calculateIntersectionPoint(line1, line2);
        if(pointOptional.isPresent()) {
            Point intersection = pointOptional.get();
            double thisMinX = Double.min(this.p1.getX(), this.p2.getX());
            double thisMaxX = Double.max(this.p1.getX(), this.p2.getX());
            double otherMinX = Double.min(otherSegment.p1.getX(), otherSegment.p2.getX());
            double otherMaxX = Double.max(otherSegment.p1.getX(), otherSegment.p2.getX());

            return intersection.getX() <= thisMaxX
                    && intersection.getX() >= thisMinX
                    && intersection.getX() <= otherMaxX
                    && intersection.getX() >= otherMinX;
        }

        return false;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Segment segment = (Segment) o;
        return Objects.equals(p1, segment.p1) && Objects.equals(p2, segment.p2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(p1, p2);
    }

    @Override
    public String toString() {
        return "Segment{" +
                "a=" + p1 +
                ", b=" + p2 +
                '}';
    }
}