
package Components;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;

public class Agent {
    private int x, y;
    private int startX, startY;
    private Circle visual;
    private static final int TILE_SIZE = 20;

    public Agent(int x, int y) {
        this.x = x;
        this.y = y;
        this.startX = x;
        this.startY = y;
        this.visual = new Circle(3.5, Color.BLUE);
        this.visual.setStroke(Color.BLACK);
        this.visual.setStrokeWidth(1.5);

    }

    public void update(PathFinder pathFinder) {
        List<int[]> path = pathFinder.nearestExit(this.x, this.y);

        if (path != null && !path.isEmpty()) {
            int[] nextStep = path.get(0);

            this.x = nextStep[0];
            this.y = nextStep[1];

            GridPane.setColumnIndex(visual, x);
            GridPane.setRowIndex(visual, y);
        }
    }

    public boolean hasExited(List<Exit> exits) {
        for (Exit exit : exits) {
            if (this.x == exit.getX() && this.y == exit.getY()) {
                return true;
            }
        }
        return false;
    }

    public void resPos(){
        this.x = this.startX;
        this.y = this.startY;

        GridPane.setColumnIndex(visual, x);
        GridPane.setRowIndex(visual, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Circle getVisual() {
        return visual;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

