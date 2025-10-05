// Helper file for making requests to the spring boot backend

const BASE_URL = "https://cpu-scheduling-sim.fly.dev/api";

export async function addProcess(process) {
    const res = await fetch(`${BASE_URL}/processes`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(process),
    });
    return res.json();
}

export async function listProcesses() {
    const res = await fetch(`${BASE_URL}/processes`);
    return res.json();
}

export async function runSimulation(algorithm, quantum = 0) {
    const res = await fetch(`${BASE_URL}/simulate?algorithm=${algorithm}&quantum=${quantum}`, {
        method: "POST",
    });
    return res.json();
}

export async function getResults() {
    const res = await fetch(`${BASE_URL}/results`);
    return res.json();
}