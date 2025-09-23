package server;

import server.model.GameSession;
import shared.NetworkMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ConnectFourServer server;
    private String username;
    private GameSession currentGame;
    private boolean isLoggedIn = false;

    public ClientHandler(Socket socket, ConnectFourServer server) {
        this.clientSocket = socket;
        this.server = server;

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error setting up streams: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                NetworkMessage message = (NetworkMessage) in.readObject();
                processMessage(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    private void processMessage(NetworkMessage message) {
        System.out.println("Processing message type: " + message.getMessageType() + " from " + message.getSender());

        switch (message.getMessageType()) {
            case NetworkMessage.LOGIN:
                handleLogin(message);
                break;
            case NetworkMessage.JOIN_GAME:
                handleJoinGame();
                break;
            case NetworkMessage.MAKE_MOVE:
                handleMakeMove(message);
                break;
            case NetworkMessage.CHAT_MESSAGE:
                handleChatMessage(message);
                break;
            case NetworkMessage.LOGOUT:
                disconnect();
                break;
            case NetworkMessage.RESIGN:
            case NetworkMessage.RESIGN_GAME:
                System.out.println("Player " + username + " resigned from game");
                if (currentGame != null) {
                    currentGame.handlePlayerResign(this);
                } else {
                    System.out.println("Error: Player " + username + " tried to resign but was not in a game");
                }
                break;
            default:
                System.out.println("Unknown message type: " + message.getMessageType());
                break;
        }
    }

    private void handleLogin(NetworkMessage message) {
        String[] credentials = ((String) message.getContent()).split(":");
        String username = credentials[0];
        String password = credentials[1];

        if (credentials.length == 3 && credentials[2].equals("create")) {
            // Creating new account
            if (server.registerUser(username, password)) {
                this.username = username;
                isLoggedIn = true;
                sendMessage(new NetworkMessage(NetworkMessage.LOGIN, "SERVER", "success"));
                System.out.println("New account created: " + username);
            } else {
                sendMessage(new NetworkMessage(NetworkMessage.LOGIN, "SERVER", "username_exists"));
                System.out.println("Account creation failed: " + username + " (already exists)");
            }
        } else {
            // Logging in to existing account
            if (server.authenticateUser(username, password)) {
                this.username = username;
                isLoggedIn = true;
                sendMessage(new NetworkMessage(NetworkMessage.LOGIN, "SERVER", "success"));
                System.out.println("User logged in: " + username);
            } else {
                sendMessage(new NetworkMessage(NetworkMessage.LOGIN, "SERVER", "invalid_credentials"));
                System.out.println("Login failed for: " + username);
            }
        }
    }

    private void handleJoinGame() {
        if (isLoggedIn) {
            server.addToWaitingList(this);
            sendMessage(new NetworkMessage(NetworkMessage.JOIN_GAME, "SERVER", "waiting_for_opponent"));
        } else {
            sendMessage(new NetworkMessage(NetworkMessage.JOIN_GAME, "SERVER", "not_logged_in"));
        }
    }

    private void handleMakeMove(NetworkMessage message) {
        if (currentGame != null) {
            try {
                int column = (Integer) message.getContent();
                System.out.println("Player " + username + " requested move in column " + column);
                currentGame.processMove(this, column);
            } catch (Exception e) {
                System.out.println("Error processing move: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Player " + username + " tried to make a move but is not in a game");
        }
    }

    private void handleChatMessage(NetworkMessage message) {
        if (currentGame != null) {
            currentGame.notifyPlayers(message);
        }
    }

    public void sendMessage(Object message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.out.println("Error sending message to " + username + ": " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (currentGame != null) {
                currentGame.handlePlayerDisconnect(this);
                currentGame = null;
            }

            server.removeClient(this);

            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }

            System.out.println("Client disconnected: " + (username != null ? username : "unknown"));

        } catch (IOException e) {
            System.out.println("Error disconnecting client: " + e.getMessage());
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isInGame() {
        return currentGame != null;
    }

    public void setCurrentGame(GameSession game) {
        this.currentGame = game;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public GameSession getCurrentGame() {
        return currentGame;
    }
}