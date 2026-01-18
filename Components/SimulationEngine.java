package Components;
//.
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.application.Platform;

public class SimulationEngine {
    private boolean running = false;
    private double timeElapsed = 0.0;
    private final double timeStep; // e.g., 0.1 seconds per step
    private PathFinder pathFinder;
    private List<Agent> exitedAgents = new ArrayList<>();


    private final List<Agent> agents;
    private final MapM map;
    private final List<Exit> exits;

    private Runnable onStep;


    public SimulationEngine(List<Agent> agents, MapM map, double timeStep, List<Exit> exits) {
        this.agents = agents;
        this.map = map;
        this.timeStep = timeStep;
        this.exits = exits;


        this.pathFinder = new PathFinder(
                exits,
                agents,
                map.getObstacles(),
                map.getMapGrid().getRowCount(),
                map.getMapGrid().getColumnCount()
        );
    }

    public void setOnStep(Runnable onStep) {
        this.onStep = onStep;
    }

    public void start() {
        running = true;
        new Thread(this::runLoop).start();
    }

    public void stop() {
        running = false;
    }

    public void pause() {

        running = false;
    }

    public void resume() {

        running = true;
    }

    public void reset() {
        timeElapsed = 0.0;
        running = false;

        // Reset current agents
        for (Agent agent : agents) {
            agent.resPos();
            Platform.runLater(() -> {
                if (!map.getMapGrid().getChildren().contains(agent.getVisual())) {
                    map.getMapGrid().getChildren().add(agent.getVisual());
                }
            });
        }

        // Restore exited agents
        for (Agent exited : exitedAgents) {
            exited.resPos();
            agents.add(exited); // Now it's safe to add them
            Platform.runLater(() -> {
                if (!map.getMapGrid().getChildren().contains(exited.getVisual())) {
                    map.getMapGrid().getChildren().add(exited.getVisual());
                }
            });
        }

        exitedAgents.clear();

        // Re-initialize the pathfinder with updated agents list
        pathFinder = new PathFinder(
                exits,
                agents,
                map.getObstacles(),
                map.getMapGrid().getRowCount(),
                map.getMapGrid().getColumnCount()
        );
    }


    private void runLoop() {
        double lastDisplayedTime = 0.0;
        while (running ) {
            updateAgents();
            timeElapsed += timeStep;

            if (onStep != null && (int) timeElapsed != (int) lastDisplayedTime) {
                lastDisplayedTime = timeElapsed;
                Platform.runLater(onStep);
            }
            if (allAgentsExited()){
                if (onStep != null) {
                    Platform.runLater(onStep);
                }
                break;

            }

            try {
                Thread.sleep((long) (timeStep * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        running = false;
        System.out.println("Simulation finished in " + timeElapsed + " seconds.");
    }

    private void updateAgents() {
        Iterator<Agent> iterator = agents.iterator();

        while (iterator.hasNext()) {
            Agent agent = iterator.next();


            if (!agent.hasExited(exits)) {
                agent.update(pathFinder);
            }

            if (agent.hasExited(exits)) {
                exitedAgents.add(agent);
                Platform.runLater(() -> {
                    map.getMapGrid().getChildren().remove(agent.getVisual());


                });
                iterator.remove();
            }
        }

}
    private boolean allAgentsExited() {
        return agents.stream().allMatch(agent -> agent.hasExited(exits));
    }

    public List<Agent> countExitedAgents() {

        return exitedAgents;
    }

    public double getTimeElapsed() {
        return timeElapsed;
    }

    public boolean isRunning() {
        return running;
    }
}

