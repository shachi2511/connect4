package client.gui;

import shared.NetworkMessage;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private ClientGUI parent;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton createAccountButton;
    private JTextField serverField;
    private JTextField portField;
    private JButton connectButton;
    private JLabel statusLabel;

    public LoginPanel(ClientGUI parent) {
        this.parent = parent;
        setupPanel();
    }

    private void setupPanel() {
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Connect Four Game", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Connection panel
        JPanel connectionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Server settings
        gbc.gridx = 0;
        gbc.gridy = 0;
        connectionPanel.add(new JLabel("Server:"), gbc);

        gbc.gridx = 1;
        serverField = new JTextField("localhost", 15);
        connectionPanel.add(serverField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        connectionPanel.add(new JLabel("Port:"), gbc);

        gbc.gridx = 1;
        portField = new JTextField("8888", 15);
        connectionPanel.add(portField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        connectButton = new JButton("Connect to Server");
        connectionPanel.add(connectButton, gbc);

        gbc.gridy = 3;
        statusLabel = new JLabel("Not connected", JLabel.CENTER);
        connectionPanel.add(statusLabel, gbc);

        // Login panel
        JPanel loginPanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        loginButton = new JButton("Login");
        loginButton.setEnabled(false);
        loginPanel.add(loginButton, gbc);

        gbc.gridx = 1;
        createAccountButton = new JButton("Create Account");
        createAccountButton.setEnabled(false);
        loginPanel.add(createAccountButton, gbc);

        // Add panels to card
        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.add(connectionPanel);
        centerPanel.add(loginPanel);

        add(centerPanel, BorderLayout.CENTER);

        // Setup action listeners
        setupListeners();
    }

    private void setupListeners() {
        connectButton.addActionListener(e -> {
            try {
                String host = serverField.getText();
                int port = Integer.parseInt(portField.getText());

                statusLabel.setText("Connecting...");
                if (parent.getClient().connect(host, port)) {
                    statusLabel.setText("Connected to " + host + ":" + port);
                    loginButton.setEnabled(true);
                    createAccountButton.setEnabled(true);
                    connectButton.setEnabled(false);
                } else {
                    statusLabel.setText("Connection failed");
                }
            } catch (NumberFormatException ex) {
                parent.showErrorMessage("Invalid port number");
            }
        });

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                parent.showErrorMessage("Username and password cannot be empty");
                return;
            }

            parent.getClient().login(username, password);
        });

        createAccountButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                parent.showErrorMessage("Username and password cannot be empty");
                return;
            }

            parent.getClient().createAccount(username, password);
        });
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }
}