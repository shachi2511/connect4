package server;

import server.model.GameSession;
import shared.NetworkMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectFourServer {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();
    private Map<String, String> userCredentials = new HashMap<>();
    private List<GameSession> activeGames = new ArrayList<>();
    private List<ClientHandler> waitingPlayers = new ArrayList<>();
    private boolean running = false;
    private final String USER_DATA_FILE = "users.dat";

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        System.out.println("Server started on port " + port);

        // Load user credentials
        loadUserData();

        // Main server loop
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();

            } catch (IOException e) {
                if (running) {
                    System.out.println("Error accepting client connection: " + e.getMessage());
                }
            }
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing server: " + e.getMessage());
        }
    }

    public boolean authenticateUser(String username, String password) {
        String storedPassword = userCredentials.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }

    public boolean registerUser(String username, String password) {
        if (userCredentials.containsKey(username)) {
            return false;
        }
        userCredentials.put(username, password);
        saveUserData();
        return true;
    }

    public void addToWaitingList(ClientHandler client) {
        waitingPlayers.add(client);
        System.out.println(client.getUsername() + " added to waiting list");
        broadcastMessage(client.getUsername() + " is waiting for a game");
        matchPlayers();
    }

    public void removeFromWaitingList(ClientHandler client) {
        waitingPlayers.remove(client);
        System.out.println(client.getUsername() + " removed from waiting list");
    }

    public void matchPlayers() {
        if (waitingPlayers.size() >= 2) {
            ClientHandler player1 = waitingPlayers.remove(0);
            ClientHandler player2 = waitingPlayers.remove(0);

            GameSession gameSession = new GameSession(player1, player2);
            activeGames.add(gameSession);

            player1.setCurrentGame(gameSession);
            player2.setCurrentGame(gameSession);

            gameSession.startGame();

            System.out.println("Matched players: " + player1.getUsername() + " and " + player2.getUsername());
            broadcastMessage("Game started between " + player1.getUsername() + " and " + player2.getUsername());
        }
    }

    public void addClient(ClientHandler client) {
        clients.add(client);
        System.out.println("Client added: " + client.getUsername());
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        removeFromWaitingList(client);
        System.out.println("Client removed: " + (client.getUsername() != null ? client.getUsername() : "unknown"));
    }

    public void broadcastMessage(String message) {
        System.out.println("[SERVER]: " + message);
    }

    private void loadUserData() {
        File file = new File(USER_DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                userCredentials = (Map<String, String>) ois.readObject();
                System.out.println("Loaded " + userCredentials.size() + " user accounts");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error loading user data: " + e.getMessage());
                initializeDefaultUsers();
            }
        } else {
            initializeDefaultUsers();
        }
    }

    private void initializeDefaultUsers() {
        userCredentials.put("user1", "pass1");
        userCredentials.put("user2", "pass2");
        System.out.println("Initialized with default users");
    }

    private void saveUserData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_DATA_FILE))) {
            oos.writeObject(userCredentials);
            System.out.println("Saved " + userCredentials.size() + " user accounts");
        } catch (IOException e) {
            System.out.println("Error saving user data: " + e.getMessage());
        }
    }

    public void removeGameSession(GameSession session) {
        activeGames.remove(session);
        System.out.println("Game session removed. Active games: " + activeGames.size());
    }

    public int getActiveGameCount() {
        return activeGames.size();
    }

    public int getWaitingPlayersCount() {
        return waitingPlayers.size();
    }

    public int getConnectedClientCount() {
        return clients.size();
    }
}