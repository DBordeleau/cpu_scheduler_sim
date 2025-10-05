/*
    Handles logic for our Spring Boot REST API endpoints.
 */

package com.cpusim.service;

import com.cpusim.model.Process;
import com.cpusim.model.SimulationResult;
import com.cpusim.scheduling.*;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SimulationService {

    private final List<Process> processes = new ArrayList<>();
    private SimulationResult lastResult;

    public void addProcess(Process process) {
        processes.add(process);
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public void clearProcesses() {
        processes.clear();
    }

    public SimulationResult runSimulation(String algorithm, int quantum) {
        Scheduler scheduler = switch (algorithm.toLowerCase()) {
            case "fcfs" -> new FCFSScheduler();
            case "sjf" -> new SJFScheduler();
            case "srtf" -> new SRTFScheduler();
            case "pp", "priority" -> new PPScheduler();
            case "rr", "roundrobin" -> new RRScheduler(quantum);
            default -> throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        };

        lastResult = scheduler.schedule(new ArrayList<>(processes));
        return lastResult;
    }

    public SimulationResult getLastResult() {
        if (lastResult == null) {
            throw new IllegalStateException("No simulation has been run yet.");
        }
        return lastResult;
    }
}
