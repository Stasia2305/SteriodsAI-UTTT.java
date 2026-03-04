package dk.easv.tictactoe.gui;

import javafx.application.Application;

/**
 * Separate launcher class to avoid the "JavaFX runtime components are missing" error
 * when running directly from some IDE configurations.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(TicTacToe.class, args);
    }
}
