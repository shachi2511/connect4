package client;

import client.gui.ClientGUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Connect Four Client...");

        SwingUtilities.invokeLater(() -> {
            ClientGUI gui = new ClientGUI();
            gui.setVisible(true);
        });
    }
}