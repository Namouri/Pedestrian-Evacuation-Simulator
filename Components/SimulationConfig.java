package Components;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SimulationConfig {
    private int numRows; // heigth of the map in rows
    private int numCols; // width of the map in colums
    private int numAgents; // number of agents
    private double timeStep; // how often it should update the simulation

    //constructor if we know values
    public SimulationConfig(int numRows, int numCols, int numAgents, double timeStep) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.numAgents = numAgents;
        this.timeStep = timeStep;
    }

    //static constructor to read from file
    public static SimulationConfig loadFromFile(String filename) {
        int numRows = 20;
        int numCols = 20;
        int numAgents = 100;
        double timeStep = 1.0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))){
            String line;

            while ((line = reader.readLine()) != null ){
                String[] parts = line.split("=");
                if (parts.length < 2) continue;

                String key = parts[0].trim();
                String value = parts[1].trim();

                switch (key) {
                    case "numRows":
                        numRows = Integer.parseInt(value);
                        break;
                    case "numCols":
                        numCols = Integer.parseInt(value);
                        break;
                    case "numAgents":
                        numAgents = Integer.parseInt(value);
                        break;
                    case "timeStep":
                        timeStep = Double.parseDouble(value);
                        break;

                }
            }
        } catch (IOException e) {
            System.err.println("Error reading config file: " + e.getMessage());
        }

        return new SimulationConfig(numRows, numCols, numAgents, timeStep);
    }

    //Getters

    public int getNumRows(){
        return numRows;
    }

    public int getNumCols(){
        return numCols;
    }

    public int getAgentCount(){
        return numAgents;
    }

    public double getTimeStep(){
        return timeStep;
    }
}
