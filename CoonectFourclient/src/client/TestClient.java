package client.test;

import client.ConnectFourClient;
import shared.NetworkMessage;

import java.util.Scanner;

public class TestClient {
    public static void main(String[] args) {
        System.out.println("Connect Four Client Test");
        ConnectFourClient client = new ConnectFourClient();

        client.setMessageListener(message -> {
            System.out.println("Received from server: " + message.getSender() +
                    " - Type: " + message.getMessageType() +
                    " - Content: " + message.getContent());
        });

        // Connect to server
        if (!client.connect("localhost", 8888)) {
            System.out.println("Failed to connect to server. Exiting.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n===== Connect Four Client Menu =====");
            System.out.println("1. Login");
            System.out.println("2. Create Account");
            System.out.println("3. Join Game");
            System.out.println("4. Make Move");
            System.out.println("5. Send Chat Message");
            System.out.println("6. Quit Game");
            System.out.println("7. Logout");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    System.out.print("Username: ");
                    String username = scanner.nextLine();
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    client.login(username, password);
                    break;

                case "2":
                    System.out.print("New Username: ");
                    String newUsername = scanner.nextLine();
                    System.out.print("New Password: ");
                    String newPassword = scanner.nextLine();
                    client.createAccount(newUsername, newPassword);
                    break;

                case "3":
                    client.joinGame();
                    break;

                case "4":
                    System.out.print("Enter column (0-6): ");
                    try {
                        int column = Integer.parseInt(scanner.nextLine());
                        client.makeMove(column);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid column number");
                    }
                    break;

                case "5":
                    System.out.print("Enter message: ");
                    String message = scanner.nextLine();
                    client.sendChatMessage(message);
                    break;

                case "6":
                    client.quitGame();
                    break;

                case "7":
                    client.logout();
                    break;

                case "8":
                    client.disconnect();
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option");
                    break;
            }
        }

        System.out.println("Test client closed");
    }
}