import React, { useState } from 'react';
import '../style/ProcessTable.css';

const ProcessTable = ({ onProcessesChange }) => {
    const [processes, setProcesses] = useState([
        { pid: 1, burstTime: '', priority: '', arrivalTime: '' }
    ]);

    const addProcess = () => {
        const newPid = processes.length + 1;
        const updatedProcesses = [
            ...processes,
            { pid: newPid, burstTime: '', priority: '', arrivalTime: '' }
        ];
        setProcesses(updatedProcesses);
        if (onProcessesChange) onProcessesChange(updatedProcesses);
    };

    const updateProcess = (index, field, value) => {
        const updatedProcesses = [...processes];
        updatedProcesses[index][field] = value;
        setProcesses(updatedProcesses);
        if (onProcessesChange) onProcessesChange(updatedProcesses);
    };

    // Expose processes to parent component on mount
    React.useEffect(() => {
        if (onProcessesChange) onProcessesChange(processes);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return (
        <div className="process-table-container">
            <table className="process-table">
                <thead>
                    <tr>
                        <th>Process ID</th>
                        <th>Burst Time</th>
                        <th>Priority</th>
                        <th>Arrival Time</th>
                    </tr>
                </thead>
                <tbody>
                    {processes.map((process, index) => (
                        <tr key={process.pid}>
                            <td className="pid-cell">
                                P<sub>{process.pid}</sub>
                            </td>
                            <td>
                                <input
                                    type="number"
                                    value={process.burstTime}
                                    onChange={(e) => updateProcess(index, 'burstTime', e.target.value)}
                                    placeholder="0"
                                    min="0"
                                />
                            </td>
                            <td>
                                <input
                                    type="number"
                                    value={process.priority}
                                    onChange={(e) => updateProcess(index, 'priority', e.target.value)}
                                    placeholder="0"
                                    min="0"
                                />
                            </td>
                            <td>
                                <input
                                    type="number"
                                    value={process.arrivalTime}
                                    onChange={(e) => updateProcess(index, 'arrivalTime', e.target.value)}
                                    placeholder="0"
                                    min="0"
                                />
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
            <div className="button-container">
                <button onClick={addProcess} className="add-button">
                    Add New Process
                </button>
            </div>
        </div>
    );
};

export default ProcessTable;