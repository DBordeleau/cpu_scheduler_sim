// Represents a single event that occurs in the timeline of CPU scheduling

package com.cpusim.model;

public class TimelineEvent {
    private int time;
    private int pid;
    private EventType type; // switch, start, finish, idle, arrival
    private Integer burstRemaining; // Optional: burst time remaining at this event
    private Integer priority; // Optional: priority of the process

    public enum EventType {
        CONTEXT_SWITCH, PROCESS_START, PROCESS_FINISH, CPU_IDLE, PROCESS_ARRIVAL
    }

    public TimelineEvent(int time, int pid, EventType type) {
        this.time = time;
        this.pid = pid;
        this.type = type;
        this.burstRemaining = null;
        this.priority = null;
    }

    public TimelineEvent(int time, int pid, EventType type, Integer burstRemaining, Integer priority) {
        this.time = time;
        this.pid = pid;
        this.type = type;
        this.burstRemaining = burstRemaining;
        this.priority = priority;
    }

    public int getTime() {
        return time;
    }

    public int getPid() {
        return pid;
    }

    public EventType getType() {
        return type;
    }

    public Integer getBurstRemaining() {
        return burstRemaining;
    }

    public Integer getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "Time %d: %s (PID %d)".formatted(time, type, pid);
    }
}