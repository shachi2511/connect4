package client.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatPanel extends JPanel {
    private ClientGUI parent;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    public ChatPanel(ClientGUI parent) {
        this.parent = parent;
        setupPanel();
    }

    private void setupPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Chat"));
        setPreferredSize(new Dimension(250, 0));

        // Chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // Message input
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Send");

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        // Action listeners
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            parent.getClient().sendChatMessage(message);
            messageField.setText("");
        }
    }

    public void addMessage(String sender, String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String timestamp = sdf.format(new Date());
        chatArea.append("[" + timestamp + "] " + sender + ": " + message + "\n");

        // Scroll to bottom
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public void clearChat() {
        chatArea.setText("");
    }
}