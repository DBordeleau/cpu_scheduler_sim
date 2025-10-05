// Interface implemented by all scheduling algorithms

package com.cpusim.scheduling;

import java.util.*;

import com.cpusim.model.Process;
import com.cpusim.model.SimulationResult;
import com.cpusim.model.TimelineEvent.EventType;

public interface Scheduler {
    SimulationResult schedule(List<Process> processes);

    default String getName() {
        return this.getClass().getSimpleName();
    }

    default boolean isPreemptive() {
        return false;
    }

    default void setQuantum(int quantum) {
        // Only used by RRScheduler
    }

    default int getEventPriority(EventType type) {
        return switch (type) {
            case PROCESS_ARRIVAL -> 0;
            case PROCESS_FINISH -> 1;
            case CONTEXT_SWITCH -> 2;
            case PROCESS_START -> 3;
            case CPU_IDLE -> 4;
        };
    }
}
