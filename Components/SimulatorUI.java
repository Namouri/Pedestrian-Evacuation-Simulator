package Components; // optional package

import javafx.animation.PauseTransition;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Duration;

public class SimulatorUI {
    private SimulationEngine engine;
    private MapM map;
    private final StringProperty totalAgents;
    private final StringProperty evacuatedCount;
    private final StringProperty initTime;
    private int lastEvacuatedCount = -1;

    private final BorderPane root = new BorderPane();
    private final Statistics stats = new Statistics();

    public SimulatorUI(StringProperty totalAgents, StringProperty evacuatedCount, StringProperty initTime) {
        this.totalAgents = totalAgents;
        this.evacuatedCount = evacuatedCount;
        this.initTime = initTime;
    }

    public Scene buildScene(Stage stage) {
        // Buttons
        Button startButton = new Button("Start");
        Button pauseButton = new Button("Pause");
        Button stopButton = new Button("Stop");
        Button resetButton = new Button("Reset");

        String buttonStyle = "-fx-font-size: 20px; -fx-padding: 15px;";
        startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;" + buttonStyle);
        pauseButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black;" + buttonStyle);
        stopButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;" + buttonStyle);
        resetButton.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white;" + buttonStyle);

        HBox buttonBox = new HBox(20, startButton, pauseButton, stopButton, resetButton);
        buttonBox.setAlignment(Pos.TOP_LEFT);
        buttonBox.setPadding(new Insets(20));

        // Map
        map = new MapM("Arena", 40, 110);
        HBox mapBox = new HBox(map);
        mapBox.setAlignment(Pos.CENTER);
        mapBox.setPadding(new Insets(20));

        // Stats menu
        MenuBar menuBar = new MenuBar();
        Menu statsMenu = new Menu();
        statsMenu.textProperty().bind(
                new SimpleStringProperty("Timer: ").concat(initTime)
                        .concat("   |   Evacuated agents: ")
                        .concat(evacuatedCount).concat(" / ").concat(totalAgents)
        );
        statsMenu.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        menuBar.getMenus().add(statsMenu);
        menuBar.setStyle("-fx-pref-height: 60px;");

        // Layout
        root.setTop(menuBar);
        root.setCenter(mapBox);
        root.setBottom(buttonBox);

        // Agent count input
        InputDialog inputDialog = new InputDialog(totalAgents);
        inputDialog.showAndGetAgentNum(stage).ifPresent(agentNum -> {
            map.generateAgents(agentNum);
            setupEngine();
        });

        // Button actions
        startButton.setOnAction(e -> {engine.start(); startButton.setDisable(true);});

        pauseButton.setOnAction(e -> {
            if (engine.isRunning()) {
                engine.pause();
                pauseButton.setText("Resume");
                stopButton.setDisable(true);
                resetButton.setDisable(true);
            } else {
                engine.start();
                pauseButton.setText("Pause");
                stopButton.setDisable(false);
                resetButton.setDisable(false);
            }

        });

        resetButton.setOnAction(e -> {

            startButton.setDisable(false);
            engine.reset();
            initTime.set("00:00");
            evacuatedCount.set("0");
            pauseButton.setText("Pause");
        });

        stopButton.setOnAction(e -> {
            engine.stop();
            pauseButton.setText("Pause");
            pauseButton.setDisable(true);
            startButton.setDisable(true);
            resetButton.setDisable(true);
            stats.writeToFile("Evacuation_stat.txt");
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(event -> stage.close());
            delay.play();
        });

        return new Scene(root, 1200, 800);
    }

    private void setupEngine() {
        engine = new SimulationEngine(
                map.getAgents(),
                map,
                0.5,
                map.getExits()
        );

        engine.setOnStep(() -> {
            int timeElapsed = (int) engine.getTimeElapsed();
            int currentCount = engine.countExitedAgents().size();
            if (currentCount != lastEvacuatedCount) {
                evacuatedCount.set(String.valueOf(currentCount));
                lastEvacuatedCount = currentCount;
            }
            initTime.set(String.format("%02d:%02d",
                    (int) engine.getTimeElapsed() / 60,
                    (int) engine.getTimeElapsed() % 60));
            stats.setEvacuatedAgents(engine.countExitedAgents());
            stats.setSimulationTimeSeconds(timeElapsed);

        });
    }
}

