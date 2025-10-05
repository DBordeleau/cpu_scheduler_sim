/*
    Shortest Remaining Time First (SRTF) Scheduling Algorithm Implementation.
    Preemptive version of SJF where we select the process with the shortest remaining burst time.
    If a new process arrives with a shorter remaining time than the current process, a context switch occurs.
*/

package com.cpusim.scheduling;

import java.util.*;

import com.cpusim.model.Process;
import com.cpusim.model.SimulationResult;
import com.cpusim.model.TimelineEvent;
import com.cpusim.model.TimelineEvent.EventType;

public class SRTFScheduler implements Scheduler {

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

        Map<Process, Integer> remainingBurstTimes = new HashMap<>();
        for (Process p : processList) {
            remainingBurstTimes.put(p, p.getBurstTime());
        }

        Process currentProcess = null;
        boolean wasIdle = false;

        while (completed < processList.size()) {
            List<Process> readyProcesses = new ArrayList<>();
            for (Process p : processList) {
                if (p.getArrivalTime() <= currentTime && remainingBurstTimes.get(p) > 0) {
                    readyProcesses.add(p);
                }
            }

            // CPU is idle if no processes are ready
            if (readyProcesses.isEmpty()) {
                // Only log idle event when CPU first becomes idle
                if (!wasIdle) {
                    timeline.add(new TimelineEvent(currentTime, 0, EventType.CPU_IDLE));
                    wasIdle = true;
                }
                currentTime++;
                continue;
            }

            // Select process with shortest remaining time
            // Use arrival time as tiebreaker
            Comparator<Process> byRemainingTime = Comparator.comparingInt(p -> remainingBurstTimes.get(p));
            Comparator<Process> byArrivalTime = Comparator.comparingInt(Process::getArrivalTime);
            readyProcesses.sort(byRemainingTime.thenComparing(byArrivalTime));

            Process nextProcess = readyProcesses.get(0);

            // Check if we need to switch processes
            if (currentProcess == null || currentProcess.getPid() != nextProcess.getPid()) {
                // Context switch only when switching between different processes (not from
                // idle)
                if (currentProcess != null && currentProcess.getPid() != nextProcess.getPid() && !wasIdle) {
                    timeline.add(new TimelineEvent(currentTime, nextProcess.getPid(), EventType.CONTEXT_SWITCH,
                            remainingBurstTimes.get(nextProcess), nextProcess.getPriority()));
                    contextSwitches++;
                }

                currentProcess = nextProcess;

                // Log start event if this process has not started before
                if (!startedProcesses.contains(currentProcess.getPid())) {
                    timeline.add(new TimelineEvent(currentTime, currentProcess.getPid(), EventType.PROCESS_START,
                            remainingBurstTimes.get(currentProcess), currentProcess.getPriority()));
                    startedProcesses.add(currentProcess.getPid());
                }

                wasIdle = false;
            }

            // Execute for one time unit
            remainingBurstTimes.put(currentProcess, remainingBurstTimes.get(currentProcess) - 1);
            currentTime++;

            // Check if process finished
            if (remainingBurstTimes.get(currentProcess) == 0) {
                timeline.add(new TimelineEvent(currentTime, currentProcess.getPid(), EventType.PROCESS_FINISH));
                completionTimes.put(currentProcess.getPid(), currentTime);
                completed++;
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
        return "Shortest Remaining Time First (SRTF)";
    }

    @Override
    public boolean isPreemptive() {
        return true;
    }
}