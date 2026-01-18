package Components;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Statistics {
    private int exitedAgents;
    private int simulationTimeSeconds = 0;

    public void setEvacuatedAgents(List<Agent> count) {
        this.exitedAgents = count.size();
    }

    public void setSimulationTimeSeconds(int seconds) {
        this.simulationTimeSeconds = seconds;
    }

    public void writeToFile(String Result) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Result))) {
            writer.write("Evacuation Statistics\n");
            writer.write("Evacuated Agents: " + exitedAgents + "\n");
            writer.write("Total Time: " + formatTime(simulationTimeSeconds) + "\n");
        } catch (IOException e) {
            System.err.println("Error writing statistics: " + e.getMessage());
        }
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
