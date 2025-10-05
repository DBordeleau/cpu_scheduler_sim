/*
    Core engine for CPU scheduling simulations. Manages processes, runs simulations and formats results.
    The SimulationEngine sits between the web layer and the scheduling algorithms.
*/

package com.cpusim.simulation;

import java.util.*;

import com.cpusim.model.Process;
import com.cpusim.model.SimulationResult;
import com.cpusim.model.TimelineEvent;
import com.cpusim.scheduling.*;

public class SimulationEngine {

    private List<Process> processes;
    private Map<String, Scheduler> schedulers;
    private SimulationResult lastResult;

    public SimulationEngine() {
        this.processes = new ArrayList<>();
        this.schedulers = new HashMap<>();
        initializeSchedulers();
    }

    // Add available scheduling algorithms here
    private void initializeSchedulers() {
        schedulers.put("FCFS", new FCFSScheduler());
        schedulers.put("SJF", new SJFScheduler());
        schedulers.put("SRTF", new SRTFScheduler());
        schedulers.put("PP", new PPScheduler());
        schedulers.put("RR", new RRScheduler());
    }

    /**
     * Add a process to the simulation
     * 
     * @param pid         Process ID, unique identifier automatically increments
     * @param arrivalTime Arrival time
     * @param burstTime   Burst time (time units needed to complete)
     * @param priority    Priority (lower number = higher priority)
     * @return The Process object
     */
    public Process addProcess(int pid, int arrivalTime, int burstTime, int priority) {
        if (arrivalTime < 0 || burstTime <= 0) {
            throw new IllegalArgumentException("Invalid process parameters");
        }

        // Check for duplicate PID, this shouldn't happen
        for (Process p : processes) {
            if (p.getPid() == pid) {
                throw new IllegalArgumentException("Process with PID " + pid + " already exists");
            }
        }

        Process process = new Process(pid, arrivalTime, burstTime, priority);
        processes.add(process);
        return process;
    }

    /**
     * Add a process without priority (default to 0)
     */
    public Process addProcess(int pid, int arrivalTime, int burstTime) {
        return addProcess(pid, arrivalTime, burstTime, 0);
    }

    public List<Process> getProcesses() {
        return new ArrayList<>(processes);
    }

    /**
     * Returns a process matching the passed PID
     */
    public Process getProcess(int pid) {
        for (Process p : processes) {
            if (p.getPid() == pid) {
                return p;
            }
        }
        return null;
    }

    /**
     * Removes a process matching the passed PID
     */
    public boolean removeProcess(int pid) {
        return processes.removeIf(p -> p.getPid() == pid);
    }

    public void clearProcesses() {
        processes.clear();
        lastResult = null;
    }

    public List<String> getAvailableAlgorithms() {
        return new ArrayList<>(schedulers.keySet());
    }

    /**
     * Run a simulation with the specified algorithm and passed quantum
     * 
     * @param algorithmName Algorithm identifier (FCFS, SJF, SRTF, PP, RR)
     * @param quantum       Time quantum (only used for RR, ignored for others).
     * @return SimulationResult object containing all metrics and timeline
     */
    public SimulationResult runSimulation(String algorithmName, int quantum) {
        if (processes.isEmpty()) {
            throw new IllegalStateException("No processes to simulate");
        }

        Scheduler scheduler = schedulers.get(algorithmName.toUpperCase());
        if (scheduler == null) {
            throw new IllegalArgumentException("Unknown algorithm: " + algorithmName);
        }

        // Set quantum for Round Robin
        if (algorithmName.equalsIgnoreCase("RR")) {
            scheduler.setQuantum(quantum);
        }

        List<Process> processCopy = new ArrayList<>();
        for (Process p : processes) {
            processCopy.add(new Process(p.getPid(), p.getArrivalTime(),
                    p.getBurstTime(), p.getPriority()));
        }

        lastResult = scheduler.schedule(processCopy);
        return lastResult;
    }

    /**
     * Run a simulation with default quantum of 2
     * 
     * @param algorithmName Algorithm identifier (FCFS, SJF, SRTF, PP, RR)
     * @param quantum       q = 2
     * @return SimulationResult object containing all metrics and timeline
     */
    public SimulationResult runSimulation(String algorithmName) {
        return runSimulation(algorithmName, 2); // Default quantum of 2
    }

    /**
     * Returns a SimulationResult object from the last simulation run
     */
    public SimulationResult getLastResult() {
        return lastResult;
    }

    /**
     * Format the timeline as a human-readable string
     * 
     * @param result SimulationResult to format
     * @return Formatted timeline string
     */
    public String formatTimeline(SimulationResult result) {
        if (result == null) {
            return "No simulation results available";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== Simulation Timeline ===\n");

        List<TimelineEvent> timeline = result.getTimeline();
        for (TimelineEvent event : timeline) {
            sb.append(formatTimelineEvent(event));
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Format a single timeline event
     * Returns a string like:
     * t0: P1 Starts
     * t5: P1 Finishes
     * t5: Context switch to P2
     */
    private String formatTimelineEvent(TimelineEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("t").append(event.getTime()).append(": ");

        switch (event.getType()) {
            case PROCESS_START:
                sb.append("P").append(event.getPid()).append(" Starts");
                break;
            case PROCESS_FINISH:
                sb.append("P").append(event.getPid()).append(" Finishes");
                break;
            case CONTEXT_SWITCH:
                sb.append("Context switch to P").append(event.getPid());
                break;
            case CPU_IDLE:
                sb.append("CPU Idle");
                break;
        }

        return sb.toString();
    }

    /**
     * Format complete simulation results including metrics
     */
    public String formatResults(SimulationResult result) {
        if (result == null) {
            return "No simulation results available";
        }

        StringBuilder sb = new StringBuilder();

        // Timeline
        sb.append(formatTimeline(result));
        sb.append("\n");

        // Metrics
        sb.append("=== Performance Metrics ===\n");
        sb.append("Average Waiting Time: %.2f\n".formatted(result.getAverageWaitingTime()));
        sb.append("Average Turnaround Time: %.2f\n".formatted(result.getAverageTurnaroundTime()));
        sb.append("Context Switches: %d\n".formatted(result.getTotalContextSwitches()));
        sb.append("\n");

        // Per-process metrics
        sb.append("=== Per-Process Metrics ===\n");
        Map<Integer, Integer> completionTimes = result.getCompletionTimes();
        Map<Integer, Integer> waitingTimes = result.getWaitingTimes();
        Map<Integer, Integer> turnaroundTimes = result.getTurnaroundTimes();

        List<Integer> pids = new ArrayList<>(completionTimes.keySet());
        Collections.sort(pids);

        for (int pid : pids) {
            sb.append("P%d: Completion=%d, Waiting=%d, Turnaround=%d\n".formatted(
                    pid,
                    completionTimes.get(pid),
                    waitingTimes.get(pid),
                    turnaroundTimes.get(pid)));
        }

        return sb.toString();
    }

    /**
     * Load sample processes for testing
     */
    public void loadSampleProcesses() {
        clearProcesses();
        addProcess(1, 0, 8, 2);
        addProcess(2, 1, 4, 1);
        addProcess(3, 2, 9, 3);
        addProcess(4, 3, 5, 2);
    }

    /**
     * Validate process configuration before simulation
     */
    public boolean validateProcesses() {
        if (processes.isEmpty()) {
            return false;
        }

        for (Process p : processes) {
            if (p.getArrivalTime() < 0 || p.getBurstTime() <= 0) {
                return false;
            }
        }

        return true;
    }
}