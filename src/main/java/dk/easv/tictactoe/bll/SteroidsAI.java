package dk.easv.tictactoe.bll;

import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced Steroids AI for Ultimate Tic-Tac-Toe.
 * Uses depth-limited Minimax with Alpha-Beta pruning.
 */
public class SteroidsAI implements AIPlayer {

    private static final String BOT_NAME = "Steroids AI v2.0";
    private static final int MAX_DEPTH = 6;
    private static final int[][] POSITIONAL_WEIGHTS = {
        {3, 2, 3},
        {2, 4, 2},
        {3, 2, 3}
    };

    @Override
    public String getBotName() {
        return BOT_NAME;
    }

    @Override
    public int[] chooseMove(IGameBoard board, int aiPlayer) {
        int bestScore = Integer.MIN_VALUE;
        List<int[]> moves = getAvailableMoves(board);
        if (moves.isEmpty()) return null;
        
        int[] bestMove = moves.get(0); // Initialize with first available move

        for (int[] move : moves) {
            GameBoard sim = new GameBoard(board);
            sim.play(move[0], move[1]);
            int score = minimax(sim, aiPlayer, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private int minimax(GameBoard board, int aiPlayer, int depth, boolean isMaximizing, int alpha, int beta) {
        if (board.isGameOver() || depth == MAX_DEPTH) {
            return evaluate(board, aiPlayer, depth);
        }

        List<int[]> moves = getAvailableMoves(board);
        if (moves.isEmpty()) return evaluate(board, aiPlayer, depth);

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int[] move : moves) {
                GameBoard sim = new GameBoard(board);
                sim.play(move[0], move[1]);
                int eval = minimax(sim, aiPlayer, depth + 1, false, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int[] move : moves) {
                GameBoard sim = new GameBoard(board);
                sim.play(move[0], move[1]);
                int eval = minimax(sim, aiPlayer, depth + 1, true, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    private int evaluate(GameBoard board, int aiPlayer, int depth) {
        int winner = board.getWinner();
        if (winner == aiPlayer) return 100000 - depth;
        if (winner != -1) return -100000 + depth;
        if (board.isGameOver()) return 0; // Draw

        int score = 0;
        int opponent = 1 - aiPlayer;

        // 1. Global Board State
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                int sw = board.getSmallBoardWinner(c, r);
                if (sw == aiPlayer) score += 500 * POSITIONAL_WEIGHTS[r][c];
                else if (sw == opponent) score -= 500 * POSITIONAL_WEIGHTS[r][c];
                else if (sw == -1) {
                    // 2. Local Board Progress
                    score += evaluateSmallBoard(board, c, r, aiPlayer);
                }
            }
        }

        return score;
    }

    private int evaluateSmallBoard(IGameBoard board, int sc, int sr, int aiPlayer) {
        int score = 0;
        int startC = sc * 3;
        int startR = sr * 3;
        int opponent = 1 - aiPlayer;

        // Positional scoring within the small board
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                int field = board.getField(startC + c, startR + r);
                if (field == aiPlayer) score += 10 * POSITIONAL_WEIGHTS[r][c];
                else if (field == opponent) score -= 10 * POSITIONAL_WEIGHTS[r][c];
            }
        }

        // Potential for winning lines (two in a row)
        score += checkPotential(board, startC, startR, aiPlayer) * 50;
        score -= checkPotential(board, startC, startR, opponent) * 50;

        return score;
    }

    private int checkPotential(IGameBoard board, int startC, int startR, int player) {
        int count = 0;
        // Rows, Cols, Diagonals
        for (int i = 0; i < 3; i++) {
            if (countLine(board, startC, startR + i, 1, 0, player) == 2) count++;
            if (countLine(board, startC + i, startR, 0, 1, player) == 2) count++;
        }
        if (countLine(board, startC, startR, 1, 1, player) == 2) count++;
        if (countLine(board, startC + 2, startR, -1, 1, player) == 2) count++;
        return count;
    }

    private int countLine(IGameBoard board, int startC, int startR, int dc, int dr, int player) {
        int p = 0;
        int e = 0;
        for (int i = 0; i < 3; i++) {
            int field = board.getField(startC + i * dc, startR + i * dr);
            if (field == player) p++;
            else if (field == -1) e++;
        }
        return (p == 2 && e == 1) ? 2 : 0;
    }

    private List<int[]> getAvailableMoves(IGameBoard board) {
        List<int[]> moves = new ArrayList<>();
        int[] active = board.getActiveSmallBoard();

        if (active != null) {
            int startC = active[0] * 3;
            int startR = active[1] * 3;
            for (int r = startR; r < startR + 3; r++) {
                for (int c = startC; c < startC + 3; c++) {
                    if (board.getField(c, r) == -1) moves.add(new int[]{c, r});
                }
            }
        } else {
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    int sc = c / 3;
                    int sr = r / 3;
                    if (board.getSmallBoardWinner(sc, sr) == -1 && board.getField(c, r) == -1) {
                        moves.add(new int[]{c, r});
                    }
                }
            }
        }
        return moves;
    }
}
