// Helper file for making requests to the spring boot backend

const BASE_URL = process.env.REACT_APP_BACKEND_URL || "https://cpu-scheduling-sim.fly.dev/api";

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

export async function generateQuiz() {
    const res = await fetch(`${BASE_URL}/simulation/quiz/generate`);
    return res.json();
}

export async function submitQuizAnswers(quizId, contextSwitches, avgWaitTime, avgTurnaroundTime) {
    const res = await fetch(
        `${BASE_URL}/simulation/quiz/submit?quizId=${quizId}&contextSwitches=${contextSwitches}&avgWaitTime=${avgWaitTime}&avgTurnaroundTime=${avgTurnaroundTime}`,
        {
            method: "POST",
        }
    );
    return res.json();
}