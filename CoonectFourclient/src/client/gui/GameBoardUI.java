package client.gui;

import shared.GameConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class GameBoardUI extends JPanel {
    private ClientGUI parent;
    private int[][] board;
    private int currentPlayer;
    private boolean inputEnabled;
    private List<Point> winningCells;

    private static final int CELL_SIZE = 70;
    private static final Color BOARD_COLOR = new Color(0, 0, 150);
    private static final Color EMPTY_COLOR = Color.WHITE;
    private static final Color PLAYER1_COLOR = Color.RED;
    private static final Color PLAYER2_COLOR = Color.YELLOW;
    private static final Color HIGHLIGHT_COLOR = Color.GREEN;

    public GameBoardUI(ClientGUI parent) {
        this.parent = parent;
        this.board = new int[GameConstants.BOARD_ROWS][GameConstants.BOARD_COLS];
        this.currentPlayer = GameConstants.PLAYER1;
        this.inputEnabled = true;

        setPreferredSize(new Dimension(
                GameConstants.BOARD_COLS * CELL_SIZE,
                (GameConstants.BOARD_ROWS + 1) * CELL_SIZE));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (inputEnabled) {
                    int column = e.getX() / CELL_SIZE;
                    if (column >= 0 && column < GameConstants.BOARD_COLS) {
                        parent.getClient().makeMove(column);
                        System.out.println("Client sending move to column: " + column);
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        //the board
        g2d.setColor(BOARD_COLOR);
        g2d.fillRect(0, CELL_SIZE, GameConstants.BOARD_COLS * CELL_SIZE, GameConstants.BOARD_ROWS * CELL_SIZE);

        //the column selectors
        for (int col = 0; col < GameConstants.BOARD_COLS; col++) {
            if (isColumnFull(col)) {
                g2d.setColor(Color.GRAY);
            } else {
                g2d.setColor(currentPlayer == GameConstants.PLAYER1 ? PLAYER1_COLOR : PLAYER2_COLOR);
            }
            g2d.fillOval(col * CELL_SIZE + 5, 5, CELL_SIZE - 10, CELL_SIZE - 10);
        }

        //the cells
        for (int row = 0; row < GameConstants.BOARD_ROWS; row++) {
            for (int col = 0; col < GameConstants.BOARD_COLS; col++) {
                Color cellColor;
                switch (board[row][col]) {
                    case GameConstants.PLAYER1:
                        cellColor = PLAYER1_COLOR;
                        break;
                    case GameConstants.PLAYER2:
                        cellColor = PLAYER2_COLOR;
                        break;
                    default:
                        cellColor = EMPTY_COLOR;
                }

                // Check if this cell is part of a winning combination
                boolean isWinningCell = false;
                if (winningCells != null) {
                    for (Point p : winningCells) {
                        if (p.x == row && p.y == col) {
                            isWinningCell = true;
                            break;
                        }
                    }
                }

                //cell with border for empty cells
                g2d.setColor(cellColor);
                if (isWinningCell) {
                    g2d.setColor(HIGHLIGHT_COLOR);
                    g2d.fillOval(col * CELL_SIZE + 5, (row + 1) * CELL_SIZE + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                    g2d.setColor(cellColor);
                    g2d.fillOval(col * CELL_SIZE + 10, (row + 1) * CELL_SIZE + 10, CELL_SIZE - 20, CELL_SIZE - 20);
                } else {
                    g2d.fillOval(col * CELL_SIZE + 5, (row + 1) * CELL_SIZE + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                }
            }
        }
    }

    private boolean isColumnFull(int col) {
        return board[0][col] != GameConstants.EMPTY;
    }

    public void updateUI(int[][] board, int currentPlayer) {
        this.board = board;
        this.currentPlayer = currentPlayer;
        repaint();
    }

    public void highlightWinningCells(List<Point> cells) {
        this.winningCells = cells;
        repaint();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.inputEnabled = enabled;
    }
}