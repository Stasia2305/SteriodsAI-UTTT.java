package dk.easv.tictactoe.bll;

/**
 * Implementation of Ultimate Tic-Tac-Toe.
 */
public class GameBoard implements IGameBoard {

    private final int[][] board = new int[9][9];
    private final int[][] smallBoardWinners = new int[3][3];
    private int nextPlayer;
    private int winner; // -1 none
    private int[] activeSmallBoard; // {col, row} or null

    public GameBoard() {
        newGame();
    }

    public GameBoard(IGameBoard other) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                board[r][c] = other.getField(c, r);
            }
        }
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                smallBoardWinners[r][c] = other.getSmallBoardWinner(c, r);
            }
        }
        this.nextPlayer = other.getNextPlayer();
        this.winner = other.getWinner();
        int[] act = other.getActiveSmallBoard();
        this.activeSmallBoard = (act == null) ? null : new int[]{act[0], act[1]};
    }

    @Override
    public int getNextPlayer() {
        return nextPlayer;
    }

    @Override
    public boolean play(int col, int row) {
        if (col < 0 || col >= 9 || row < 0 || row >= 9) return false;
        if (isGameOver()) return false;
        if (board[row][col] != -1) return false;

        int sc = col / 3;
        int sr = row / 3;

        // Check if move is in active small board
        if (activeSmallBoard != null) {
            if (sc != activeSmallBoard[0] || sr != activeSmallBoard[1]) {
                return false;
            }
        }

        // Check if small board is already won
        if (smallBoardWinners[sr][sc] != -1) {
            return false;
        }

        board[row][col] = nextPlayer;

        // Check if this move wins the small board
        if (checkSmallWin(sc, sr, nextPlayer)) {
            smallBoardWinners[sr][sc] = nextPlayer;
            // Check if this wins the whole game
            if (checkGlobalWin(nextPlayer)) {
                winner = nextPlayer;
            }
        } else if (isSmallBoardFull(sc, sr)) {
            smallBoardWinners[sr][sc] = -2; // Draw in small board
        }

        // Determine next active small board
        int nextSc = col % 3;
        int nextSr = row % 3;

        if (smallBoardWinners[nextSr][nextSc] != -1 || isSmallBoardFull(nextSc, nextSr)) {
            activeSmallBoard = null; // Play anywhere
        } else {
            activeSmallBoard = new int[]{nextSc, nextSr};
        }

        nextPlayer = 1 - nextPlayer;
        return true;
    }

    @Override
    public int getField(int col, int row) {
        if (col < 0 || col >= 9 || row < 0 || row >= 9) return -2;
        return board[row][col];
    }

    @Override
    public int getSmallBoardWinner(int col, int row) {
        if (col < 0 || col >= 3 || row < 0 || row >= 3) return -2;
        return smallBoardWinners[row][col];
    }

    @Override
    public int[] getActiveSmallBoard() {
        return activeSmallBoard;
    }

    @Override
    public boolean isGameOver() {
        if (winner != -1) return true;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (smallBoardWinners[r][c] == -1) return false;
            }
        }
        return true;
    }

    @Override
    public int getWinner() {
        return winner;
    }

    @Override
    public void newGame() {
        for (int r = 0; r < 9; r++) for (int c = 0; c < 9; c++) board[r][c] = -1;
        for (int r = 0; r < 3; r++) for (int c = 0; c < 3; c++) smallBoardWinners[r][c] = -1;
        nextPlayer = 0;
        winner = -1;
        activeSmallBoard = null;
    }

    private boolean checkSmallWin(int sc, int sr, int player) {
        int startR = sr * 3;
        int startC = sc * 3;
        for (int r = 0; r < 3; r++) {
            if (board[startR + r][startC] == player && board[startR + r][startC + 1] == player && board[startR + r][startC + 2] == player) return true;
        }
        for (int c = 0; c < 3; c++) {
            if (board[startR][startC + c] == player && board[startR + 1][startC + c] == player && board[startR + 2][startC + c] == player) return true;
        }
        if (board[startR][startC] == player && board[startR + 1][startC + 1] == player && board[startR + 2][startC + 2] == player) return true;
        if (board[startR][startC + 2] == player && board[startR + 1][startC + 1] == player && board[startR + 2][startC] == player) return true;
        return false;
    }

    private boolean isSmallBoardFull(int sc, int sr) {
        int startR = sr * 3;
        int startC = sc * 3;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[startR + r][startC + c] == -1) return false;
            }
        }
        return true;
    }

    private boolean checkGlobalWin(int player) {
        for (int r = 0; r < 3; r++) {
            if (smallBoardWinners[r][0] == player && smallBoardWinners[r][1] == player && smallBoardWinners[r][2] == player) return true;
        }
        for (int c = 0; c < 3; c++) {
            if (smallBoardWinners[0][c] == player && smallBoardWinners[1][c] == player && smallBoardWinners[2][c] == player) return true;
        }
        if (smallBoardWinners[0][0] == player && smallBoardWinners[1][1] == player && smallBoardWinners[2][2] == player) return true;
        if (smallBoardWinners[0][2] == player && smallBoardWinners[1][1] == player && smallBoardWinners[2][0] == player) return true;
        return false;
    }
}
