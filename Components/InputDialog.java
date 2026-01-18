package Components;


import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Modality;

import java.util.Optional;

public class InputDialog {

    private StringProperty totalAgents;

    public InputDialog(StringProperty totalAgents) {
        this.totalAgents = totalAgents;
    }
    public Optional<Integer> showAndGetAgentNum(Stage owner) {
        // Create dialog stage
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Number of agents");
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.WINDOW_MODAL);

        Label label = new Label("Enter number of agents here (1 to 1000):");
        TextField agentNumField = new TextField();
        agentNumField.setPromptText("Enter number here");
        agentNumField.setPrefWidth(200);

        Button okButton = new Button("OK");

        VBox vbox = new VBox(20, label, agentNumField, okButton);
        vbox.setPadding(new Insets(20));
        Scene scene = new Scene(vbox, 300, 150);
        dialogStage.setScene(scene);

        final Integer[] result = new Integer[1];  // to store input

        okButton.setOnAction(e -> {
            String input = agentNumField.getText();
            try {
                int agentNum = Integer.parseInt(input);
                totalAgents.set(Integer.toString(agentNum));
                if (agentNum < 1 || agentNum > 1000) {
                    showAlert("Please enter a number between 1 and 1000.");
                } else {
                    result[0] = agentNum;
                    dialogStage.close();
                }
            } catch (NumberFormatException ex) {
                showAlert("Invalid input. Please enter a valid number.");
            }
        });

        dialogStage.showAndWait();

        return Optional.ofNullable(result[0]);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
