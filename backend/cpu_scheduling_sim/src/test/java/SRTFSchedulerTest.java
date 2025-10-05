import com.cpusim.scheduling.*;
import com.cpusim.model.Process;
import com.cpusim.model.SimulationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SRTFSchedulerTest {

    private SRTFScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new SRTFScheduler();
    }

    @Test
    void testPreemptionOccurs() {
        // P1: arrival=0, burst=7
        // P2: arrival=2, burst=4
        // P3: arrival=4, burst=1
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 7, 0, 0));
        processes.add(new Process(2, 4, 0, 2));
        processes.add(new Process(3, 1, 0, 4));

        SimulationResult result = scheduler.schedule(processes);

        // P1 runs 0-2, preempted by P2
        // P2 runs 2-4, preempted by P3
        // P3 runs 4-5, completes
        // P2 resumes 5-7, completes
        // P1 resumes 7-12, completes
        assertEquals(12, result.getCompletionTimes().get(1));
        assertEquals(7, result.getCompletionTimes().get(2));
        assertEquals(5, result.getCompletionTimes().get(3));
        assertEquals(4, result.getTotalContextSwitches()); // P1->P2 at t=2, P2->P3 at t=4, P3->P2 at t=5, P2->P1 at t=7
    }

    @Test
    void testNoPreemptionNeeded() {
        // P1: arrival=0, burst=2
        // P2: arrival=2, burst=3
        // P3: arrival=5, burst=4
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 2, 0, 0));
        processes.add(new Process(2, 3, 0, 2));
        processes.add(new Process(3, 4, 0, 5));

        SimulationResult result = scheduler.schedule(processes);

        assertEquals(2, result.getCompletionTimes().get(1));
        assertEquals(5, result.getCompletionTimes().get(2));
        assertEquals(9, result.getCompletionTimes().get(3));
        assertEquals(2, result.getTotalContextSwitches()); // P1->P2 at t=2, P2->P3 at t=5
    }

    @Test
    void testMultiplePreemptions() {
        // P1: arrival=0, burst=8
        // P2: arrival=1, burst=1
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 8, 0, 0));
        processes.add(new Process(2, 1, 0, 1));

        SimulationResult result = scheduler.schedule(processes);

        // P1 runs 0-1, preempted
        // P2 runs 1-2, completes
        // P1 resumes 2-9, completes
        assertEquals(9, result.getCompletionTimes().get(1));
        assertEquals(2, result.getCompletionTimes().get(2));
        assertEquals(2, result.getTotalContextSwitches()); // P1->P2 at t=1, P2->P1 at t=2
    }

    @Test
    void testAllProcessesSameRemainingTime() {
        // P1: arrival=0, burst=3
        // P2: arrival=0, burst=3
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 3, 0, 0));
        processes.add(new Process(2, 3, 0, 0));

        SimulationResult result = scheduler.schedule(processes);

        // Should break tie by arrival time (both 0, so by order added)
        assertEquals(3, result.getCompletionTimes().get(1));
        assertEquals(6, result.getCompletionTimes().get(2));
        assertEquals(1, result.getTotalContextSwitches()); // P1->P2 at t=3
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