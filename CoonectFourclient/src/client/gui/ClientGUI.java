package client.gui;

import client.ConnectFourClient;
import shared.NetworkMessage;
import shared.GameConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClientGUI extends JFrame implements ConnectFourClient.MessageListener {
    private ConnectFourClient client;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private LoginPanel loginPanel;
    private LobbyPanel lobbyPanel;
    private GamePanel gamePanel;
    private EndGamePanel endGamePanel;
    private ChatPanel chatPanel;

    // Tracking if game is over to prevent more moves
    private boolean gameOver = false;

    public ClientGUI() {
        client = new ConnectFourClient();
        client.setMessageListener(this);

        setupMainFrame();
        setupPanels();

        displayLoginScreen();
    }

    private void setupMainFrame() {
        setTitle("Connect Four Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client.isConnected()) {
                    client.disconnect();
                }
            }
        });

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);
    }

    private void setupPanels() {
        loginPanel = new LoginPanel(this);
        lobbyPanel = new LobbyPanel(this);
        gamePanel = new GamePanel(this);
        endGamePanel = new EndGamePanel(this);
        chatPanel = new ChatPanel(this);

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(lobbyPanel, "LOBBY");

        JPanel gameContainer = new JPanel(new BorderLayout());
        gameContainer.add(gamePanel, BorderLayout.CENTER);
        gameContainer.add(chatPanel, BorderLayout.EAST);
        mainPanel.add(gameContainer, "GAME");

        mainPanel.add(endGamePanel, "END_GAME");
    }

    public void displayLoginScreen() {
        gameOver = false;
        cardLayout.show(mainPanel, "LOGIN");
    }

    public void displayLobbyScreen() {
        // Reset game state
        gameOver = false;

        // Update lobby welcome message
        lobbyPanel.setWelcomeMessage(client.getUsername());

        // Show the lobby panel
        cardLayout.show(mainPanel, "LOBBY");

        // Force UI update
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void displayGameScreen() {
        if (!gameOver) {
            cardLayout.show(mainPanel, "GAME");
        }
    }

    public void displayEndGameScreen(String winner) {
        System.out.println("SWITCHING TO END GAME SCREEN: " + winner);
        gameOver = true;

        // Setting the result on the end game panel which will show dialog
        endGamePanel.setResult(winner);
        cardLayout.show(mainPanel, "END_GAME");

        revalidate();
        repaint();

        System.out.println("End game screen should now be visible");
    }

    public void resetUI() {
        System.out.println("Completely resetting UI to show lobby screen");

        // Resetting game state
        gameOver = false;

        // Create new frame with same properties
        setSize(800, 600);

        // Removing all components
        getContentPane().removeAll();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        setupPanels();

        // Updating welcome message
        lobbyPanel.setWelcomeMessage(client.getUsername());

        // Show the lobby
        cardLayout.show(mainPanel, "LOBBY");

        revalidate();
        repaint();

        System.out.println("Lobby screen should now be visible");
    }

    public void resetAndDisplayLobby() {
        resetUI();
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void updateGameBoard(int[][] board, int currentPlayer) {
        if (!gameOver) {
            gamePanel.updateBoard(board, currentPlayer);
        }
    }

    public void updateChat(String sender, String message) {
        chatPanel.addMessage(sender, message);
    }

    public ConnectFourClient getClient() {
        return client;
    }

    @Override
    public void onMessageReceived(NetworkMessage message) {
        System.out.println("Received message type: " + message.getMessageType() +
                " from: " + message.getSender() +
                " content: " + message.getContent());

        SwingUtilities.invokeLater(() -> {
            processMessage(message);
        });
    }

    private void processMessage(NetworkMessage message) {
        System.out.println("Processing message type: " + message.getMessageType());

        switch (message.getMessageType()) {
            case NetworkMessage.LOGIN:
                handleLoginResponse(message);
                break;
            case NetworkMessage.JOIN_GAME:
                handleJoinGameResponse(message);
                break;
            case NetworkMessage.GAME_STATE:
                if (!gameOver) {
                    handleGameStateUpdate(message);
                }
                break;
            case NetworkMessage.CHAT_MESSAGE:
                handleChatMessage(message);
                break;
            case NetworkMessage.GAME_RESULT:
                System.out.println("Game result message received, handling game result");
                handleGameResult(message);
                break;
            default:
                System.out.println("Unknown message type: " + message.getMessageType());
                break;
        }
    }

    private void handleLoginResponse(NetworkMessage message) {
        String response = (String) message.getContent();
        if (response.equals(NetworkMessage.SUCCESS)) {
            displayLobbyScreen();
        } else {
            showErrorMessage("Login failed: " + response);
        }
    }

    private void handleJoinGameResponse(NetworkMessage message) {
        String response = (String) message.getContent();
        if (response.equals(NetworkMessage.WAITING_FOR_OPPONENT)) {
            JOptionPane.showMessageDialog(this, "Waiting for an opponent...");
        } else {
            // Game starting
            gameOver = false;
            displayGameScreen();
        }
    }

    private void handleGameStateUpdate(NetworkMessage message) {
        // Skiping if game is over
        if (gameOver) {
            System.out.println("Game is over, ignoring game state update");
            return;
        }
        displayGameScreen();

        Object content = message.getContent();
        if (content instanceof String) {// Checking if string message or board state data
            String status = (String) content;//status message
            gamePanel.setTurnMessage(status);
        }
        else if (content instanceof Object[]) {
            try {
                Object[] gameStateData = (Object[]) content;

                // Extracting data from the array
                int[][] boardState = (int[][]) gameStateData[0];
                int currentTurn = (int) gameStateData[1];
                String turnMessage = (String) gameStateData[2];

                // Updating the game board display with new board state
                gamePanel.updateBoard(boardState, currentTurn);
                gamePanel.setTurnMessage(turnMessage);
            } catch (Exception e) {
                System.out.println("Error processing game state: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void handleChatMessage(NetworkMessage message) {
        updateChat(message.getSender(), (String) message.getContent());
    }

    private void handleGameResult(NetworkMessage message) {
        String result = (String) message.getContent();
        System.out.println("Game result received: " + result);

        gameOver = true;

        try {
            SwingUtilities.invokeLater(() -> {
                try {
                    displayEndGameScreen(result);
                } catch (Exception e) {
                    System.out.println("Error displaying end game screen: " + e);
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.out.println("Error in handleGameResult: " + e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientGUI gui = new ClientGUI();
            gui.setVisible(true);
        });
    }
}
