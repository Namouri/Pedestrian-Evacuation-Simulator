/*package Components;
import Components.Agent;
import java.util.List;


public class Movement {
    public static void moveAgentTowardsNearestExit(Agent agent, List<Exit> exits, List<Obstacle> obstacles, int cols, int rows) {
        Exit closestExit = null;
        double minDistance = Double.MAX_VALUE;

        int agentX = agent.getX();
        int agentY = agent.getY();

        // 1. Find nearest exit
        for (Exit exit : exits) {
            double dist = Math.hypot(exit.getX() - agentX, exit.getY() - agentY);
            if (dist < minDistance) {
                minDistance = dist;
                closestExit = exit;
            }
        }

        if (closestExit == null) return;

        // 2. Try moving towards it
        int[][] directions = {
                {Integer.compare(closestExit.getX(), agentX), Integer.compare(closestExit.getY(), agentY)}, // primary
                {1, 0}, {-1, 0}, {0, 1}, {0, -1} // fallback directions
        };

        for (int[] dir : directions) {
            int newX = agentX + dir[0];
            int newY = agentY + dir[1];

            boolean isExit = exits.stream().anyMatch(e -> e.getX() == newX && e.getY() == newY);
            boolean isInsideGreen = newX > 0 && newX < cols - 1 && newY > 0 && newY < rows - 1;
            boolean isBlocked = obstacles.stream().anyMatch(o -> o.getX() == newX && o.getY() == newY);

            if ((isInsideGreen && !isBlocked) || isExit) {
                agent.setX(newX);
                agent.setY(newY);
                agent.updateVisualPosition();
                return;
            }
        }
    }
}



*/





