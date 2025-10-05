import com.cpusim.scheduling.*;
import com.cpusim.model.Process;
import com.cpusim.model.SimulationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PPSchedulerTest {

    private PPScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new PPScheduler();
    }

    @Test
    void testHigherPriorityPreemptsLower() {
        // P1: arrival=0, burst=4, priority=2
        // P2: arrival=1, burst=3, priority=1 (higher priority)
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 4, 2, 0));
        processes.add(new Process(2, 3, 1, 1));

        SimulationResult result = scheduler.schedule(processes);

        // P1 runs 0-1, preempted by higher priority P2
        // P2 runs 1-4, completes
        // P1 resumes 4-7, completes
        assertEquals(7, result.getCompletionTimes().get(1));
        assertEquals(4, result.getCompletionTimes().get(2));
        assertEquals(2, result.getTotalContextSwitches()); // P1->P2 at t=1, P2->P1 at t=4
    }

    @Test
    void testNoPriorityDifference() {
        // P1: arrival=0, burst=5, priority=1
        // P2: arrival=2, burst=3, priority=1
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 5, 1, 0));
        processes.add(new Process(2, 3, 1, 2));

        SimulationResult result = scheduler.schedule(processes);

        // Same priority, P1 runs to completion first (arrived first)
        assertEquals(5, result.getCompletionTimes().get(1));
        assertEquals(8, result.getCompletionTimes().get(2));
        assertEquals(1, result.getTotalContextSwitches()); // P1->P2 at t=5
    }

    @Test
    void testMultiplePriorityLevels() {
        // P1: arrival=0, burst=3, priority=3 (lowest)
        // P2: arrival=1, burst=2, priority=1 (highest)
        // P3: arrival=2, burst=4, priority=2 (middle)
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 3, 3, 0));
        processes.add(new Process(2, 2, 1, 1));
        processes.add(new Process(3, 4, 2, 2));

        SimulationResult result = scheduler.schedule(processes);

        // P1 runs 0-1, preempted
        // P2 runs 1-3, completes (highest priority)
        // P3 runs 3-7, completes (middle priority)
        // P1 resumes 7-9, completes
        assertEquals(9, result.getCompletionTimes().get(1));
        assertEquals(3, result.getCompletionTimes().get(2));
        assertEquals(7, result.getCompletionTimes().get(3));
        assertEquals(3, result.getTotalContextSwitches()); // P1->P2 at t=1, P2->P3 at t=3, P3->P1 at t=7
    }

    @Test
    void testAllSamePriority() {
        // P1: arrival=0, burst=2, priority=5
        // P2: arrival=0, burst=3, priority=5
        // P3: arrival=0, burst=1, priority=5
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 2, 5, 0));
        processes.add(new Process(2, 3, 5, 0));
        processes.add(new Process(3, 1, 5, 0));

        SimulationResult result = scheduler.schedule(processes);

        // All same priority, executes in arrival order
        assertEquals(2, result.getCompletionTimes().get(1));
        assertEquals(5, result.getCompletionTimes().get(2));
        assertEquals(6, result.getCompletionTimes().get(3));
        assertEquals(2, result.getTotalContextSwitches()); // P1->P2 at t=2, P2->P3 at t=5
    }

    @Test
    void testSingleProcess() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 7, 1, 0));

        SimulationResult result = scheduler.schedule(processes);

        assertEquals(0.0, result.getAverageWaitingTime());
        assertEquals(7.0, result.getAverageTurnaroundTime());
        assertEquals(0, result.getTotalContextSwitches());
    }
}