package client;

import client.ConnectFourClient;
import shared.NetworkMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SimpleMultiClientTest {

    public static void main(String[] args) {
        System.out.println("Starting Simple Multi-Client Test...");

        // Create test frames
        JFrame client1Frame = createClientTestFrame("Player 1", 100, 100);
        JFrame client2Frame = createClientTestFrame("Player 2", 950, 100);

        client1Frame.setVisible(true);
        client2Frame.setVisible(true);
    }

    private static JFrame createClientTestFrame(String title, int x, int y) {
        JFrame frame = new JFrame("Connect Four - " + title);
        frame.setSize(400, 300);
        frame.setLocation(x, y);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        // Connection panel
        JPanel connectionPanel = new JPanel(new FlowLayout());
        JTextField hostField = new JTextField("localhost", 10);
        JTextField portField = new JTextField("8888", 5);
        JButton connectButton = new JButton("Connect");

        connectionPanel.add(new JLabel("Host:"));
        connectionPanel.add(hostField);
        connectionPanel.add(new JLabel("Port:"));
        connectionPanel.add(portField);
        connectionPanel.add(connectButton);

        // Login panel
        JPanel loginPanel = new JPanel(new FlowLayout());
        JTextField usernameField = new JTextField(title, 10);
        JTextField passwordField = new JTextField("pass", 10);
        JButton loginButton = new JButton("Login");
        JButton createButton = new JButton("Create Account");

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(createButton);

        // Game panel
        JPanel gamePanel = new JPanel(new FlowLayout());
        JButton joinButton = new JButton("Join Game");
        JTextField moveField = new JTextField("0", 2);
        JButton moveButton = new JButton("Make Move");

        gamePanel.add(joinButton);
        gamePanel.add(new JLabel("Column:"));
        gamePanel.add(moveField);
        gamePanel.add(moveButton);

        // Status
        JTextArea statusArea = new JTextArea(10, 30);
        statusArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(statusArea);

        // Add panels
        panel.add(connectionPanel, BorderLayout.NORTH);
        panel.add(loginPanel, BorderLayout.CENTER);
        panel.add(gamePanel, BorderLayout.SOUTH);
        panel.add(scrollPane, BorderLayout.PAGE_END);

        frame.add(panel);

        // Client
        ConnectFourClient client = new ConnectFourClient();

        // Setup listeners
        connectButton.addActionListener(e -> {
            try {
                String host = hostField.getText();
                int port = Integer.parseInt(portField.getText());

                boolean connected = client.connect(host, port);
                statusArea.append("Connection " + (connected ? "successful" : "failed") + "\n");

                if (connected) {
                    client.setMessageListener(message -> {
                        SwingUtilities.invokeLater(() -> {
                            statusArea.append("Received: " + message.getMessageType() +
                                    " from " + message.getSender() +
                                    " - " + message.getContent() + "\n");
                        });
                    });
                }
            } catch (Exception ex) {
                statusArea.append("Error: " + ex.getMessage() + "\n");
            }
        });

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            client.login(username, password);
            statusArea.append("Login attempt: " + username + "\n");
        });

        createButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            client.createAccount(username, password);
            statusArea.append("Create account attempt: " + username + "\n");
        });

        joinButton.addActionListener(e -> {
            client.joinGame();
            statusArea.append("Joining game...\n");
        });

        moveButton.addActionListener(e -> {
            try {
                int column = Integer.parseInt(moveField.getText());
                client.makeMove(column);
                statusArea.append("Making move in column " + column + "\n");
            } catch (Exception ex) {
                statusArea.append("Error making move: " + ex.getMessage() + "\n");
            }
        });

        return frame;
    }
}
