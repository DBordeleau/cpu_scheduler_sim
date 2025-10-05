import React, { useState } from 'react';
import '../style/AlgorithmList.css';

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