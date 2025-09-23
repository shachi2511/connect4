package client;

import shared.GameConstants;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class GameBoard {
    private int[][] board;
    private int currentPlayer;
    private boolean gameOver;
    private int winner;

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
        currentPlayer = GameConstants.PLAYER1;
        gameOver = false;
        winner = GameConstants.EMPTY;
    }

    public boolean makeMove(int column, int player) {
        if (column < 0 || column >= GameConstants.BOARD_COLS || gameOver) {
            return false;
        }

        // Find the lowest empty row in the column
        for (int row = GameConstants.BOARD_ROWS - 1; row >= 0; row--) {
            if (board[row][column] == GameConstants.EMPTY) {
                board[row][column] = player;
                checkGameStatus();
                return true;
            }
        }

        return false; // Column is full
    }

    private void checkGameStatus() {
        if (checkWin()) {
            gameOver = true;
            winner = currentPlayer;
        } else if (isFull()) {
            gameOver = true;
            winner = GameConstants.DRAW;
        } else {
            // Switch players
            currentPlayer = (currentPlayer == GameConstants.PLAYER1) ?
                    GameConstants.PLAYER2 : GameConstants.PLAYER1;
        }
    }

    public boolean checkWin() {
        // Check horizontal
        for (int row = 0; row < GameConstants.BOARD_ROWS; row++) {
            for (int col = 0; col <= GameConstants.BOARD_COLS - 4; col++) {
                if (board[row][col] != GameConstants.EMPTY &&
                        board[row][col] == board[row][col+1] &&
                        board[row][col] == board[row][col+2] &&
                        board[row][col] == board[row][col+3]) {
                    winner = board[row][col];
                    return true;
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
                    winner = board[row][col];
                    return true;
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
                    winner = board[row][col];
                    return true;
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
                    winner = board[row][col];
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isFull() {
        for (int col = 0; col < GameConstants.BOARD_COLS; col++) {
            if (board[0][col] == GameConstants.EMPTY) {
                return false;
            }
        }
        return true;
    }

    public int getCell(int row, int col) {
        return board[row][col];
    }

    public int[][] getBoardState() {
        return board;
    }

    public void setBoardState(int[][] newBoard) {
        for (int row = 0; row < GameConstants.BOARD_ROWS; row++) {
            for (int col = 0; col < GameConstants.BOARD_COLS; col++) {
                board[row][col] = newBoard[row][col];
            }
        }
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int player) {
        this.currentPlayer = player;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public List<Point> getWinningCells() {
        List<Point> winningCells = new ArrayList<>();
        return winningCells;
    }
}