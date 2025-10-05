# CPU Scheduling Simulation

This is a web app I made to simulate different CPU scheduling algorithms and display simulation metrics and a timeline of events. The primary purpose of this project was to deepen my understanding of operating systems concepts. You can access a live demo [here](https://cpu-scheduler-sim.vercel.app/)

The project currently has implementations for the following algorithms:

- **First Come First Served (FCFS)** - The CPU works on processes in the order they arrive.
- **Preemptive Priority (PP)** - The CPU works on the highest priority process first. If a higher priority process arrives while another process is being worked on, the process is preempted and the CPU performs a context switch.
- **Shortest Job First (SJF)** - The CPU works on the process with the shortest burst time. This scheduling algorithm is non-preemptive. Once a process is started it will be worked on until it finishes.
- **Shortest Remaining Time First (SRTF)** - This is a preemptive version of the SJF algorithm. The CPU works on the process with the shortest burst time. If a process with a shorter burst time arrives, the active process is preempted and the CPU performs a context switch.
- **Round Robin (RR)** - The CPU works on each process for a predefined quantum amount of time. Every q time units the CPU performs a context switch and works on the next process in the ready queue.

And tracks the following metrics:

- **Context switches**: The simulation engine logs a context switch event when the CPU switches from one process to another. This occurs when one process is finishing while another process is in the ready queue, or when a process is preempted. A context switch is **not** logged when the CPU transitions from an idle state to working on a process.
- **Waiting time**: The simulation engine tracks waiting time per process and the average waiting time for all processes. Waiting time is defined as the amount of time a process spends in the ready queue before it is allocated any CPU time.
- **Turnaround time**: The simulation engine tracks turnaround time per process and the average turnaround time for all processes. Turnaround time is defined as the amount of time it takes for a process to finish after it arrives in the ready queue.

## Project Structure

The project is separated into a backend and frontend which are deployed separately. The backend is a Maven application with a Springboot API written in Java. The frontend is a React app.

```
cpu_sim/
├── backend/                                         # Simulation Engine + API (Maven/Springboot)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/cpusim/
│   │   │   │   ├── api/
│   │   │   │   │   └── SimulationController.java    # REST API endpoints
│   │   │   │   ├── config/
│   │   │   │   │   └── CorsConfig.java              # CORS configuration
│   │   │   │   ├── model/
│   │   │   │   │   ├── Process.java                 # Process data model
│   │   │   │   │   ├── SimulationResult.java        # Simulation result data model
│   │   │   │   │   └── TimelineEvent.java           # Timeline event data model
│   │   │   │   ├── scheduling/
│   │   │   │   │   ├── Scheduler.java               # Scheduler interface
│   │   │   │   │   ├── FCFSScheduler.java           # First Come First Served implementation
│   │   │   │   │   ├── SJFScheduler.java            # Shortest Job First implementation
│   │   │   │   │   ├── SRTFScheduler.java           # Shortest Remaining Time First 
│   │   │   │   │   ├── PPScheduler.java             # Preemptive Priority implementation
│   │   │   │   │   └── RRScheduler.java             # Round Robin implementation
│   │   │   │   ├── service/
│   │   │   │   │   └── SimulationService.java       # Business logic for API
│   │   │   │   ├── simulation/                      # Core simulation engine
│   │   │   │   └── Main.java                        # Springboot application entry point
│   │   │   └── resources/
│   │   │       └── application.properties           # Springboot configuration
│   │   └── test/java/                               # Unit tests for all schedulers
│   ├── pom.xml                                      # Maven build configuration
│   ├── dockerfile                                   # Docker container configuration
│   └── fly.toml                                     # Fly.io deployment configuration
├── frontend/                                        # Web Interface (React)
│   ├── public/
│   │   ├── index.html                               # Main webpage
│   │   └── ...                                      # Other static assets (favicon, etc)
│   ├── src/
│   │   ├── components/                              # React Components
│   │   │   ├── ProcessTable.jsx                     # Table for adding/editing processes
│   │   │   ├── AlgorithmList.jsx                    # Algorithm selection buttons
│   │   │   ├── SimulationTimeline.jsx               # Timeline and metrics display
│   │   │   └── Tooltip.jsx                          # Reusable tooltip for metrics
│   │   ├── style/                                   # CSS for React Components
│   │   │   └── ...  
│   │   ├── App.js                                   # Main React component
│   │   ├── App.css                                  # Main app styles
│   │   ├── index.js                                 # React entry point
│   │   ├── index.css                                # Global styles
│   │   └── api.js                                   # API client utilities
│   ├── package.json                                 # Node dependencies and scripts
└── README.md                                        # You are here
```

## Simulation Engine Components

### Process.java
Represents a process in the ready queue
- Process ID (pid), unique autoincrementing identifier
- Burst time, the amount of time units the CPU will need to work on the process to complete it
- Priority, lower number = higher priority
- Arrival time, the time at which the process is added to the ready queue

### TimelineEvent.java
Represents a single event in the simulation timeline
- Time: The time the event occured at.
- Pid: The ID of the process the event is associated with.
- Type: IDLE, PROCESS_ARRIVAL, PROCESS_START, PROCESS_FINISH, PROCESS_SWITCH
- Remaining Burst: Burst time - time the process has been active. Only useful for preemptive simulations.
- Priority: The priority of the process associated with this event, only useful for PP simulations.

### SimulationResult.java
Represents the results of a single simulation. Consumed by the frontend to render the simulation timeline and display metrics
- Timeline: A list of TimelineEvents that occurred during the simulation.
- Average waiting time: Sum of all waiting times / number of processes in the sim.
- Average turnaround time: Sum of all turnaround times / number of processes in the sim.
- Context switches: Number of CONTEXT_SWITCH events in the timeline. 
- Completion times: A map of process IDs -> the process' completion time.
- Waiting times: A map of process IDs -> the process' waiting time.
- Turnaround times: A map of process IDs -> the process' turnaround time.

### Scheduler.java
The interface that all of the scheduling algorithms implement. All schedulers manage a list of all processes included in the simulation (user-defined) and all of the data to be passed to the SimulationResult
    - **FCFSScheduler.java** - First come first served scheduling algorithm implementation.
    - **PPcheduler.java** - Preemptive priority scheduling algorithm implementation.
    - **SJFScheduler.java** - Shortest job first scheduling algorithm implementation.
    - **SRTFScheduler.java** - Shortest remaining time first scheduling algorithm implementation.
    - **RRScheduler.java** - Round robin scheduling algorithm implementation.

### SimulationEngine.java
The core simulation engine that sits between the scheduler and the web layer. The simulation engine runs simulations from beginning to end and formats the results before it is consumed by the frontend.

### SimulationController.java
REST controller for the Springboot API. Defines endpoints that add processes to the process list, initiate simulations and retrieve results via the SimulationService.

### SimulationService.java
All logic for the Springboot API.
    
### Main.java
Runs the Springboot application.

## License

This project is licensed under the MIT License.
