package Components;
import java.util.*;
import Components.*;

public class PathFinder {
    private final List<Exit> exits;
    private final List<Agent> agents;
    private final List<Obstacle> obstacles;
    private final int rows;
    private final int cols;

    public PathFinder(List<Exit> exits, List<Agent> agents, List<Obstacle> obstacles, int rows, int cols) {
        this.exits = exits;
        this.agents = agents;
        this.obstacles = obstacles;
        this.rows = rows;
        this.cols = cols;
    }

    public List<int[]> nearestExit(int startCol, int startRow) {
        boolean[][] visited = new boolean[cols][rows];
        int[][][] parents = new int[cols][rows][2];
        Queue<int[]> queue = new LinkedList<>();
        Set<String> exitPositions = new HashSet<>();

        for (Exit exit : exits) {
            exitPositions.add(exit.getX() + "," + exit.getY());
        }

        queue.offer(new int[]{startCol, startRow});
        visited[startCol][startRow] = true;

        int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int col = current[0];
            int row = current[1];

            if (exitPositions.contains(col + "," + row)) {
                List<int[]> path = new ArrayList<>();
                while (col != startCol || row != startRow) {
                    path.add(0, new int[]{col, row});
                    int parentCol = parents[col][row][0];
                    int parentRow = parents[col][row][1];
                    col = parentCol;
                    row = parentRow;
                }
                return path;
            }
            for (int[] direction : DIRECTIONS) {
                int neighborCol = col + direction[0];
                int neighborRow = row + direction[1];
                String key = neighborCol + "," + neighborRow;

                if (neighborCol >= 0 && neighborRow >= 0 && neighborCol < cols && neighborRow < rows && !visited[neighborCol][neighborRow] && !isObstacle(neighborCol, neighborRow) && isCellFree(neighborCol, neighborRow)) {
                    visited[neighborCol][neighborRow] = true;
                    parents[neighborCol][neighborRow][0] = col;
                    parents[neighborCol][neighborRow][1] = row;
                    queue.offer(new int[]{neighborCol, neighborRow});
                }
            }
        }
        return new ArrayList<>();
    }

    private boolean isObstacle(int col, int row) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.getX() == col && obstacle.getY() == row) {
                return true;
            }
        }
        return false;
    }

    private boolean isCellFree(int col, int row) {
        for (Agent agent : agents) {
            if (agent.getX() == col && agent.getY() == row) {
                return false;
            }
        }
        return true;
    }
}
