
package dk.easv.tictactoe.bll;

/**
 * Simple AI interface for TicTacToe
 */
public interface AIPlayer {
    /**
     * Given the current game board, return a move as int[]{col,row}.
     * Return null if no move possible.
     */
    int[] chooseMove(IGameBoard board, int aiPlayer);
}
