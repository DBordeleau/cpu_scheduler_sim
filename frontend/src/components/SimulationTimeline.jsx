import React from 'react';
import '../style/SimulationTimeline.css';
import Tooltip from './Tooltip';
import { FaInfoCircle } from 'react-icons/fa';

const SimulationTimeline = ({ result }) => {
    if (!result) {
        return (
            <div className="timeline-container">
                <p className="no-results">No simulation results to display. Run a simulation to see the timeline.</p>
            </div>
        );
    }

    // Helper function to safely format numbers
    const formatNumber = (value) => {
        if (value === null || value === undefined) return '0.00';
        const num = typeof value === 'number' ? value : parseFloat(value);
        return isNaN(num) ? '0.00' : num.toFixed(2);
    };

    const getEventTypeLabel = (type) => {
        switch (type) {
            case 'PROCESS_ARRIVAL':
                return 'Arrival';
            case 'PROCESS_START':
                return 'Start';
            case 'PROCESS_FINISH':
                return 'Finish';
            case 'CONTEXT_SWITCH':
                return 'Context Switch';
            case 'CPU_IDLE':
                return 'CPU Idle';
            default:
                return type;
        }
    };

    const getEventClass = (type) => {
        switch (type) {
            case 'PROCESS_ARRIVAL':
                return 'event-arrival';
            case 'PROCESS_START':
                return 'event-start';
            case 'PROCESS_FINISH':
                return 'event-finish';
            case 'CONTEXT_SWITCH':
                return 'event-switch';
            case 'CPU_IDLE':
                return 'event-idle';
            default:
                return '';
        }
    };

    // Debug: Log the result to see what structure we're getting
    console.log('Simulation result:', result);
    console.log('Timeline events:', result.timeline);

    // Log a specific context switch event for debugging
    if (result.timeline) {
        const contextSwitches = result.timeline.filter(e => e.type === 'CONTEXT_SWITCH');
        console.log('Context switch events:', contextSwitches);
    }

    return (
        <div className="timeline-container">
            <h2>Simulation Results</h2>

            {/* Performance Metrics */}
            <div className="metrics-container">
                <h3>Performance Metrics</h3>
                <div className="metrics-grid">
                    <div className="metric-card">
                        <span className="metric-label">
                            <Tooltip content="The waiting time for a process is the total time it spends in the ready queue not being worked on.">
                                <span><FaInfoCircle className='tooltip-icon' /></span>
                            </Tooltip>
                            Avg Waiting Time:
                        </span>
                        <span className="metric-value">{formatNumber(result.averageWaitingTime)}</span>
                    </div>
                    <div className="metric-card">
                        <span className="metric-label">
                            <Tooltip content="The turnaround time for a process is equal to its completion time - its arrival time.">
                                <span><FaInfoCircle className='tooltip-icon' /></span>
                            </Tooltip>
                            Avg Turnaround Time:
                        </span>
                        <span className="metric-value">{formatNumber(result.averageTurnaroundTime)}</span>
                    </div>
                    <div className="metric-card">
                        <span className="metric-label">
                            <Tooltip content={
                                <>
                                    A context switch occurs when the CPU switches from one active process to another. This happens when a process is preempted (interrupted), or when a process finishes while another process is ready to be executed. The CPU switching from an idle state to working on a process is <strong>not</strong> a context switch.
                                </>
                            }>
                                <span><FaInfoCircle className='tooltip-icon' /></span>
                            </Tooltip>
                            Context Switches:
                        </span>
                        <span className="metric-value">{result.totalContextSwitches || 0}</span>
                    </div>
                </div>
            </div>

            {/* Timeline Events */}
            <div className="timeline-events">
                <h3>Timeline Events</h3>
                <div className="events-list">
                    {result.timeline && result.timeline.length > 0 ? (
                        result.timeline.map((event, index) => (
                            <div key={index} className={`event-item ${getEventClass(event.type)}`}>
                                <div className="event-time">t = {event.time}</div>
                                <div className="event-details">
                                    {event.type === 'CPU_IDLE' ? (
                                        <span>CPU Idle</span>
                                    ) : event.type === 'PROCESS_ARRIVAL' ? (
                                        <span>
                                            P<sub>{event.pid}</sub> - {getEventTypeLabel(event.type)}
                                            {event.burstRemaining !== null && event.burstRemaining !== undefined && (
                                                <span className="event-info"> (Burst: {event.burstRemaining}, Priority: {event.priority})</span>
                                            )}
                                        </span>
                                    ) : event.type === 'PROCESS_START' ? (
                                        <span>
                                            P<sub>{event.pid}</sub> - {getEventTypeLabel(event.type)}
                                            {event.burstRemaining !== null && event.burstRemaining !== undefined && (
                                                <span className="event-info"> (Burst Remaining: {event.burstRemaining}, Priority: {event.priority})</span>
                                            )}
                                        </span>
                                    ) : event.type === 'CONTEXT_SWITCH' ? (
                                        <span>
                                            P<sub>{event.pid}</sub> - {getEventTypeLabel(event.type)}
                                            {event.burstRemaining !== null && event.burstRemaining !== undefined && (
                                                <span className="event-info"> (Burst Remaining: {event.burstRemaining}, Priority: {event.priority})</span>
                                            )}
                                        </span>
                                    ) : (
                                        <span>
                                            P<sub>{event.pid}</sub> - {getEventTypeLabel(event.type)}
                                        </span>
                                    )}
                                </div>
                            </div>
                        ))
                    ) : (
                        <p>No timeline events available</p>
                    )}
                </div>
            </div>

            {/* Per-Process Metrics */}
            <div className="process-metrics">
                <h3>Per-Process Metrics</h3>
                {result.completionTimes && Object.keys(result.completionTimes).length > 0 ? (
                    <table className="metrics-table">
                        <thead>
                            <tr>
                                <th>Process</th>
                                <th>Completion Time</th>
                                <th>Waiting Time</th>
                                <th>Turnaround Time</th>
                            </tr>
                        </thead>
                        <tbody>
                            {Object.keys(result.completionTimes).map((pid) => (
                                <tr key={pid}>
                                    <td>P<sub>{pid}</sub></td>
                                    <td>{result.completionTimes[pid]}</td>
                                    <td>{result.waitingTimes?.[pid] !== undefined ? result.waitingTimes[pid] : 'N/A'}</td>
                                    <td>{result.turnaroundTimes?.[pid] !== undefined ? result.turnaroundTimes[pid] : 'N/A'}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                ) : (
                    <p>No per-process metrics available</p>
                )}
            </div>
        </div>
    );
};

export default SimulationTimeline;