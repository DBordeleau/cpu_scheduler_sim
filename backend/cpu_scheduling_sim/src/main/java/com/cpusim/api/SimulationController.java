/*
    Spring Boot REST API controller that connects frontend with backend simulation logic.
    Provides endpoints to add processes, run simulations, and retrieve results.
    All the actual logic is handled in SimulationService.java
 */

package com.cpusim.api;

import com.cpusim.model.Process;
import com.cpusim.model.SimulationResult;
import com.cpusim.service.SimulationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simulation")
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    // Endpoint to add a process. Accepts an array of processes to add. PID starts
    // at 1 and auto increments.
    @PostMapping("/processes")
    public ResponseEntity<String> addProcesses(@RequestBody int[][] processData) {
        simulationService.clearProcesses(); // Clear existing processes
        for (int i = 0; i < processData.length; i++) {
            int[] data = processData[i];
            Process process = new Process(
                    i + 1, // pid (1-indexed)
                    data[0], // burstTime
                    data[1], // priority
                    data[2] // arrivalTime
            );
            simulationService.addProcess(process);
        }
        return ResponseEntity.ok("Processes added successfully.");
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    // Endpoint to return all added processes
    @GetMapping("/processes")
    public ResponseEntity<?> getProcesses() {
        return ResponseEntity.ok(simulationService.getProcesses());
    }

    // Endpoint to run the simulation with specified algorithm and quantum (if
    // applicable)
    @PostMapping("/simulate")
    public ResponseEntity<SimulationResult> runSimulation(
            @RequestParam("algorithm") String algorithm,
            @RequestParam(value = "quantum", required = false, defaultValue = "2") int quantum) {
        SimulationResult result = simulationService.runSimulation(algorithm, quantum);
        return ResponseEntity.ok(result);
    }

    // Endpoint to get the last simulation result
    @GetMapping("/result")
    public ResponseEntity<SimulationResult> getLastResult() {
        SimulationResult result = simulationService.getLastResult();
        return ResponseEntity.ok(result);
    }
}
