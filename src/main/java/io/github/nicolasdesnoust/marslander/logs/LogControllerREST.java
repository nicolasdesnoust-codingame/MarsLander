package io.github.nicolasdesnoust.marslander.logs;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class LogControllerREST {

	private final ElasticsearchOperations elasticsearchTemplate;
	private final LogRepository logRepository;

	@GetMapping("/surface")
	public ResponseEntity<List<LogRecord>> getMarsSurface() {
		List<LogRecord> marsSurface = logRepository.findByTypeOrderByIndexAsc("surface");
		return ResponseEntity.ok(marsSurface);
	}

//	@GetMapping("/path")
//	public ResponseEntity<List<LogRecord>> getPath() {
//		List<LogRecord> path = logRepository.findByTypeOrderByIndexAsc("path");
//		return ResponseEntity.ok(path);
//	}
//
//	@GetMapping("/path/points")
//	public ResponseEntity<List<LogRecord>> getPathPoints() {
//		List<LogRecord> pathPoints = logRepository.findByTypeOrderByIndexAsc("point");
//		return ResponseEntity.ok(pathPoints);
//	}

	@PostMapping("/reset")
	public ResponseEntity<Void> resetLogRepository() {
		IndexOperations operations = elasticsearchTemplate.indexOps(LogRecord.class);
		operations.delete();
		operations.create();
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/generations/{generation}")
	public ResponseEntity<List<LoggableIndividual>> getGeneration(@PathVariable int generation) {
		List<LogRecord> logRecords = logRepository.findByGenerationAndType(generation, "individual");
		
		List<LoggableIndividual> population = logRecords.stream().map(LogRecord::getIndividual)
				.sorted(Comparator.comparingDouble(LoggableIndividual::getEvaluation).reversed())
				.collect(Collectors.toList());
		population.forEach(individual -> individual.getCapsules()
				.sort(Comparator.comparingInt(LoggableCapsule::getIndex)));
		
		return ResponseEntity.ok(population);
	}

	@GetMapping("/generations/_count")
	public ResponseEntity<Long> getGenerationCount() {
		return ResponseEntity.ok(logRepository.countDistinctGeneration());
	}

}