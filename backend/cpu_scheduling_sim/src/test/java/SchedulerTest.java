import com.cpusim.model.TimelineEvent.EventType;
import com.cpusim.scheduling.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SchedulerTest {

    private final Scheduler scheduler = new FCFSScheduler(); // Use any implementation

    @Test
    void testEventPriority_ProcessArrivalHasLowestPriority() {
        assertEquals(0, scheduler.getEventPriority(EventType.PROCESS_ARRIVAL));
    }

    @Test
    void testEventPriority_ProcessFinishBeforeContextSwitch() {
        assertTrue(scheduler.getEventPriority(EventType.PROCESS_FINISH) < scheduler
                .getEventPriority(EventType.CONTEXT_SWITCH));
    }

    @Test
    void testEventPriority_ContextSwitchBeforeProcessStart() {
        assertTrue(scheduler.getEventPriority(EventType.CONTEXT_SWITCH) < scheduler
                .getEventPriority(EventType.PROCESS_START));
    }

    @Test
    void testEventPriority_ProcessStartBeforeCpuIdle() {
        assertTrue(
                scheduler.getEventPriority(EventType.PROCESS_START) < scheduler.getEventPriority(EventType.CPU_IDLE));
    }

    @Test
    void testEventPriority_CpuIdleHasHighestPriority() {
        assertEquals(4, scheduler.getEventPriority(EventType.CPU_IDLE));
    }
}