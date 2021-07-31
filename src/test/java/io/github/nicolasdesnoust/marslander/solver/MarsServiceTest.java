package io.github.nicolasdesnoust.marslander.solver;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.nicolasdesnoust.marslander.core.Capsule;
import io.github.nicolasdesnoust.marslander.core.InitialGameState;
import io.github.nicolasdesnoust.marslander.core.Mars;
import io.github.nicolasdesnoust.marslander.math.Point;
import io.github.nicolasdesnoust.marslander.math.Segment;

class MarsServiceTest {
	private MarsService marsService = new MarsService();
	
	@Test
	void returnTrue_ifVerticalSegmentIntersectAnother() {
		List<Segment> marsSurface = new ArrayList<>();
		marsSurface.add(new Segment(new Point(0, 1800), new Point(300, 1200)));
		marsSurface.add(new Segment(new Point(300, 1200), new Point(1000, 1550)));
		marsSurface.add(new Segment(new Point(1000, 1550), new Point(2000, 1200)));
		marsSurface.add(new Segment(new Point(2000, 1200), new Point(2500, 1650)));
		marsSurface.add(new Segment(new Point(2500, 1650), new Point(3700, 220)));
		Segment landingArea = new Segment(new Point(3700, 220), new Point(4700, 220));
		marsSurface.add(landingArea);
		marsSurface.add(new Segment(new Point(4700, 220), new Point(4750, 1000)));
		marsSurface.add(new Segment(new Point(4750, 1000), new Point(4700, 1650)));
		marsSurface.add(new Segment(new Point(4700, 1650), new Point(4000, 1700)));
		marsSurface.add(new Segment(new Point(4000, 1700), new Point(3700, 1600)));
		marsSurface.add(new Segment(new Point(3700, 1600), new Point(3750, 1900)));
		marsSurface.add(new Segment(new Point(3750, 1900), new Point(4000, 2100)));
		marsSurface.add(new Segment(new Point(4000, 2100), new Point(4900, 2050)));
		marsSurface.add(new Segment(new Point(4900, 2050), new Point(5100, 1000)));
		marsSurface.add(new Segment(new Point(5100, 1000), new Point(5500, 500)));
		marsSurface.add(new Segment(new Point(5500, 500), new Point(6200, 800)));
		marsSurface.add(new Segment(new Point(6200, 800), new Point(6999, 600)));
			
		double height = marsService.findHeightWhereThereIsNoObstacleToLand(marsSurface, landingArea);
		
		Assertions.assertEquals(1600, Math.round(height));
	}
}
