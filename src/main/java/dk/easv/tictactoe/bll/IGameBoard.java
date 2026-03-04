package dk.easv.tictactoe.bll;

import java.util.List;

/**
 * Interface for Ultimate Tic-Tac-Toe.
 * @author EASV
 */
public interface IGameBoard
{
    /**
     * Returns 0 for player 0, 1 for player 1.
     *
     * @return int Id of the next player.
     */
    int getNextPlayer();

    /**
     * Attempts to let the current player play at the given coordinates (0-8).
     *
     * @param col column (0-8).
     * @param row row (0-8).
     * @return true if the move is accepted.
     */
    boolean play(int col, int row);

    /**
     * Gets the value of the field (0-8).
     * -1 if empty, 0 for player 0, 1 for player 1.
     */
    int getField(int col, int row);

    /**
     * Gets winner of a small 3x3 board (0-2).
     * -1 if no winner or draw.
     */
    int getSmallBoardWinner(int col, int row);

    /**
     * Returns the small board that must be played in next (0-2),
     * or -1 if the player can play anywhere.
     * Returns {col, row} or null.
     */
    int[] getActiveSmallBoard();

    /**
     * Returns true if the game is over.
     */
    boolean isGameOver();

    /**
     * Gets the id of the winner (-1, 0, 1).
     */
    int getWinner();

    /**
     * Resets the game.
     */
    void newGame();
}
