import React, { useState } from 'react';
import '../style/AlgorithmList.css';
import Tooltip from './Tooltip';

const BASE_URL = process.env.REACT_APP_BACKEND_URL || "https://cpu-scheduling-sim.fly.dev/api";

const AlgorithmList = ({ processes, onSimulationComplete }) => {
    const [loading, setLoading] = useState(false);
    const [selectedAlgorithm, setSelectedAlgorithm] = useState(null);
    const [quantum, setQuantum] = useState(2);

    const algorithms = [
        { name: 'FCFS', displayName: 'Run First Come First Served Simulation' },
        { name: 'SJF', displayName: 'Run Shortest Job First Simulation' },
        { name: 'SRTF', displayName: 'Run Shortest Remaining Time First Simulation' },
        { name: 'PP', displayName: 'Run Priority Scheduling Simulation' },
        { name: 'RR', displayName: 'Run Round Robin Simulation', hasQuantum: true }
    ];

    // Tooltip text for each button
    const algorithmDescriptions = {
        FCFS: "The CPU works on processes in the order they arrive.",
        PP: "The CPU works on the highest priority process first. If a higher priority process arrives while another process is being worked on, the process is preempted and the CPU performs a context switch.",
        SJF: "The CPU works on the process with the shortest burst time. This scheduling algorithm is non-preemptive. Once a process is started it will be worked on until it finishes.",
        SRTF: "This is a preemptive version of the SJF algorithm. The CPU works on the process with the shortest burst time. If a process with a shorter burst time arrives, the active process is preempted and the CPU performs a context switch.",
        RR: "The CPU works on each process for a predefined quantum amount of time. Every q time units the CPU performs a context switch and works on the next process in the ready queue."
    };

    const runSimulation = async (algorithmName) => {
        setLoading(true);
        setSelectedAlgorithm(algorithmName);

        try {
            // Convert processes to array of tuples [burstTime, priority, arrivalTime]
            const processData = processes.map(p => [
                parseInt(p.burstTime) || 0,
                parseInt(p.priority) || 0,
                parseInt(p.arrivalTime) || 0
            ]);

            // First, submit processes
            const processResponse = await fetch(`${BASE_URL}/simulation/processes`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(processData)
            });

            if (!processResponse.ok) {
                throw new Error('Failed to submit processes');
            }

            // Then run simulation with quantum for RR
            const quantumParam = algorithmName === 'RR' ? quantum : 2;
            const simResponse = await fetch(
                `${BASE_URL}/simulation/simulate?algorithm=${algorithmName}&quantum=${quantumParam}`,
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    }
                }
            );

            if (!simResponse.ok) {
                throw new Error('Failed to run simulation');
            }

            const result = await simResponse.json();
            console.log('Simulation completed:', result);

            if (onSimulationComplete) {
                onSimulationComplete(result);
            }

        } catch (error) {
            console.error('Error running simulation:', error);
            alert('Error running simulation: ' + error.message);
        } finally {
            setLoading(false);
            setSelectedAlgorithm(null);
        }
    };

    return (
        <div className="algorithm-list-container">
            <h3>Scheduling Algorithms</h3>
            <div className="algorithm-buttons">
                {algorithms.map((algo) => (
                    <div key={algo.name} className="algorithm-row">
                        <Tooltip content={algorithmDescriptions[algo.name]}>
                            <button
                                onClick={() => runSimulation(algo.name)}
                                disabled={loading}
                                className={`algorithm-button ${selectedAlgorithm === algo.name ? 'loading' : ''}`}
                            >
                                {loading && selectedAlgorithm === algo.name ? (
                                    <>Running...</>
                                ) : (
                                    <>{algo.displayName}</>
                                )}
                            </button>
                        </Tooltip>
                        {algo.hasQuantum && (
                            <div className="quantum-input-container">
                                <label htmlFor="quantum">q =</label>
                                <input
                                    id="quantum"
                                    type="number"
                                    min="1"
                                    value={quantum}
                                    onChange={(e) => setQuantum(parseInt(e.target.value) || 1)}
                                    disabled={loading}
                                />
                            </div>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
};

export default AlgorithmList;