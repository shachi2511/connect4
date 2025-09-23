package client;

import shared.NetworkMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectFourClient {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String username;
    private String password;
    private boolean isConnected = false;
    private boolean inGame = false;
    private MessageListener messageListener;

    public ConnectFourClient() {
        // Initialize client
    }

    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            isConnected = true;

            // Start message
            new Thread(this::receiveMessages).start();

            System.out.println("Connected to server at " + host + ":" + port);
            return true;
        } catch (IOException e) {
            System.out.println("Failed to connect: " + e.getMessage());
            return false;
        }
    }

    public void disconnect() {
        try {
            if (isConnected) {
                sendMessage(new NetworkMessage(NetworkMessage.LOGOUT, username, null));
                socket.close();
                isConnected = false;
                System.out.println("Disconnected from server");
            }
        } catch (IOException e) {
            System.out.println("Error disconnecting: " + e.getMessage());
        }
    }

    public void login(String username, String password) {
        if (!isConnected) {
            System.out.println("Not connected to server");
            return;
        }

        this.username = username;
        this.password = password;

        sendMessage(new NetworkMessage(
                NetworkMessage.LOGIN,
                username,
                username + ":" + password
        ));
    }

    public void createAccount(String username, String password) {
        if (!isConnected) {
            System.out.println("Not connected to server");
            return;
        }

        this.username = username;
        this.password = password;

        sendMessage(new NetworkMessage(
                NetworkMessage.LOGIN,
                username,
                username + ":" + password + ":create"
        ));
    }

    public void joinGame() {
        if (!isConnected) {
            System.out.println("Not connected to server");
            return;
        }

        sendMessage(new NetworkMessage(
                NetworkMessage.JOIN_GAME,
                username,
                null
        ));
    }

    public void makeMove(int column) {
        if (!isConnected || !inGame) {
            System.out.println("Not in a game");
            return;
        }

        sendMessage(new NetworkMessage(
                NetworkMessage.MAKE_MOVE,
                username,
                column
        ));
    }

    public void sendResignation() {
        if (isConnected) {
            System.out.println("Sending resignation message to server");
            //after server confirmation
            sendMessage(new NetworkMessage(
                    NetworkMessage.RESIGN,
                    username,
                    "RESIGN"
            ));
            //wait for server confirmation
        } else {
            System.out.println("Not connected to server, cannot resign");
        }
    }

    public void sendChatMessage(String message) {
        if (!isConnected) {
            System.out.println("Not connected to server");
            return;
        }

        sendMessage(new NetworkMessage(
                NetworkMessage.CHAT_MESSAGE,
                username,
                message
        ));
    }

    public void quitGame() {
        if (!isConnected || !inGame) {
            System.out.println("Not in a game");
            return;
        }

        System.out.println("Warning: Using deprecated quitGame() method. Use sendResignation() instead.");
        sendMessage(new NetworkMessage(
                NetworkMessage.RESIGN,
                username,
                "RESIGN"
        ));


    }

    public void logout() {
        if (!isConnected) {
            System.out.println("Not connected to server");
            return;
        }

        sendMessage(new NetworkMessage(
                NetworkMessage.LOGOUT,
                username,
                null
        ));
    }

    private void sendMessage(NetworkMessage message) {
        try {
            System.out.println("Sending message type: " + message.getMessageType() +
                    " to server: " + (message.getContent() != null ? message.getContent().toString() : "null"));
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
            disconnect();
        }
    }

    private void receiveMessages() {
        try {
            while (isConnected) {
                NetworkMessage message = (NetworkMessage) in.readObject();
                processMessage(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Connection to server lost: " + e.getMessage());
            disconnect();
        }
    }

    private void processMessage(NetworkMessage message) {
        System.out.println("Received message type: " + message.getMessageType() +
                " from: " + message.getSender());

        if (messageListener != null) {
            messageListener.onMessageReceived(message);
        }

        switch (message.getMessageType()) {
            case NetworkMessage.LOGIN:
                handleLoginResponse(message);
                break;
            case NetworkMessage.GAME_STATE:
                inGame = true;
                break;
            case NetworkMessage.GAME_RESULT:
                System.out.println("Game result received: " + message.getContent());
                inGame = false;
                break;
            case NetworkMessage.PLAYER_DISCONNECT:
                inGame = false;
                break;
        }
    }

    private void handleLoginResponse(NetworkMessage message) {
        String response = (String) message.getContent();
        if (response.equals(NetworkMessage.SUCCESS)) {
            System.out.println("Login successful");
        } else {
            System.out.println("Login failed: " + response);
        }
    }

    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    public interface MessageListener {
        void onMessageReceived(NetworkMessage message);
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean isInGame() {
        return inGame;
    }

    public String getUsername() {
        return username;
    }
}