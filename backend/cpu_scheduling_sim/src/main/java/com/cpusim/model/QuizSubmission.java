// Represents quiz submission data from the frontend

package com.cpusim.model;

import java.util.List;

public class QuizSubmission {
    private String quizId;
    private List<Process> processes;
    private String algorithm;
    private Integer quantum;
    private int userContextSwitches;
    private double userAverageWaitingTime;
    private double userAverageTurnaroundTime;

    // Default constructor for Jackson
    public QuizSubmission() {
    }

    public QuizSubmission(String quizId, List<Process> processes, String algorithm, Integer quantum,
            int userContextSwitches, double userAverageWaitingTime, double userAverageTurnaroundTime) {
        this.quizId = quizId;
        this.processes = processes;
        this.algorithm = algorithm;
        this.quantum = quantum;
        this.userContextSwitches = userContextSwitches;
        this.userAverageWaitingTime = userAverageWaitingTime;
        this.userAverageTurnaroundTime = userAverageTurnaroundTime;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public void setProcesses(List<Process> processes) {
        this.processes = processes;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public Integer getQuantum() {
        return quantum;
    }

    public void setQuantum(Integer quantum) {
        this.quantum = quantum;
    }

    public int getUserContextSwitches() {
        return userContextSwitches;
    }

    public void setUserContextSwitches(int userContextSwitches) {
        this.userContextSwitches = userContextSwitches;
    }

    public double getUserAverageWaitingTime() {
        return userAverageWaitingTime;
    }

    public void setUserAverageWaitingTime(double userAverageWaitingTime) {
        this.userAverageWaitingTime = userAverageWaitingTime;
    }

    public double getUserAverageTurnaroundTime() {
        return userAverageTurnaroundTime;
    }

    public void setUserAverageTurnaroundTime(double userAverageTurnaroundTime) {
        this.userAverageTurnaroundTime = userAverageTurnaroundTime;
    }
}
