// Represents the result of checking quiz answers

package com.cpusim.model;

public class QuizResult {
    private SimulationResult actualResult;
    private boolean contextSwitchesCorrect;
    private boolean averageWaitingTimeCorrect;
    private boolean averageTurnaroundTimeCorrect;
    private int userContextSwitches;
    private double userAverageWaitingTime;
    private double userAverageTurnaroundTime;

    public QuizResult(
            SimulationResult actualResult,
            boolean contextSwitchesCorrect,
            boolean averageWaitingTimeCorrect,
            boolean averageTurnaroundTimeCorrect,
            int userContextSwitches,
            double userAverageWaitingTime,
            double userAverageTurnaroundTime) {
        this.actualResult = actualResult;
        this.contextSwitchesCorrect = contextSwitchesCorrect;
        this.averageWaitingTimeCorrect = averageWaitingTimeCorrect;
        this.averageTurnaroundTimeCorrect = averageTurnaroundTimeCorrect;
        this.userContextSwitches = userContextSwitches;
        this.userAverageWaitingTime = userAverageWaitingTime;
        this.userAverageTurnaroundTime = userAverageTurnaroundTime;
    }

    public SimulationResult getActualResult() {
        return actualResult;
    }

    public boolean isContextSwitchesCorrect() {
        return contextSwitchesCorrect;
    }

    public boolean isAverageWaitingTimeCorrect() {
        return averageWaitingTimeCorrect;
    }

    public boolean isAverageTurnaroundTimeCorrect() {
        return averageTurnaroundTimeCorrect;
    }

    public int getUserContextSwitches() {
        return userContextSwitches;
    }

    public double getUserAverageWaitingTime() {
        return userAverageWaitingTime;
    }

    public double getUserAverageTurnaroundTime() {
        return userAverageTurnaroundTime;
    }
}
