package io.github.nicolasdesnoust.marslander.simulator;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.nicolasdesnoust.marslander.SolverConfiguration;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/simulations")
@RequiredArgsConstructor
public class SimulatorControllerREST {

	private final SimulatorService simulationService;

    @Async
    @PostMapping
    public ResponseEntity<Void> runSimulation(@RequestBody SolverConfiguration configuration)
            throws IOException, InterruptedException {
    	
        simulationService.runSimulation(configuration);
        return ResponseEntity.noContent().build();
    }
}