/*
    Handles logic for our Spring Boot REST API endpoints.
 */

package com.cpusim.service;

import com.cpusim.model.Process;
import com.cpusim.model.QuizData;
import com.cpusim.model.QuizResult;
import com.cpusim.model.SimulationResult;
import com.cpusim.scheduling.*;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SimulationService {

    private final List<Process> processes = new ArrayList<>();
    private SimulationResult lastResult;
    private final Map<String, QuizData> activeQuizzes = new HashMap<>();
    private final Random random = new Random();

    public void addProcess(Process process) {
        processes.add(process);
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public void clearProcesses() {
        processes.clear();
    }

    public SimulationResult runSimulation(String algorithm, int quantum) {
        Scheduler scheduler = switch (algorithm.toLowerCase()) {
            case "fcfs" -> new FCFSScheduler();
            case "sjf" -> new SJFScheduler();
            case "srtf" -> new SRTFScheduler();
            case "pp", "priority" -> new PPScheduler();
            case "rr", "roundrobin" -> new RRScheduler(quantum);
            default -> throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        };

        lastResult = scheduler.schedule(new ArrayList<>(processes));
        return lastResult;
    }

    public SimulationResult getLastResult() {
        if (lastResult == null) {
            throw new IllegalStateException("No simulation has been run yet.");
        }
        return lastResult;
    }

    // Generate a random quiz with 4-10 processes and a random algorithm
    public QuizData generateQuiz() {
        String quizId = UUID.randomUUID().toString();

        // Generate 4-10 random processes
        int numProcesses = random.nextInt(7) + 4;
        List<Process> quizProcesses = new ArrayList<>();

        for (int i = 0; i < numProcesses; i++) {
            int burstTime = random.nextInt(15) + 1; // 1-15 time units
            int priority = random.nextInt(10) + 1; // 1-10 priority
            int arrivalTime = random.nextInt(10); // 0-9 arrival time

            quizProcesses.add(new Process(i + 1, burstTime, priority, arrivalTime));
        }

        // Sort processes by arrival time to ensure P1 arrives before or at the same
        // time as P2, etc.
        // Makes the process table easier to read and understand
        quizProcesses.sort(Comparator.comparingInt(Process::getArrivalTime));

        // Reassign PIDs after sorting so they're in order (1, 2, 3, ...)
        for (int i = 0; i < quizProcesses.size(); i++) {
            Process oldProcess = quizProcesses.get(i);
            quizProcesses.set(i, new Process(
                    i + 1, // new PID in sorted order
                    oldProcess.getBurstTime(),
                    oldProcess.getPriority(),
                    oldProcess.getArrivalTime()));
        }

        // Select a random algorithm
        String[] algorithms = { "fcfs", "sjf", "srtf", "pp", "rr" };
        String selectedAlgorithm = algorithms[random.nextInt(algorithms.length)];

        // Generate a random quantum for RR (2-6)
        Integer quantum = selectedAlgorithm.equals("rr") ? random.nextInt(5) + 2 : null;

        // Map algorithm codes to display names
        String displayName = switch (selectedAlgorithm) {
            case "fcfs" -> "First Come First Served (FCFS)";
            case "sjf" -> "Shortest Job First (SJF)";
            case "srtf" -> "Shortest Remaining Time First (SRTF)";
            case "pp" -> "Preemptive Priority (PP)";
            case "rr" -> "Round Robin (RR)";
            default -> selectedAlgorithm;
        };

        QuizData quizData = new QuizData(quizId, quizProcesses, selectedAlgorithm, displayName, quantum);
        activeQuizzes.put(quizId, quizData);

        return quizData;
    }

    // Check quiz answers and return results
    public QuizResult checkQuizAnswers(String quizId, int userContextSwitches, double userAvgWaitTime,
            double userAvgTurnaroundTime) {
        QuizData quiz = activeQuizzes.get(quizId);
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz not found: " + quizId);
        }

        // Run the simulation with the quiz's algorithm
        Scheduler scheduler = switch (quiz.getAlgorithm().toLowerCase()) {
            case "fcfs" -> new FCFSScheduler();
            case "sjf" -> new SJFScheduler();
            case "srtf" -> new SRTFScheduler();
            case "pp" -> new PPScheduler();
            case "rr" -> new RRScheduler(quiz.getQuantum());
            default -> throw new IllegalArgumentException("Unknown algorithm: " + quiz.getAlgorithm());
        };

        SimulationResult actualResult = scheduler.schedule(new ArrayList<>(quiz.getProcesses()));

        // Check answers with tolerance for floating point (0.01 margin of error)
        // This allows .66 and .33 to be accepted for 0.67 and 0.33
        boolean contextSwitchesCorrect = userContextSwitches == actualResult.getTotalContextSwitches();
        boolean waitTimeCorrect = Math.abs(userAvgWaitTime - actualResult.getAverageWaitingTime()) <= 0.01;
        boolean turnaroundTimeCorrect = Math
                .abs(userAvgTurnaroundTime - actualResult.getAverageTurnaroundTime()) <= 0.01;

        // Clean up the quiz from active quizzes
        activeQuizzes.remove(quizId);

        return new QuizResult(
                actualResult,
                contextSwitchesCorrect,
                waitTimeCorrect,
                turnaroundTimeCorrect,
                userContextSwitches,
                userAvgWaitTime,
                userAvgTurnaroundTime);
    }
}
