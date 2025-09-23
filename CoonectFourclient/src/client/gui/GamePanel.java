package client.gui;

import client.GameBoard;
import shared.GameConstants;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private ClientGUI parent;
    private GameBoardUI boardUI;
    private JButton resignButton;
    private JLabel opponentLabel;
    private JLabel turnLabel;

    public GamePanel(ClientGUI parent) {
        this.parent = parent;
        setupPanel();
    }

    private void setupPanel() {
        setLayout(new BorderLayout());

        // Game info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        opponentLabel = new JLabel("Opponent: Waiting...", JLabel.CENTER);
        turnLabel = new JLabel("Your turn", JLabel.CENTER);
        infoPanel.add(opponentLabel, BorderLayout.NORTH);
        infoPanel.add(turnLabel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        resignButton = new JButton("Resign");
        buttonPanel.add(resignButton);

        infoPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(infoPanel, BorderLayout.NORTH);

        // Game board
        boardUI = new GameBoardUI(parent);
        add(boardUI, BorderLayout.CENTER);

        resignButton.addActionListener(e -> {
            System.out.println("Resign button clicked");
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to resign? This will count as a loss.",
                    "Confirm Resignation",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                System.out.println("Resignation confirmed - sending to server");
                // Send resignation message to server
                parent.getClient().sendResignation();

                enablePlayerInput(false);
                resignButton.setEnabled(false);
                turnLabel.setText("Resigning...");
            } else {
                System.out.println("Resignation cancelled");
            }
        });
    }

    // In GamePanel.java - updateBoard method
    public void updateBoard(int[][] board, int currentPlayer) {
        System.out.println("GamePanel.updateBoard called"); // Debug print
        // Print board state for debugging
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                System.out.print(board[row][col] + " ");
            }
            System.out.println();
        }

        boardUI.updateUI(board, currentPlayer);
        repaint();
    }

    public void enablePlayerInput(boolean enable) {
        boardUI.setEnabled(enable);
        resignButton.setEnabled(enable);
    }

    public void setOpponentName(String name) {
        opponentLabel.setText("Opponent: " + name);
    }

    public void setTurnMessage(String message) {
        turnLabel.setText(message);
    }
}