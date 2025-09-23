package client;

import client.ConnectFourClient;
import client.gui.ClientGUI;
import shared.NetworkMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class MultiClientTest {

    public static void main(String[] args) {
        System.out.println("Starting Multi-Client Test...");

        // Configure test settings
        String host = "localhost";
        int port = 8888;

        // User info for testing
        String[] usernames = {"player1", "player2"};
        String[] passwords = {"pass1", "pass2"};

        // Launch multiple client instances
        SwingUtilities.invokeLater(() -> {
            // Create and position first client window
            ClientGUI client1 = new ClientGUI();
            client1.setTitle("Connect Four - " + usernames[0]);
            client1.setLocation(100, 100);
            client1.setVisible(true);


            ClientGUI client2 = new ClientGUI();
            client2.setTitle("Connect Four - " + usernames[1]);
            client2.setLocation(950, 100);
            client2.setVisible(true);

        });

    }
}