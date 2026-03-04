package dk.easv.tictactoe.gui.controller;

import dk.easv.tictactoe.bll.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.media.AudioClip;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class GameViewController {

    @FXML private GridPane gridPane;
    @FXML private Label statusLabel;
    @FXML private Label overlayLabel;
    private GameBoard board;
    private String mode = "two";
    private AIPlayer ai;
    public AudioClip clickSound;

    public void setMode(String mode) {
        this.mode = mode;
        if (mode.equals("steroids")) ai = new SteroidsAI();
        else ai = null;
    }

    @FXML
    private void initialize() {
        board = new GameBoard();
        drawBoard();
        if (overlayLabel != null) { overlayLabel.setVisible(false); overlayLabel.setManaged(false); }
        updateStatus();
        try {
            clickSound = new AudioClip(Objects.requireNonNull(getClass().getResource("/sounds/click.wav")).toString());
        } catch (Exception ignored) {}
    }

    private void drawBoard() {
        gridPane.getChildren().clear();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                Button btn = new Button();
                btn.setPrefSize(55, 55);
                btn.getStyleClass().add("game-button");
                btn.setFocusTraversable(false);
                final int cc = c, rr = r;
                btn.setOnAction(e -> handleClick(cc, rr));
                gridPane.add(btn, c, r);
            }
        }
        updateButtons();
    }

    private void handleClick(int c, int r) {
        if (board.isGameOver()) return;
        boolean ok = board.play(c, r);
        if (!ok) return;
        if (clickSound != null) clickSound.play();
        updateButtons();
        if (board.isGameOver()) onGameOver();
        else {
            if (ai != null && board.getNextPlayer() != 0) {
                runAIMove();
            }
        }
        updateStatus();
    }

    private void runAIMove() {
        CompletableFuture.runAsync(() -> {
            int aiPlayer = board.getNextPlayer();
            int[] move = ai.chooseMove(board, aiPlayer);
            if (move == null) return;
            Platform.runLater(() -> {
                board.play(move[0], move[1]);
                if (clickSound != null) clickSound.play();
                updateButtons();
                if (board.isGameOver()) onGameOver();
                updateStatus();
            });
        });
    }

    private void updateButtons() {
        int[] active = board.getActiveSmallBoard();
        for (Node n : gridPane.getChildren()) {
            Button b = (Button) n;
            Integer col = GridPane.getColumnIndex(b);
            Integer row = GridPane.getRowIndex(b);
            int c = col == null ? 0 : col;
            int r = row == null ? 0 : row;
            int v = board.getField(c, r);

            if (v == 0) b.setText("X");
            else if (v == 1) b.setText("O");
            else b.setText("");

            int sc = c / 3;
            int sr = r / 3;
            int sw = board.getSmallBoardWinner(sc, sr);

            boolean isActive = (active == null && sw == -1) || (active != null && sc == active[0] && sr == active[1]);
            b.setDisable(board.isGameOver() || v != -1 || !isActive || sw != -1);

            // Base Style
            String style = "-fx-background-radius: 0; ";
            
            // Background based on small board winner
            if (sw == 0) style += "-fx-background-color: #ffaaaa; "; // Red-ish for player 0
            else if (sw == 1) style += "-fx-background-color: #aaaaff; "; // Blue-ish for player 1
            else if (sw == -2) style += "-fx-background-color: #cccccc; "; // Grey for draw
            else if (!isActive) style += "-fx-background-color: #eeeeee; -fx-opacity: 0.5; "; // Dim inactive
            else style += "-fx-background-color: white; ";

            // Borders for small boards
            String borderStyle = "-fx-border-color: #888888; -fx-border-width: 0.5; ";
            if (c % 3 == 0) borderStyle += "-fx-border-insets: 0 0 0 2; -fx-border-color: black #888888 #888888 black; -fx-border-width: 0.5 0.5 0.5 2; ";
            if (r % 3 == 0) borderStyle += "-fx-border-insets: 2 0 0 0; -fx-border-color: black #888888 #888888 black; -fx-border-width: 2 0.5 0.5 0.5; ";
            if (c == 8) borderStyle += "-fx-border-width: 0.5 2 0.5 0.5; -fx-border-color: #888888 black #888888 #888888; ";
            if (r == 8) borderStyle += "-fx-border-width: 0.5 0.5 2 0.5; -fx-border-color: #888888 #888888 black #888888; ";

            // Highlight active board strongly
            if (active != null && sc == active[0] && sr == active[1]) {
                style += "-fx-border-color: #ffd700; -fx-border-width: 3; -fx-border-insets: 1; ";
            } else if (active == null && sw == -1 && !board.isGameOver()) {
                style += "-fx-border-color: #90ee90; -fx-border-width: 2; ";
            }

            b.setStyle(style + borderStyle);
            
            // Text color
            if (v == 0) b.setStyle(b.getStyle() + "-fx-text-fill: darkred;");
            else if (v == 1) b.setStyle(b.getStyle() + "-fx-text-fill: darkblue;");
        }
    }

    private void onGameOver() {
        int w = board.getWinner();
        String result;
        if (w == -1) result = "Draw!";
        else result = "Player " + (w == 0 ? "X" : "O") + " wins!";

        statusLabel.setText(result);
        if (overlayLabel != null) {
            overlayLabel.setText(result);
            overlayLabel.setManaged(true);
            overlayLabel.setVisible(true);
            overlayLabel.setStyle("-fx-background-color: rgba(255,255,255,0.8); -fx-font-size: 40px; -fx-font-weight: bold; -fx-padding: 20; -fx-border-color: black; -fx-border-width: 5;");
        }
    }

    @FXML
    private void onNewGame() {
        board.newGame();
        if (overlayLabel != null) {
            overlayLabel.setVisible(false);
            overlayLabel.setManaged(false);
            overlayLabel.setText("");
        }
        updateButtons();
        updateStatus();
    }

    @FXML
    private void onBack() throws IOException {
        if (clickSound != null) clickSound.play();
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/start_menu.fxml"));
        javafx.scene.Parent root = loader.load();
        javafx.stage.Stage stage = (javafx.stage.Stage) gridPane.getScene().getWindow();
        stage.setScene(new javafx.scene.Scene(root));
    }

    private void updateStatus() {
        statusLabel.setText("Next: " + (board.getNextPlayer() == 0 ? "X" : "O"));
    }
}
