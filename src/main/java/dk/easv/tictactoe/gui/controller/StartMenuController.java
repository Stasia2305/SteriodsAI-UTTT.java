package dk.easv.tictactoe.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.media.AudioClip;

import java.io.IOException;
import java.util.Objects;

public class StartMenuController {

    public AudioClip clickSound;

    @FXML
    public void initialize() {
        clickSound = new AudioClip(Objects.requireNonNull(getClass().getResource("/sounds/click.wav")).toString());
    }

    @FXML
    private void onTwoPlayers(ActionEvent e) {
        try { openGame(e, "two"); } catch (IOException ex) { showError(ex); }
        clickSound.play();
    }

    @FXML
    private void onOnePlayerSteroids(ActionEvent e) {
        clickSound.play();
        try { openGame(e, "steroids"); } catch (IOException ex) { showError(ex); }
    }

    private void openGame(ActionEvent e, String mode) throws IOException {
        java.net.URL url = getClass().getResource("/fxml/game_view.fxml");
        if (url == null) {
            throw new IOException("Resource not found: /fxml/game_view.fxml");
        }
        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();
        GameViewController ctrl = loader.getController();
        ctrl.setMode(mode);
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Navigation error");
        alert.setHeaderText("Couldn't open the game view");
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        ex.printStackTrace(pw);
        String details = sw.toString();
        alert.setContentText(ex.getClass().getSimpleName() + ": " + (ex.getMessage()==null?"(no message)":ex.getMessage()));
        javafx.scene.control.TextArea ta = new javafx.scene.control.TextArea(details);
        ta.setEditable(false);
        ta.setWrapText(false);
        ta.setMaxWidth(Double.MAX_VALUE);
        ta.setMaxHeight(Double.MAX_VALUE);
        javafx.scene.layout.GridPane.setVgrow(ta, javafx.scene.layout.Priority.ALWAYS);
        javafx.scene.layout.GridPane.setHgrow(ta, javafx.scene.layout.Priority.ALWAYS);
        javafx.scene.layout.GridPane exp = new javafx.scene.layout.GridPane();
        exp.add(new javafx.scene.control.Label("Stack trace:"), 0, 0);
        exp.add(ta, 0, 1);
        alert.getDialogPane().setExpandableContent(exp);
        alert.showAndWait();
    }
}
