import React, { useState } from 'react';
import '../style/Quiz.css';

function Quiz({ quizData, onSubmit }) {
    const [contextSwitches, setContextSwitches] = useState('');
    const [avgWaitTime, setAvgWaitTime] = useState('');
    const [avgTurnaroundTime, setAvgTurnaroundTime] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        onSubmit({
            contextSwitches: parseInt(contextSwitches),
            avgWaitTime: parseFloat(avgWaitTime),
            avgTurnaroundTime: parseFloat(avgTurnaroundTime)
        });
    };

    return (
        <div className="quiz-container">
            <h2>Quiz Mode</h2>
            <div className="quiz-instructions">
                <p>
                    Input your answers below assuming the processes below were scheduled with{' '}
                    <strong>{quizData.algorithmDisplayName}</strong> scheduling.
                    {quizData.quantum && ` (Quantum = ${quizData.quantum})`}
                </p>
            </div>

            <div className="quiz-process-table-container">
                <table className="quiz-process-table">
                    <thead>
                        <tr>
                            <th>Process ID</th>
                            <th>Burst Time</th>
                            <th>Priority</th>
                            <th>Arrival Time</th>
                        </tr>
                    </thead>
                    <tbody>
                        {quizData.processes.map((process) => (
                            <tr key={process.pid}>
                                <td>P<sub>{process.pid}</sub></td>
                                <td>{process.burstTime}</td>
                                <td>{process.priority}</td>
                                <td>{process.arrivalTime}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            <form className="quiz-form" onSubmit={handleSubmit}>
                <div className="quiz-input-group">
                    <label htmlFor="contextSwitches">Context Switches:</label>
                    <input
                        type="number"
                        id="contextSwitches"
                        value={contextSwitches}
                        onChange={(e) => setContextSwitches(e.target.value)}
                        required
                        min="0"
                    />
                </div>

                <div className="quiz-input-group">
                    <label htmlFor="avgWaitTime">Average Waiting Time:</label>
                    <input
                        type="number"
                        id="avgWaitTime"
                        value={avgWaitTime}
                        onChange={(e) => setAvgWaitTime(e.target.value)}
                        required
                        step="0.01"
                        min="0"
                    />
                </div>

                <div className="quiz-input-group">
                    <label htmlFor="avgTurnaroundTime">Average Turnaround Time:</label>
                    <input
                        type="number"
                        id="avgTurnaroundTime"
                        value={avgTurnaroundTime}
                        onChange={(e) => setAvgTurnaroundTime(e.target.value)}
                        required
                        step="0.01"
                        min="0"
                    />
                </div>

                <button type="submit" className="submit-quiz-btn">
                    Submit Answers
                </button>
            </form>
        </div>
    );
}

export default Quiz;
