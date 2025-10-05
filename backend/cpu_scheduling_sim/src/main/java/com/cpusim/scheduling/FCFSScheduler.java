/*
    First Come, First-Served (FCFS) Scheduling Algorithm Implementation.
    Non-preemptive scheduling where we execute processes in the order they arrive.
 */

package com.cpusim.scheduling;

import java.util.*;

import com.cpusim.model.Process;
import com.cpusim.model.SimulationResult;
import com.cpusim.model.TimelineEvent;
import com.cpusim.model.TimelineEvent.EventType;

public class FCFSScheduler implements Scheduler {

    @Override
    public SimulationResult schedule(List<Process> processes) {
        List<Process> processList = new ArrayList<>(processes);

        processList.sort(Comparator.comparingInt(Process::getArrivalTime));

        List<TimelineEvent> timeline = new ArrayList<>();
        Map<Integer, Integer> completionTimes = new HashMap<>();
        Map<Integer, Integer> waitingTimes = new HashMap<>();
        Map<Integer, Integer> turnaroundTimes = new HashMap<>();

        // Log all process arrivals
        for (Process p : processList) {
            timeline.add(new TimelineEvent(p.getArrivalTime(), p.getPid(), EventType.PROCESS_ARRIVAL, p.getBurstTime(),
                    p.getPriority()));
        }

        int currentTime = 0;
        int contextSwitches = 0;
        boolean wasIdle = false;

        // Process every process in order of arrival
        for (int i = 0; i < processList.size(); i++) {
            Process p = processList.get(i);

            if (currentTime < p.getArrivalTime()) {
                timeline.add(new TimelineEvent(currentTime, 0, EventType.CPU_IDLE));
                currentTime = p.getArrivalTime();
                wasIdle = true;
            }

            // Only context switch if transitioning from another process (not from idle or
            // start)
            if (i > 0 && !wasIdle) {
                timeline.add(new TimelineEvent(currentTime, p.getPid(), EventType.CONTEXT_SWITCH, p.getBurstTime(),
                        p.getPriority()));
                contextSwitches++;
            }

            wasIdle = false;

            timeline.add(new TimelineEvent(currentTime, p.getPid(), EventType.PROCESS_START, p.getBurstTime(),
                    p.getPriority()));
            int startTime = currentTime;
            currentTime += p.getBurstTime(); // Run to completion in FCFS
            timeline.add(new TimelineEvent(currentTime, p.getPid(), EventType.PROCESS_FINISH));

            completionTimes.put(p.getPid(), currentTime);

            int waitingTime = startTime - p.getArrivalTime();
            waitingTimes.put(p.getPid(), waitingTime);

            int turnaroundTime = currentTime - p.getArrivalTime();
            turnaroundTimes.put(p.getPid(), turnaroundTime);
        }

        // Sort timeline by time, then by event priority
        timeline.sort(Comparator.comparingInt(TimelineEvent::getTime)
                .thenComparingInt(e -> getEventPriority(e.getType())));

        // Calculate metrics for simulation results
        double totalWaitingTime = 0;
        for (int waitTime : waitingTimes.values()) {
            totalWaitingTime += waitTime;
        }

        double averageWaitingTime = totalWaitingTime / waitingTimes.size();

        double totalTurnaroundTime = 0;
        for (int tat : turnaroundTimes.values()) {
            totalTurnaroundTime += tat;
        }

        double averageTurnaroundTime = totalTurnaroundTime / turnaroundTimes.size();

        return new SimulationResult(timeline, averageWaitingTime, averageTurnaroundTime, contextSwitches,
                completionTimes, waitingTimes,
                turnaroundTimes);
    }

    @Override
    public String getName() {
        return "First-Come, First-Served (FCFS)";
    }
}
