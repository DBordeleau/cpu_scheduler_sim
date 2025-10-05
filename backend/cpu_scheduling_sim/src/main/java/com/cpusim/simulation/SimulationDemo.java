package com.cpusim.simulation;

import com.cpusim.model.SimulationResult;

public class SimulationDemo {

    public static void main(String[] args) {
        SimulationEngine engine = new SimulationEngine();

        // Add some test processes
        System.out.println("=== Adding Processes ===");
        engine.addProcess(1, 0, 8, 2);
        engine.addProcess(2, 1, 4, 1);
        engine.addProcess(3, 2, 9, 3);
        engine.addProcess(4, 3, 5, 2);

        System.out.println("Added " + engine.getProcesses().size() + " processes\n");

        // Test all algorithms
        String[] algorithms = { "FCFS", "SJF", "SRTF", "PP", "RR" };

        for (String algo : algorithms) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("Algorithm: " + algo);
            System.out.println("=".repeat(60));

            try {
                SimulationResult result;
                if (algo.equals("RR")) {
                    result = engine.runSimulation(algo, 3); // quantum = 3
                } else {
                    result = engine.runSimulation(algo);
                }

                System.out.println(engine.formatResults(result));

            } catch (Exception e) {
                System.out.println("Error running " + algo + ": " + e.getMessage());
            }
        }
    }
}