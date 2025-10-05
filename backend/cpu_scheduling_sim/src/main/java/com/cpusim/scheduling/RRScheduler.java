/*
    Round Robin (RR) Scheduling Algorithm Implementation.
    Preemptive scheduling where each process gets to run for q time units.
    Processes are executed in a circular FIFO manner.
*/

package com.cpusim.scheduling;

import java.util.*;

import com.cpusim.model.Process;
import com.cpusim.model.SimulationResult;
import com.cpusim.model.TimelineEvent;
import com.cpusim.model.TimelineEvent.EventType;

public class RRScheduler implements Scheduler {

    private int quantum;

    public RRScheduler() {
        this.quantum = 2; // Default quantum
    }

    public RRScheduler(int quantum) {
        this.quantum = quantum;
    }

    @Override
    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

    @Override
    public SimulationResult schedule(List<Process> processes) {
        List<Process> processList = new ArrayList<>(processes);
        processList.sort(Comparator.comparingInt(Process::getArrivalTime));

        List<TimelineEvent> timeline = new ArrayList<>();
        Map<Integer, Integer> completionTimes = new HashMap<>();
        Map<Integer, Integer> waitingTimes = new HashMap<>();
        Map<Integer, Integer> turnaroundTimes = new HashMap<>();
        Set<Integer> startedProcesses = new HashSet<>();

        // Log all process arrivals
        for (Process p : processList) {
            timeline.add(new TimelineEvent(p.getArrivalTime(), p.getPid(), EventType.PROCESS_ARRIVAL, p.getBurstTime(),
                    p.getPriority()));
        }

        int currentTime = 0;
        int contextSwitches = 0;
        int completed = 0;

        // Track remaining burst time for each process
        Map<Process, Integer> remainingBurstTimes = new HashMap<>();
        for (Process p : processList) {
            remainingBurstTimes.put(p, p.getBurstTime());
        }

        // Use a queue to maintain FIFO order
        Queue<Process> readyQueue = new LinkedList<>();
        // Track processes that are already in the queue, use a set to avoid duplicates
        Set<Process> inQueue = new HashSet<>();
        int processIndex = 0; // Used to track which processes have been added to the queue

        Process currentProcess = null;
        boolean wasIdle = false;

        while (completed < processList.size()) {
            while (processIndex < processList.size() &&
                    processList.get(processIndex).getArrivalTime() <= currentTime) {
                Process p = processList.get(processIndex);
                if (!inQueue.contains(p) && remainingBurstTimes.get(p) > 0) {
                    readyQueue.add(p);
                    inQueue.add(p);
                }
                processIndex++;
            }

            // If no process is ready, CPU is idle
            if (readyQueue.isEmpty()) {
                // Only log idle event when CPU first becomes idle
                if (!wasIdle) {
                    timeline.add(new TimelineEvent(currentTime, 0, EventType.CPU_IDLE));
                    wasIdle = true;
                }
                currentTime++;
                continue;
            }

            // Get next process from ready queue
            Process nextProcess = readyQueue.poll();
            inQueue.remove(nextProcess);

            // Check if we need to switch processes
            if (currentProcess == null || currentProcess.getPid() != nextProcess.getPid()) {
                // Context switch only when switching between different processes (not from
                // idle)
                if (currentProcess != null && !wasIdle) {
                    timeline.add(new TimelineEvent(currentTime, nextProcess.getPid(), EventType.CONTEXT_SWITCH,
                            remainingBurstTimes.get(nextProcess), nextProcess.getPriority()));
                    contextSwitches++;
                }

                currentProcess = nextProcess;

                // Only log start event if this process hasn't started before
                if (!startedProcesses.contains(currentProcess.getPid())) {
                    timeline.add(new TimelineEvent(currentTime, currentProcess.getPid(), EventType.PROCESS_START,
                            remainingBurstTimes.get(currentProcess), currentProcess.getPriority()));
                    startedProcesses.add(currentProcess.getPid());
                }

                wasIdle = false;
            }

            // Execute for quantum time or until process completes
            int timeToExecute = Math.min(quantum, remainingBurstTimes.get(currentProcess));

            for (int i = 0; i < timeToExecute; i++) {
                remainingBurstTimes.put(currentProcess, remainingBurstTimes.get(currentProcess) - 1);
                currentTime++;

                // Add newly arrived processes during execution
                while (processIndex < processList.size() &&
                        processList.get(processIndex).getArrivalTime() <= currentTime) {
                    Process p = processList.get(processIndex);
                    if (!inQueue.contains(p) && remainingBurstTimes.get(p) > 0 &&
                            p.getPid() != currentProcess.getPid()) {
                        readyQueue.add(p);
                        inQueue.add(p);
                    }
                    processIndex++;
                }
            }

            // Check if process finished and add back to ready queue if not
            if (remainingBurstTimes.get(currentProcess) == 0) {
                timeline.add(new TimelineEvent(currentTime, currentProcess.getPid(), EventType.PROCESS_FINISH));
                completionTimes.put(currentProcess.getPid(), currentTime);
                completed++;
            } else {
                readyQueue.add(currentProcess);
                inQueue.add(currentProcess);
            }
        }

        // Sort timeline by time, then by event priority
        timeline.sort(Comparator.comparingInt(TimelineEvent::getTime)
                .thenComparingInt(e -> getEventPriority(e.getType())));

        // Calculate metrics for simulation results
        for (Process p : processList) {
            int turnaroundTime = completionTimes.get(p.getPid()) - p.getArrivalTime();
            int waitingTime = turnaroundTime - p.getBurstTime();
            waitingTimes.put(p.getPid(), waitingTime);
            turnaroundTimes.put(p.getPid(), turnaroundTime);
        }

        double totalWaitingTime = waitingTimes.values().stream().mapToInt(Integer::intValue).sum();
        double averageWaitingTime = totalWaitingTime / waitingTimes.size();

        double totalTurnaroundTime = turnaroundTimes.values().stream().mapToInt(Integer::intValue).sum();
        double averageTurnaroundTime = totalTurnaroundTime / turnaroundTimes.size();

        return new SimulationResult(timeline, averageWaitingTime, averageTurnaroundTime, contextSwitches,
                completionTimes, waitingTimes, turnaroundTimes);
    }

    @Override
    public String getName() {
        return "Round Robin (RR) - Quantum: " + quantum;
    }

    @Override
    public boolean isPreemptive() {
        return true;
    }
}