package io.github.nicolasdesnoust.marslander.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.nicolasdesnoust.marslander.math.Segment;

public class SegmentChunks {
	private final Chunk[][] chunks;
	public final Map<Key, Segment[]> cache;
	private boolean initialized = false;

	public SegmentChunks() {
		this.chunks = new Chunk[7][3];
		for (int x = 0; x <= 6; x++) {
			for (int y = 0; y <= 2; y++) {
				chunks[x][y] = new Chunk();
			}
		}
		this.cache = new HashMap<>(50);
	}

	public void splitsIntoChunksVertically(List<Segment> segments) {
		initialized = true;
		cache.clear();
		// chunks.forEach(List::clear);
		segments.forEach(Segment::preComputeHashCode);

		for (int x = 0; x <= 6; x++) {
			Integer fromXCoordinate = x * 1000;
			Integer toXCoordinate = fromXCoordinate + 1000;
			for (int y = 0; y <= 2; y++) {
				Integer fromYCoordinate = y * 1000;
				Integer toYCoordinate = fromYCoordinate + 1000;
				for (Segment segment : segments) {
					if (isSegmentInChunk(segment, fromXCoordinate, toXCoordinate, fromYCoordinate, toYCoordinate)) {
						chunks[x][y].segments.add(segment);
					}
				}
			}
		}
	}

	private boolean isSegmentInChunk(
			Segment segment, int clusterX1, int clusterX2, int clusterY1, int clusterY2) {

		double minX = Math.min(segment.getP1().getX(), segment.getP2().getX());
		double maxX = Math.max(segment.getP1().getX(), segment.getP2().getX());
		double minY = Math.min(segment.getP1().getY(), segment.getP2().getY());
		double maxY = Math.max(segment.getP1().getY(), segment.getP2().getY());

		boolean isInsideAlongX = (minX <= clusterX1 && maxX > clusterX1)
				|| (minX >= clusterX1 && maxX <= clusterX2)
				|| (minX < clusterX2 && maxX >= clusterX2);
		boolean isInsideAlongY = (minY <= clusterY1 && maxY > clusterY1)
				|| (minY >= clusterY1 && maxY <= clusterY2)
				|| (minY < clusterY2 && maxY >= clusterY2);

		return isInsideAlongX && isInsideAlongY;
	}

	public Segment[] getSegmentsBetween(int fromXCoordinate, int toXCoordinate, int fromYCoordinate, int toYCoordinate) {
		int fromXIndex = fromXCoordinate / 1000;
		int toXIndex = toXCoordinate / 1000;
		int fromYIndex = fromYCoordinate / 1000;
		int toYIndex = toYCoordinate / 1000;

		if (fromXIndex > 0 && fromXCoordinate == fromXIndex * 1000) {
			fromXIndex--;
		}
		if (toXIndex < 7 && toXCoordinate == toXIndex * 1000) {
			toXIndex++;
		}
		//fromXIndex = Math.min(fromXIndex, 0);
		toXIndex = Math.min(toXIndex, 6);
		
		if (fromYIndex > 0 && fromYCoordinate == fromYIndex * 1000) {
			fromYIndex--;
		}
		if (toYIndex < 3 && toYCoordinate == toYIndex * 1000) {
			toYIndex++;
		}
		//fromYIndex = Math.min(fromYIndex, 0);
		toYIndex = Math.min(toYIndex, 2);


		Key cacheKey = Key.of(fromXIndex, toXIndex, fromYIndex, toYIndex);
		if (cache.containsKey(cacheKey)) {
			return cache.get(cacheKey);
		}

		Set<Segment> segments = new HashSet<>();

		int currentXIndex = fromXIndex;
		while (currentXIndex <= toXIndex) {	
			int currentYIndex = fromYIndex;
			while (currentYIndex <= toYIndex) {
				List<Segment> list = chunks[currentXIndex][currentYIndex].segments;
				segments.addAll(list);
				currentYIndex++;
			}
			
			currentXIndex++;
		}
		
		Segment[] result = segments.toArray(new Segment[segments.size()]);
		cache.put(Key.of(cacheKey), result);
		return result;
	}

	public boolean hasBeenInitialized() {
		return initialized;
	}
	
	private static class Chunk {
		final List<Segment> segments = new ArrayList<>();
	}

	static class Key {

		private static final Key cacheKey = new Key();

		int x1;
		int x2;
		int y1;
		int y2;

		private int preCalculatedHashCode;

		private Key() {
		}

		private Key(Key key) {
			this.x1 = key.x1;
			this.x2 = key.x2;
			this.y1 = key.y1;
			this.y2 = key.y2;
			this.preCalculatedHashCode = key.preCalculatedHashCode;
		}

		public static Key of(int x1, int x2, int y1, int y2) {
			cacheKey.x1 = x1;
			cacheKey.x2 = x2;
			cacheKey.y1 = y1;
			cacheKey.y2 = y2;
			cacheKey.preCalculatedHashCode = precalculateHashCode(cacheKey);
			return cacheKey;
		}

		public static Key of(Key key) {
			return new Key(key);
		}

		public static int precalculateHashCode(Key key) {
			final int prime = 31;
			int result = 1;
			result = prime * result + key.x1;
			result = prime * result + key.x2;
			result = prime * result + key.y1;
			result = prime * result + key.y2;
			return result;
		}

		@Override
		public int hashCode() {
			return preCalculatedHashCode;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			Key other = (Key) obj;
			if (x1 != other.x1)
				return false;
			if (x2 != other.x2)
				return false;
			if (y1 != other.y1)
				return false;
			if (y2 != other.y2)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Key [x1=" + x1 + ", x2=" + x2 + ", y1=" + y1 + ", y2=" + y2 + "]";
		}

	}

}
