package server;

import server.gui.ServerGUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Connect Four Server...");

        SwingUtilities.invokeLater(() -> {
            ServerGUI gui = new ServerGUI();
            gui.setVisible(true);
        });
    }
}