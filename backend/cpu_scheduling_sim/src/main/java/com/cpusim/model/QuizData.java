// Represents quiz data sent to the frontend

package com.cpusim.model;

import java.util.List;

public class QuizData {
    private String quizId;
    private List<Process> processes;
    private String algorithm;
    private String algorithmDisplayName;
    private Integer quantum; // Only applicable for RR

    public QuizData(String quizId, List<Process> processes, String algorithm, String algorithmDisplayName,
            Integer quantum) {
        this.quizId = quizId;
        this.processes = processes;
        this.algorithm = algorithm;
        this.algorithmDisplayName = algorithmDisplayName;
        this.quantum = quantum;
    }

    public String getQuizId() {
        return quizId;
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getAlgorithmDisplayName() {
        return algorithmDisplayName;
    }

    public Integer getQuantum() {
        return quantum;
    }
}
