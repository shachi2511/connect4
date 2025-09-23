package client.gui;

import javax.swing.*;
import java.awt.*;

public class EndGamePanel extends JPanel {
    private ClientGUI parent;
    private JLabel resultLabel;
    private JLabel messageLabel;
    private JButton returnToLobbyButton;

    public EndGamePanel(ClientGUI parent) {
        this.parent = parent;
        setupPanel();
    }

    private void setupPanel() {
        setLayout(new BorderLayout());

        //panel for labels with padding
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        labelPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        // Result label
        resultLabel = new JLabel("Game Over", JLabel.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 30));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Message label
        messageLabel = new JLabel("", JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add labels to panel
        labelPanel.add(resultLabel);
        labelPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        labelPanel.add(messageLabel);

        add(labelPanel, BorderLayout.NORTH);

        // Button panel with single button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 30, 20));

        returnToLobbyButton = new JButton("Return to Lobby");
        returnToLobbyButton.setFont(new Font("Arial", Font.BOLD, 16));
        returnToLobbyButton.setPreferredSize(new Dimension(150, 40));

        buttonPanel.add(returnToLobbyButton);

        add(buttonPanel, BorderLayout.CENTER);


        returnToLobbyButton.addActionListener(e -> {
            System.out.println("Return to Lobby button clicked - returning to lobby immediately");
            parent.resetUI();
        });

        // Set background color
        setBackground(new Color(240, 240, 255));
    }

    public void setResult(String result) {
        resultLabel.setText(result);

        boolean isResignation = result.contains("resigned");

        // Personalized message based on result
        if (result.contains(parent.getClient().getUsername() + " wins") && isResignation) {
            // player won because opponent resigned
            resultLabel.setForeground(new Color(0, 150, 0)); // Green
            messageLabel.setText("Your opponent resigned. You win!");
            messageLabel.setForeground(new Color(0, 100, 0)); // Dark green

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        this,
                        "Your opponent resigned. You win!",
                        "Victory!",
                        JOptionPane.INFORMATION_MESSAGE
                );
                parent.resetUI();
            });
        } else if (result.contains(parent.getClient().getUsername()) && !isResignation) {
            // player won
            resultLabel.setForeground(new Color(0, 150, 0)); // Green
            messageLabel.setText("Well done! You are the Connect Four champion!");
            messageLabel.setForeground(new Color(0, 100, 0)); // Dark green

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        this,
                        "Congratulations on your victory!",
                        "You Won!",
                        JOptionPane.INFORMATION_MESSAGE
                );
                parent.resetUI();
            });
        } else if (result.contains("draw")) {
            // Game ended in a draw
            resultLabel.setForeground(new Color(0, 0, 150)); // Blue
            messageLabel.setText("It's a draw! You both played well.");
            messageLabel.setForeground(new Color(0, 0, 100)); // Dark blue

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        this,
                        "The game ended in a draw.",
                        "Draw Game",
                        JOptionPane.INFORMATION_MESSAGE
                );
                parent.resetUI();
            });
        } else if (result.contains(parent.getClient().getUsername() + " resigned")) {
            // The player resigned
            resultLabel.setForeground(new Color(150, 0, 0)); // Red
            messageLabel.setText("You resigned the game.");
            messageLabel.setForeground(new Color(100, 0, 0)); // Dark red

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        this,
                        "You resigned the game.",
                        "Game Over",
                        JOptionPane.INFORMATION_MESSAGE
                );
                // Return to lobby automatically after dialog is closed
                parent.resetUI();
            });
        } else {
            resultLabel.setForeground(new Color(150, 0, 0)); // Red
            messageLabel.setText("Better luck next time! Don't give up!");
            messageLabel.setForeground(new Color(100, 0, 0)); // Dark red

            // Show dialog with automatic return to lobby
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        this,
                        "You'll get them next time!",
                        "Game Over",
                        JOptionPane.INFORMATION_MESSAGE
                );
                // Return to lobby automatically after dialog is closed
                parent.resetUI();
            });
        }
    }
}