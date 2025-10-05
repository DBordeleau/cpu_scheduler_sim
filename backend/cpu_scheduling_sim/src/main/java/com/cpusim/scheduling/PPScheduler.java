/*
    Preemptive Priority Scheduling Algorithm Implementation.
    In this algorithm the CPU is allocated to the process with the highest priority.
    If a new process arrives with a higher priority than the current process, a context switch occurs.
 */

package com.cpusim.scheduling;

import java.util.*;

import com.cpusim.model.Process;
import com.cpusim.model.SimulationResult;
import com.cpusim.model.TimelineEvent;
import com.cpusim.model.TimelineEvent.EventType;

public class PPScheduler implements Scheduler {

    @Override
    public SimulationResult schedule(List<Process> processes) {
        List<Process> processList = new ArrayList<>(processes);

        // Sort by arrival time first for tie breaking
        processList.sort(Comparator.comparingInt(Process::getArrivalTime));

        List<TimelineEvent> timeline = new ArrayList<>();
        Map<Integer, Integer> completionTimes = new HashMap<>();
        Map<Integer, Integer> turnaroundTimes = new HashMap<>();
        Map<Integer, Integer> waitingTimes = new HashMap<>();
        Set<Integer> startedProcesses = new HashSet<>();

        // Log all process arrivals
        for (Process p : processList) {
            timeline.add(new TimelineEvent(p.getArrivalTime(), p.getPid(), EventType.PROCESS_ARRIVAL, p.getBurstTime(),
                    p.getPriority()));
        }

        int currentTime = 0;
        int contextSwitches = 0;
        int completed = 0;

        // process -> remaining burst time
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

            // CPU idles when no processes are ready
            if (readyProcesses.isEmpty()) {
                // Only log idle event when CPU first becomes idle
                if (!wasIdle) {
                    timeline.add(new TimelineEvent(currentTime, 0, EventType.CPU_IDLE));
                    wasIdle = true;
                }
                currentTime++;
                continue;
            }

            // Select process with highest priority (lowest priority number)
            // arrival time is tiebreaker
            readyProcesses.sort(Comparator.comparingInt(Process::getPriority)
                    .thenComparingInt(Process::getArrivalTime));

            Process nextProcess = readyProcesses.get(0);

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

                // If this process has not started before, log a start event
                if (!startedProcesses.contains(currentProcess.getPid())) {
                    timeline.add(new TimelineEvent(currentTime, currentProcess.getPid(), EventType.PROCESS_START,
                            remainingBurstTimes.get(currentProcess), currentProcess.getPriority()));
                    startedProcesses.add(currentProcess.getPid());
                }

                wasIdle = false;
            }

            // Execute process for one time unit
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

        // Calculate simulation result metrics
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
        return "Preemptive Priority (PP)";
    }

    @Override
    public boolean isPreemptive() {
        return true;
    }
}