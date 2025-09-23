package client.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LobbyPanel extends JPanel {
    private ClientGUI parent;
    private JButton joinGameButton;
    private JButton logoutButton;
    private JLabel welcomeLabel;

    public LobbyPanel(ClientGUI parent) {
        this.parent = parent;
        setupPanel();
    }

    private void setupPanel() {
        setLayout(new BorderLayout());

        // Welcome message
        welcomeLabel = new JLabel("Welcome!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(welcomeLabel, BorderLayout.NORTH);

        // Buttons
        JPanel buttonPanel = new JPanel();
        joinGameButton = new JButton("Join Game");
        logoutButton = new JButton("Logout");

        buttonPanel.add(joinGameButton);
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.CENTER);

        // Action listeners
        joinGameButton.addActionListener(e -> {
            parent.getClient().joinGame();
        });

        logoutButton.addActionListener(e -> {
            parent.getClient().logout();
            parent.displayLoginScreen();
        });
    }

    public void setWelcomeMessage(String username) {
        welcomeLabel.setText("Welcome, " + username + "!");
    }

    public void enableJoinButton(boolean enable) {
        joinGameButton.setEnabled(enable);
    }
}