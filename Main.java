import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Components.SimulatorUI; 


public class Main extends Application {
    private final StringProperty totalAgents = new SimpleStringProperty("0");
    private final StringProperty initTime = new SimpleStringProperty("00:00");
    private final StringProperty evacuatedCount = new SimpleStringProperty("0");

    @Override
    public void start(Stage primaryStage) {
        SimulatorUI simulatorUI = new SimulatorUI(totalAgents, evacuatedCount, initTime);
        Scene scene = simulatorUI.buildScene(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Evacuation Plan Simulator");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


