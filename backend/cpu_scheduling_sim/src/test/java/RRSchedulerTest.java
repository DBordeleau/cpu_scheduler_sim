import com.cpusim.scheduling.*;
import com.cpusim.model.Process;
import com.cpusim.model.SimulationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RRSchedulerTest {

    private RRScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new RRScheduler(2); // Default quantum of 2
    }

    @Test
    void testBasicRoundRobin() {
        // P1: arrival=0, burst=5
        // P2: arrival=0, burst=3
        // Quantum=2
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 5, 0, 0));
        processes.add(new Process(2, 3, 0, 0));

        SimulationResult result = scheduler.schedule(processes);

        // P1: 0-2, P2: 2-4, P1: 4-6, P2: 6-7, P1: 7-8
        assertEquals(8, result.getCompletionTimes().get(1));
        assertEquals(7, result.getCompletionTimes().get(2));
        assertEquals(4, result.getTotalContextSwitches()); // P1->P2 at t=2, P2->P1 at t=4, P1->P2 at t=6, P2->P1 at t=7
    }

    @Test
    void testQuantumLargerThanBurst() {
        // P1: arrival=0, burst=1
        // P2: arrival=0, burst=2
        // Quantum=5
        scheduler.setQuantum(5);
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 1, 0, 0));
        processes.add(new Process(2, 2, 0, 0));

        SimulationResult result = scheduler.schedule(processes);

        // Both processes complete within quantum
        assertEquals(1, result.getCompletionTimes().get(1));
        assertEquals(3, result.getCompletionTimes().get(2));
        assertEquals(1, result.getTotalContextSwitches()); // P1->P2 at t=1
    }

    @Test
    void testDifferentArrivalTimes() {
        // P1: arrival=0, burst=4
        // P2: arrival=1, burst=3
        // Quantum=2
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 4, 0, 0));
        processes.add(new Process(2, 3, 0, 1));

        SimulationResult result = scheduler.schedule(processes);

        // P1: 0-2, P2: 2-4, P1: 4-6, P2: 6-7
        assertEquals(6, result.getCompletionTimes().get(1));
        assertEquals(7, result.getCompletionTimes().get(2));
        assertEquals(3, result.getTotalContextSwitches()); // P1->P2 at t=2, P2->P1 at t=4, P1->P2 at t=6
    }

    @Test
    void testLargeQuantum() {
        // P1: arrival=0, burst=3
        // P2: arrival=0, burst=4
        // Quantum=10
        scheduler.setQuantum(10);
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 3, 0, 0));
        processes.add(new Process(2, 4, 0, 0));

        SimulationResult result = scheduler.schedule(processes);

        // Acts like FCFS when quantum is large
        assertEquals(3, result.getCompletionTimes().get(1));
        assertEquals(7, result.getCompletionTimes().get(2));
        assertEquals(1, result.getTotalContextSwitches()); // P1->P2 at t=3
    }

    @Test
    void testSingleProcess() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 6, 0, 0));

        SimulationResult result = scheduler.schedule(processes);

        assertEquals(0.0, result.getAverageWaitingTime());
        assertEquals(6.0, result.getAverageTurnaroundTime());
        assertEquals(0, result.getTotalContextSwitches());
    }
}