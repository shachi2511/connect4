package server.gui;

import server.ConnectFourServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerGUI extends JFrame {
    private ConnectFourServer server;
    private JTextArea logArea;
    private JButton startButton;
    private JButton stopButton;
    private JTextField portField;
    private JLabel statusLabel;

    public ServerGUI() {
        server = new ConnectFourServer();
        setupMainFrame();
        setupComponents();
    }

    private void setupMainFrame() {
        setTitle("Connect Four Server");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (server != null) {
                    server.stop();
                }
            }
        });

        setLayout(new BorderLayout());
    }

    private void setupComponents() {
        // Control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder(BorderFactory.createTitledBorder("Server Controls"));

        portField = new JTextField("8888", 5);
        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        stopButton.setEnabled(false);
        statusLabel = new JLabel("Server not running", JLabel.CENTER);

        controlPanel.add(new JLabel("Port:"));
        controlPanel.add(portField);
        controlPanel.add(startButton);
        controlPanel.add(stopButton);

        add(controlPanel, BorderLayout.NORTH);
        add(statusLabel, BorderLayout.SOUTH);

        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);

        // Redirect system out to text area
        redirectSystemOut();

        // Action listeners
        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e -> stopServer());
    }

    private void startServer() {
        try {
            int port = Integer.parseInt(portField.getText());

            // Start the server in a new thread
            new Thread(() -> {
                try {
                    server.start(port);
                } catch (IOException ex) {
                    SwingUtilities.invokeLater(() -> {
                        appendLog("Error starting server: " + ex.getMessage());
                        updateStatus("Server failed to start");
                        startButton.setEnabled(true);
                        stopButton.setEnabled(false);
                    });
                }
            }).start();

            updateStatus("Server running on port " + port);
            startButton.setEnabled(false);
            stopButton.setEnabled(true);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid port number", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void stopServer() {
        server.stop();
        updateStatus("Server stopped");
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    public void appendLog(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(new Date());
        logArea.append("[" + timestamp + "] " + message + "\n");

        // Scroll to bottom
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void clearLog() {
        logArea.setText("");
    }

    public void updateStatus(String status) {
        statusLabel.setText(status);
    }

    private void redirectSystemOut() {
        PrintStream printStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                String newText = String.valueOf((char) b);
                SwingUtilities.invokeLater(() -> logArea.append(newText));
            }

            @Override
            public void write(byte[] b, int off, int len) {
                String newText = new String(b, off, len);
                SwingUtilities.invokeLater(() -> logArea.append(newText));
            }
        });

        System.setOut(printStream);
        System.setErr(printStream);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ServerGUI gui = new ServerGUI();
            gui.setVisible(true);
        });
    }
}
