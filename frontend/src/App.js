import React, { useState } from 'react';
import ProcessTable from './components/ProcessTable';
import AlgorithmList from './components/AlgorithmList';
import SimulationTimeline from './components/SimulationTimeline';
import './App.css';

function App() {
  const [processes, setProcesses] = useState([]);
  const [simulationResult, setSimulationResult] = useState(null);

  return (
    <div className="App">
      <h1>CPU Scheduling Simulator</h1>
      <ProcessTable onProcessesChange={setProcesses} />
      <AlgorithmList
        processes={processes}
        onSimulationComplete={setSimulationResult}
      />
      <SimulationTimeline result={simulationResult} />
    </div>
  );
}

export default App;