package Components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.*;

public class MapM extends VBox {

    private GridPane mapGrid;
    private List<Agent> agents;
    private final List<Exit> exits = new ArrayList<>();
    private final List<Obstacle> obstacles = new ArrayList<>();

    public MapM(String floorName, int rows, int cols) {
        agents = new ArrayList<>();
        Label floorLabel = new Label(floorName);
        floorLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        mapGrid = new GridPane();
        mapGrid.setAlignment(Pos.CENTER);
        mapGrid.setHgap(0);
        mapGrid.setVgap(0);
        mapGrid.setPadding(new Insets(0));

        Set<String> occupied = new HashSet<>();
        Set<String> exitPositions = new HashSet<>();


        // 1. Placera 4 dörrar (5 block breda) i mitten av varje vägg
        int doorSize = 5;
        int midCol = cols / 2;
        int midRow = rows / 2;

        // Toppvägg (rad 0)
        for (int i = -doorSize / 2; i <= doorSize / 2; i++) {
            int x = midCol + i;
            if (x >= 0 && x < cols) {
                exits.add(new Exit(x, 0));
                exitPositions.add(x + "," + 0);
                occupied.add(x + "," + 0);
            }
        }

        // Bottenvägg (rad rows - 1)
        for (int i = -doorSize / 2; i <= doorSize / 2; i++) {
            int x = midCol + i;
            if (x >= 0 && x < cols) {
                exits.add(new Exit(x, rows - 1));
                exitPositions.add(x + "," + (rows - 1));
                occupied.add(x + "," + (rows - 1));
            }
        }

        // Vänster vägg (kolumn 0)
        for (int i = -doorSize / 2; i <= doorSize / 2; i++) {
            int y = midRow + i;
            if (y >= 0 && y < rows) {
                exits.add(new Exit(0, y));
                exitPositions.add(0 + "," + y);
                occupied.add(0 + "," + y);
            }
        }

        // Höger vägg (kolumn cols - 1)
        for (int i = -doorSize / 2; i <= doorSize / 2; i++) {
            int y = midRow + i;
            if (y >= 0 && y < rows) {
                exits.add(new Exit(cols - 1, y));
                exitPositions.add((cols - 1) + "," + y);
                occupied.add((cols - 1) + "," + y);
            }
        }
        Set<String> forbiddenForObstacles = new HashSet<>(exitPositions);

        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (String pos : exitPositions) {
            String[] parts = pos.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);

            for (int[] d : dirs) {
                int nx = x + d[0];
                int ny = y + d[1];
                forbiddenForObstacles.add(nx + "," + ny);
            }
        }


        String[] types = {
                "L", "square", "longH", "longV", "T", "zigzag", "plus",
                "U", "S", "corner", "wall3x1", "wall1x3", "hook", "pillar", "staggered"
        };


        Random rand = new Random();
        int maxAttempts = 1000;
        int maxBlocks = 150;
        int placed = 0;

        while (placed < maxBlocks && maxAttempts-- > 0) {
            int x = rand.nextInt(cols - 6) + 3;
            int y = rand.nextInt(rows - 6) + 3;
            String type = types[rand.nextInt(types.length)];
            List<int[]> shape = generateBlock(type);

            // Kolla om blocket får plats
            boolean canPlace = true;
            List<String> keys = new ArrayList<>();

            for (int[] offset : shape) {
                int px = x + offset[0];
                int py = y + offset[1];
                String key = px + "," + py;

                if (px <= 0 || px >= cols - 1 || py <= 0 || py >= rows - 1 ||
                        occupied.contains(key) || forbiddenForObstacles.contains(key)) {
                    canPlace = false;
                    break;
                }

                keys.add(key);
            }

            if (!canPlace) continue;

            // Temporärt placera blocket
            for (String key : keys) {
                String[] parts = key.split(",");
                obstacles.add(new Obstacle(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
                occupied.add(key);
            }

            // Kontrollera om kartan fortfarande är genomtränglig
            if (isMapReachable(rows, cols, occupied, exits)) {
                placed++;
            } else {
                // Återställ om det blev blockering
                for (String key : keys) {
                    occupied.remove(key);
                    String[] parts = key.split(",");
                    obstacles.removeIf(o -> o.getX() == Integer.parseInt(parts[0]) && o.getY() == Integer.parseInt(parts[1]));
                }
            }
        }




        // 3. Bygg grid och färglägg
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                String key = col + "," + row;
                Rectangle cell = new Rectangle(10, 10);
                cell.setStroke(Color.WHITE);

                if (exitPositions.contains(key)) {
                    cell.setFill(Color.RED);
                } else if (occupied.contains(key)) {
                    cell.setFill(Color.BLACK);
                } else if (row == 0 || row == rows - 1 || col == 0 || col == cols - 1) {
                    cell.setFill(Color.DARKGRAY);
                    if (!exitPositions.contains(key)) {
                        obstacles.add(new Obstacle(col, row));
                        occupied.add(key);
                    }
                } else {
                    cell.setFill(Color.LIGHTGREEN);
                }

                mapGrid.add(cell, col, row);
            }
        }

        this.setSpacing(10);
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(floorLabel, mapGrid);
    }

    public GridPane getMapGrid() {
        return mapGrid;
    }

    public List<Exit> getExits() {
        return exits;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public List<Agent> getAgents() {
        return agents;
    }

//ADDING AGENTS ON MAP AND PREVENTING THEM FROM BEING ON OBSTCALES AND ARENA WALLS
    public void generateAgents(int numAgents) {
        Random rand = new Random();
        int rows = mapGrid.getRowCount();
        int cols = mapGrid.getColumnCount();

        Set<String> blocked = new HashSet<>();

        for (Obstacle o : obstacles) {
            blocked.add(o.getX() + "," + o.getY());
        }
        for (Exit e : exits) {
            blocked.add(e.getX() + "," + e.getY());
        }

        Set<String> occupied = new HashSet<>(blocked);

        int attempts = 0;
        int placed = 0;

        while (placed < numAgents && attempts < numAgents * 10) {
            int x = rand.nextInt(cols - 2) + 1; // 1 to cols-2
            int y = rand.nextInt(rows - 2) + 1; // 1 to rows-2

            String key = x + "," + y;

            if (!occupied.contains(key)) {
                Agent agent = new Agent(x, y);
                agents.add(agent);
                mapGrid.add(agent.getVisual(), x, y);
                occupied.add(key);
                placed++;
            }

            attempts++;
        }

        System.out.println("Agents placed: " + placed + " / " + numAgents);

    }
    private List<int[]> generateBlock(String type) {
        List<int[]> positions = new ArrayList<>();

        switch (type) {
            case "L":
                positions.add(new int[]{0, 0});
                positions.add(new int[]{0, 1});
                positions.add(new int[]{0, 2});
                positions.add(new int[]{1, 2});
                break;

            case "square":
                positions.add(new int[]{0, 0});
                positions.add(new int[]{1, 0});
                positions.add(new int[]{0, 1});
                positions.add(new int[]{1, 1});
                break;

            case "longH":
                for (int i = 0; i < 5; i++) positions.add(new int[]{i, 0});
                break;

            case "longV":
                for (int i = 0; i < 5; i++) positions.add(new int[]{0, i});
                break;

            case "T":
                for (int i = -1; i <= 1; i++) positions.add(new int[]{i, 0});
                positions.add(new int[]{0, 1});
                break;

            case "zigzag":
                positions.add(new int[]{0, 0});
                positions.add(new int[]{1, 0});
                positions.add(new int[]{1, 1});
                positions.add(new int[]{2, 1});
                break;

            case "plus":
                positions.add(new int[]{0, 1});
                positions.add(new int[]{1, 0});
                positions.add(new int[]{1, 1});
                positions.add(new int[]{1, 2});
                positions.add(new int[]{2, 1});
                break;
            case "U":
                positions.add(new int[]{0, 0});
                positions.add(new int[]{0, 1});
                positions.add(new int[]{1, 0});
                positions.add(new int[]{2, 0});
                positions.add(new int[]{2, 1});
                break;

            case "S":
                positions.add(new int[]{1, 0});
                positions.add(new int[]{2, 0});
                positions.add(new int[]{0, 1});
                positions.add(new int[]{1, 1});
                break;

            case "corner":
                positions.add(new int[]{0, 0});
                positions.add(new int[]{1, 0});
                positions.add(new int[]{0, 1});
                break;

            case "wall3x1":
                for (int i = 0; i < 3; i++) {
                    positions.add(new int[]{i, 0});
                }
                break;

            case "wall1x3":
                for (int i = 0; i < 3; i++) {
                    positions.add(new int[]{0, i});
                }
                break;

            case "hook":
                positions.add(new int[]{0, 0});
                positions.add(new int[]{0, 1});
                positions.add(new int[]{0, 2});
                positions.add(new int[]{1, 2});
                positions.add(new int[]{2, 2});
                break;

            case "pillar":
                positions.add(new int[]{0, 0});
                positions.add(new int[]{1, 0});
                positions.add(new int[]{2, 0});
                positions.add(new int[]{1, 1});
                positions.add(new int[]{1, -1});
                break;

            case "staggered":
                positions.add(new int[]{0, 0});
                positions.add(new int[]{1, 1});
                positions.add(new int[]{2, 2});
                break;

            // Lägg till fler former här
        }

        return positions;
    }

    private boolean isMapReachable(int rows, int cols, Set<String> occupied, List<Exit> exits) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        for (Exit exit : exits) {
            String start = exit.getX() + "," + exit.getY();
            queue.add(start);
            visited.add(start);
        }

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

        while (!queue.isEmpty()) {
            String current = queue.poll();
            String[] parts = current.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);

            for (int[] d : dirs) {
                int nx = x + d[0];
                int ny = y + d[1];
                String neighbor = nx + "," + ny;

                if (nx >= 0 && nx < cols && ny >= 0 && ny < rows && !occupied.contains(neighbor) && !visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        // Kolla att alla inre rutor är nåbara
        for (int y = 1; y < rows - 1; y++) {
            for (int x = 1; x < cols - 1; x++) {
                String key = x + "," + y;
                if (!occupied.contains(key) && !visited.contains(key)) {
                    return false; // blockerad cell utan väg
                }
            }
        }

        return true;
    }


}




