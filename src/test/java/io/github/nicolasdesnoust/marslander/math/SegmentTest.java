package io.github.nicolasdesnoust.marslander.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SegmentTest {
	@Test
	void returnTrue_ifVerticalSegmentIntersectAnother() {
		Segment capsuleSegment = new Segment(new Point(5158.32, 908.59), new Point(5158.32, 943.28));
		Segment surfaceSegment = new Segment(new Point(5100, 1000), new Point(5500, 500));
		boolean result = capsuleSegment.doesIntersect(surfaceSegment);
		Assertions.assertTrue(result);
	}
}
