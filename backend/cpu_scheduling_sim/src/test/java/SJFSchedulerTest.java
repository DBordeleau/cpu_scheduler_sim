import com.cpusim.scheduling.*;
import com.cpusim.model.Process;
import com.cpusim.model.SimulationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SJFSchedulerTest {

    private SJFScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new SJFScheduler();
    }

    @Test
    void testSelectsShortestJobFirst() {
        // P1: arrival=0, burst=8
        // P2: arrival=1, burst=4
        // P3: arrival=2, burst=2
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 8, 0, 0));
        processes.add(new Process(2, 4, 0, 1));
        processes.add(new Process(3, 2, 0, 2));

        SimulationResult result = scheduler.schedule(processes);

        // Order: P1 (0-8), P3 (8-10), P2 (10-14)
        assertEquals(8, result.getCompletionTimes().get(1));
        assertEquals(10, result.getCompletionTimes().get(3));
        assertEquals(14, result.getCompletionTimes().get(2));
        assertEquals(5, result.getAverageWaitingTime(), 0.01); // (0 + 9 + 6) / 3
        assertEquals(2, result.getTotalContextSwitches()); // P1->P3 at t=8, P3->P2 at t=10
    }

    @Test
    void testAllProcessesArriveAtOnce() {
        // P1: arrival=0, burst=6
        // P2: arrival=0, burst=3
        // P3: arrival=0, burst=1
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 6, 0, 0));
        processes.add(new Process(2, 3, 0, 0));
        processes.add(new Process(3, 1, 0, 0));

        SimulationResult result = scheduler.schedule(processes);

        // Order: P3 (0-1), P2 (1-4), P1 (4-10)
        assertEquals(1, result.getCompletionTimes().get(3));
        assertEquals(4, result.getCompletionTimes().get(2));
        assertEquals(10, result.getCompletionTimes().get(1));
        assertEquals(1.66, result.getAverageWaitingTime(), 0.01); // (4 + 1 + 0) / 3
        assertEquals(2, result.getTotalContextSwitches()); // P3->P2 at t=1, P2->P1 at t=4
    }

    @Test
    void testWithIdleTime() {
        // P1: arrival=0, burst=3
        // P2: arrival=5, burst=2
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 3, 0, 0));
        processes.add(new Process(2, 2, 0, 5));

        SimulationResult result = scheduler.schedule(processes);

        assertEquals(3, result.getCompletionTimes().get(1));
        assertEquals(7, result.getCompletionTimes().get(2));
        assertEquals(0.0, result.getAverageWaitingTime());
        assertEquals(0, result.getTotalContextSwitches()); // No context switch when CPU is idle
    }

    @Test
    void testTieBreakingByArrivalTime() {
        // P1: arrival=0, burst=4
        // P2: arrival=1, burst=4
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 4, 0, 0));
        processes.add(new Process(2, 4, 0, 1));

        SimulationResult result = scheduler.schedule(processes);

        // P1 arrives first, should run first despite same burst time
        assertEquals(4, result.getCompletionTimes().get(1));
        assertEquals(8, result.getCompletionTimes().get(2));
        assertEquals(1, result.getTotalContextSwitches()); // P1->P2 at t=4
    }

    @Test
    void testSingleProcess() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 5, 0, 0));

        SimulationResult result = scheduler.schedule(processes);

        assertEquals(0.0, result.getAverageWaitingTime());
        assertEquals(5.0, result.getAverageTurnaroundTime());
        assertEquals(0, result.getTotalContextSwitches());
    }
}