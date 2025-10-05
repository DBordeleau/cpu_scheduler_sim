// Output of simulating one scheduling algorithm

package com.cpusim.model;

import java.util.List;
import java.util.Map;

public class SimulationResult {
    private List<TimelineEvent> timeline;
    private double averageWaitingTime;
    private double averageTurnaroundTime;
    private int totalContextSwitches;
    private Map<Integer, Integer> completionTimes; // pid -> completion time
    private Map<Integer, Integer> waitingTimes; // pid -> waiting time
    private Map<Integer, Integer> turnaroundTimes; // pid -> turnaround time

    public SimulationResult(
            List<TimelineEvent> timeline,
            double averageWaitingTime,
            double averageTurnaroundTime,
            int totalContextSwitches,
            Map<Integer, Integer> completionTimes,
            Map<Integer, Integer> waitingTimes,
            Map<Integer, Integer> turnaroundTimes) {
        this.timeline = timeline;
        this.averageWaitingTime = averageWaitingTime;
        this.averageTurnaroundTime = averageTurnaroundTime;
        this.totalContextSwitches = totalContextSwitches;
        this.completionTimes = completionTimes;
        this.waitingTimes = waitingTimes;
        this.turnaroundTimes = turnaroundTimes;
    }

    public List<TimelineEvent> getTimeline() {
        return timeline;
    }

    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public double getAverageTurnaroundTime() {
        return averageTurnaroundTime;
    }

    public int getTotalContextSwitches() {
        return totalContextSwitches;
    }

    public Map<Integer, Integer> getCompletionTimes() {
        return completionTimes;
    }

    public Map<Integer, Integer> getWaitingTimes() {
        return waitingTimes;
    }

    public Map<Integer, Integer> getTurnaroundTimes() {
        return turnaroundTimes;
    }

    @Override
    public String toString() {
        return "SimulationResult{" +
                "averageWaitingTime=" + averageWaitingTime +
                ", totalContextSwitches=" + totalContextSwitches +
                ", timelineSize=" + (timeline != null ? timeline.size() : 0) +
                '}';
    }
}
