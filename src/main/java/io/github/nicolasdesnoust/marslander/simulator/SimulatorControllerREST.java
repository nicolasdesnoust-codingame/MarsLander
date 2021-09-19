package io.github.nicolasdesnoust.marslander.simulator;

import java.io.IOException;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.nicolasdesnoust.marslander.SolverConfiguration;
import io.github.nicolasdesnoust.marslander.logs.LogRecord;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/simulations")
@RequiredArgsConstructor
public class SimulatorControllerREST {

	private final SimulatorService simulationService;
	private final ElasticsearchOperations elasticsearchTemplate;

    @Async
    @PostMapping
    public ResponseEntity<Void> runSimulation(@RequestBody SolverConfiguration configuration)
            throws IOException, InterruptedException {
    	IndexOperations operations = elasticsearchTemplate.indexOps(LogRecord.class);
		operations.delete();
		operations.create();

		simulationService.runSimulation(configuration);
        return ResponseEntity.noContent().build();
    }
}