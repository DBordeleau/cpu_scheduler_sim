import React from 'react';
import SimulationTimeline from './SimulationTimeline';
import '../style/QuizResults.css';

function QuizResults({ quizResult, onQuizAgain }) {
    // Safety check in case actualResult is missing
    if (!quizResult || !quizResult.actualResult) {
        return (
            <div className="quiz-results-container">
                <h2>Error</h2>
                <p>Unable to load quiz results. Please try again.</p>
                <button className="quiz-again-btn" onClick={onQuizAgain}>
                    Try Again
                </button>
            </div>
        );
    }

    const allCorrect =
        quizResult.contextSwitchesCorrect &&
        quizResult.averageWaitingTimeCorrect &&
        quizResult.averageTurnaroundTimeCorrect;

    return (
        <div className="quiz-results-container">
            <h2>Quiz Results</h2>

            <div className="score-summary">
                {allCorrect ? (
                    <div className="all-correct">
                        <h3>Perfect Score!</h3>
                        <p>You got all answers correct!</p>
                    </div>
                ) : (
                    <div className="some-incorrect">
                        <p>Review your answers below:</p>
                    </div>
                )}
            </div>

            <div className="answer-comparison">
                <div className={`answer-row ${quizResult.contextSwitchesCorrect ? 'correct' : 'incorrect'}`}>
                    <span className="answer-label">Context Switches:</span>
                    <span className="user-answer">Your answer: {quizResult.userContextSwitches}</span>
                    <span className="actual-answer">Actual: {quizResult.actualResult.totalContextSwitches}</span>
                    <span className="status-icon">{quizResult.contextSwitchesCorrect ? '✓' : '✗'}</span>
                </div>

                <div className={`answer-row ${quizResult.averageWaitingTimeCorrect ? 'correct' : 'incorrect'}`}>
                    <span className="answer-label">Average Waiting Time:</span>
                    <span className="user-answer">Your answer: {quizResult.userAverageWaitingTime.toFixed(2)}</span>
                    <span className="actual-answer">Actual: {quizResult.actualResult.averageWaitingTime.toFixed(2)}</span>
                    <span className="status-icon">{quizResult.averageWaitingTimeCorrect ? '✓' : '✗'}</span>
                </div>

                <div className={`answer-row ${quizResult.averageTurnaroundTimeCorrect ? 'correct' : 'incorrect'}`}>
                    <span className="answer-label">Average Turnaround Time:</span>
                    <span className="user-answer">Your answer: {quizResult.userAverageTurnaroundTime.toFixed(2)}</span>
                    <span className="actual-answer">Actual: {quizResult.actualResult.averageTurnaroundTime.toFixed(2)}</span>
                    <span className="status-icon">{quizResult.averageTurnaroundTimeCorrect ? '✓' : '✗'}</span>
                </div>
            </div>

            <h3>Simulation Details</h3>
            <SimulationTimeline result={quizResult.actualResult} />

            <button className="quiz-again-btn" onClick={onQuizAgain}>
                Quiz Me Again
            </button>
        </div>
    );
}

export default QuizResults;
