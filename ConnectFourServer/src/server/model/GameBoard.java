package server.model;

import shared.GameConstants;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class GameBoard {
    private int[][] board;

    public GameBoard() {
        board = new int[GameConstants.BOARD_ROWS][GameConstants.BOARD_COLS];
        reset();
    }

    public void reset() {
        for (int row = 0; row < GameConstants.BOARD_ROWS; row++) {
            for (int col = 0; col < GameConstants.BOARD_COLS; col++) {
                board[row][col] = GameConstants.EMPTY;
            }
        }
    }

    public boolean makeMove(int column, int player) {
        if (column < 0 || column >= GameConstants.BOARD_COLS) {
            return false;
        }

        // Find lowest empty spot in the column
        for (int row = GameConstants.BOARD_ROWS - 1; row >= 0; row--) {
            if (board[row][column] == GameConstants.EMPTY) {
                board[row][column] = player;
                return true;
            }
        }

        return false; // Column is full
    }

    public boolean checkWin() {
        return getWinner() != 0;
    }

    public int getWinner() {
        // Check horizontal
        for (int row = 0; row < GameConstants.BOARD_ROWS; row++) {
            for (int col = 0; col <= GameConstants.BOARD_COLS - 4; col++) {
                if (board[row][col] != GameConstants.EMPTY &&
                        board[row][col] == board[row][col+1] &&
                        board[row][col] == board[row][col+2] &&
                        board[row][col] == board[row][col+3]) {
                    return board[row][col];
                }
            }
        }

        // Check vertical
        for (int row = 0; row <= GameConstants.BOARD_ROWS - 4; row++) {
            for (int col = 0; col < GameConstants.BOARD_COLS; col++) {
                if (board[row][col] != GameConstants.EMPTY &&
                        board[row][col] == board[row+1][col] &&
                        board[row][col] == board[row+2][col] &&
                        board[row][col] == board[row+3][col]) {
                    return board[row][col];
                }
            }
        }

        // Check diagonal (down-right)
        for (int row = 0; row <= GameConstants.BOARD_ROWS - 4; row++) {
            for (int col = 0; col <= GameConstants.BOARD_COLS - 4; col++) {
                if (board[row][col] != GameConstants.EMPTY &&
                        board[row][col] == board[row+1][col+1] &&
                        board[row][col] == board[row+2][col+2] &&
                        board[row][col] == board[row+3][col+3]) {
                    return board[row][col];
                }
            }
        }

        // Check diagonal (up-right)
        for (int row = 3; row < GameConstants.BOARD_ROWS; row++) {
            for (int col = 0; col <= GameConstants.BOARD_COLS - 4; col++) {
                if (board[row][col] != GameConstants.EMPTY &&
                        board[row][col] == board[row-1][col+1] &&
                        board[row][col] == board[row-2][col+2] &&
                        board[row][col] == board[row-3][col+3]) {
                    return board[row][col];
                }
            }
        }

        return 0; // No winner
    }

    public boolean isFull() {
        for (int col = 0; col < GameConstants.BOARD_COLS; col++) {
            if (board[0][col] == GameConstants.EMPTY) {
                return false;
            }
        }
        return true;
    }

    public int[][] getBoardState() {
        // Create a deep copy to prevent external modification
        int[][] copy = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, board[i].length);
        }
        return copy;
    }

    public List<Integer> getValidMoves() {
        List<Integer> validMoves = new ArrayList<>();
        for (int col = 0; col < GameConstants.BOARD_COLS; col++) {
            if (board[0][col] == GameConstants.EMPTY) {
                validMoves.add(col);
            }
        }
        return validMoves;
    }
}