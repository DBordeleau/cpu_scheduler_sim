import React, { useState } from 'react';
import ProcessTable from './components/ProcessTable';
import AlgorithmList from './components/AlgorithmList';
import SimulationTimeline from './components/SimulationTimeline';
import Quiz from './components/Quiz';
import QuizResults from './components/QuizResults';
import { generateQuiz, submitQuizAnswers } from './api';
import './App.css';
import Footer from './components/Footer';

function App() {
  const [processes, setProcesses] = useState([]);
  const [simulationResult, setSimulationResult] = useState(null);
  const [quizMode, setQuizMode] = useState(false);
  const [quizData, setQuizData] = useState(null);
  const [quizResult, setQuizResult] = useState(null);
  const [isLoadingQuiz, setIsLoadingQuiz] = useState(false);

  const startQuiz = async () => {
    setIsLoadingQuiz(true);
    try {
      const data = await generateQuiz();
      setQuizData(data);
      setQuizResult(null);
      setQuizMode(true);
      setSimulationResult(null);
    } catch (error) {
      console.error('Failed to generate quiz:', error);
    } finally {
      setIsLoadingQuiz(false);
    }
  };

  const handleQuizSubmit = async (answers) => {
    try {
      const result = await submitQuizAnswers(
        quizData.quizId,
        answers.contextSwitches,
        answers.avgWaitTime,
        answers.avgTurnaroundTime
      );
      setQuizResult(result);
    } catch (error) {
      console.error('Failed to submit quiz:', error);
    }
  };

  const handleQuizAgain = () => {
    startQuiz();
  };

  const exitQuiz = () => {
    setQuizMode(false);
    setQuizData(null);
    setQuizResult(null);
  };

  return (
    <div className="App">
      <div className="header-container">
        <h1>CPU Scheduling Simulator</h1>
        {!quizMode ? (
          <button
            className="quiz-mode-btn"
            onClick={startQuiz}
            disabled={isLoadingQuiz}
          >
            {isLoadingQuiz ? (
              <span className="spinner"></span>
            ) : (
              'Quiz Me'
            )}
          </button>
        ) : (
          <button className="exit-quiz-btn" onClick={exitQuiz}>
            Exit Quiz
          </button>
        )}
      </div>

      {!quizMode ? (
        <>
          <ProcessTable onProcessesChange={setProcesses} />
          <AlgorithmList
            processes={processes}
            onSimulationComplete={setSimulationResult}
          />
          <SimulationTimeline result={simulationResult} />
        </>
      ) : (
        <>
          {!quizResult && quizData && (
            <Quiz quizData={quizData} onSubmit={handleQuizSubmit} />
          )}
          {quizResult && (
            <QuizResults quizResult={quizResult} onQuizAgain={handleQuizAgain} />
          )}
        </>
      )}
      <Footer className="footer" />
    </div>
  );
}

export default App;