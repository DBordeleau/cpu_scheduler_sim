import com.cpusim.scheduling.*;
import com.cpusim.model.Process;
import com.cpusim.model.SimulationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FCFSSchedulerTest {

    private FCFSScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new FCFSScheduler();
    }

    @Test
    void testSimpleSequentialProcesses() {
        // P1: arrival=0, burst=4
        // P2: arrival=0, burst=3
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 4, 0, 0));
        processes.add(new Process(2, 3, 0, 0));

        SimulationResult result = scheduler.schedule(processes);

        assertEquals(2.0, result.getAverageWaitingTime()); // (0 + 4) / 2
        assertEquals(5.5, result.getAverageTurnaroundTime()); // (4 + 7) / 2
        assertEquals(1, result.getTotalContextSwitches());
        assertEquals(4, result.getCompletionTimes().get(1));
        assertEquals(7, result.getCompletionTimes().get(2));
    }

    @Test
    void testProcessesWithGap_CpuIdle() {
        // P1: arrival=0, burst=2
        // P2: arrival=5, burst=3
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 2, 0, 0));
        processes.add(new Process(2, 3, 0, 5));

        SimulationResult result = scheduler.schedule(processes);

        assertEquals(0.0, result.getAverageWaitingTime());
        assertEquals(2.5, result.getAverageTurnaroundTime()); // (2 + 3) / 2
        assertEquals(0, result.getTotalContextSwitches()); // No context switch during idle
        assertEquals(2, result.getCompletionTimes().get(1));
        assertEquals(8, result.getCompletionTimes().get(2));
    }

    @Test
    void testMultipleProcessesArrivingAtSameTime() {
        // P1: arrival=0, burst=5
        // P2: arrival=0, burst=3
        // P3: arrival=0, burst=2
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 5, 0, 0));
        processes.add(new Process(2, 3, 0, 0));
        processes.add(new Process(3, 2, 0, 0));

        SimulationResult result = scheduler.schedule(processes);

        assertEquals(4.33, result.getAverageWaitingTime(), 0.01); // (0 + 5 + 8) / 3
        assertEquals(7.66, result.getAverageTurnaroundTime(), 0.01); // (5 + 8 + 10) / 3
        assertEquals(2, result.getTotalContextSwitches());
        assertEquals(5, result.getCompletionTimes().get(1));
        assertEquals(8, result.getCompletionTimes().get(2));
        assertEquals(10, result.getCompletionTimes().get(3));
    }

    @Test
    void testSingleProcess() {
        // P1: arrival=0, burst=10
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 10, 0, 0));

        SimulationResult result = scheduler.schedule(processes);

        assertEquals(0.0, result.getAverageWaitingTime());
        assertEquals(10.0, result.getAverageTurnaroundTime());
        assertEquals(0, result.getTotalContextSwitches());
        assertEquals(10, result.getCompletionTimes().get(1));
    }

    @Test
    void testProcessArrivingLater() {
        // P1: arrival=2, burst=4
        // P2: arrival=3, burst=2
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 4, 0, 2));
        processes.add(new Process(2, 2, 0, 3));

        SimulationResult result = scheduler.schedule(processes);

        assertEquals(1.5, result.getAverageWaitingTime()); // (0 + 3) / 2
        assertEquals(4.5, result.getAverageTurnaroundTime()); // (4 + 5) / 2
        assertEquals(1, result.getTotalContextSwitches());
        assertEquals(6, result.getCompletionTimes().get(1));
        assertEquals(8, result.getCompletionTimes().get(2));
    }
}