package com.example.demo1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientGUI extends JFrame {
    private final PrintWriter out;
    private final BufferedReader in;
    private final JTextArea chatArea;
    private final JTextField inputField;
    private final String username;

    public ClientGUI(String username) throws IOException {
        this.username = username;
        setTitle("Chat Application - " + username);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        panel.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        inputField = new JTextField();
        panel.add(inputField, BorderLayout.SOUTH);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        panel.add(sendButton, BorderLayout.EAST);

        add(panel);

        Socket socket = new Socket("localhost", 8080);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Send the username to the server
        out.println(username);

        new Thread(new IncomingReader()).start();
    }

    private void sendMessage() {
        String message = inputField.getText();
        out.println(username + ": " + message);
        chatArea.append(username + ": " + message + "\n"); // Append message to chat area
        inputField.setText("");
    }





    private class IncomingReader implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    chatArea.append(message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String username = JOptionPane.showInputDialog("Enter your username:");
        if (username != null && !username.isEmpty()) {
            SwingUtilities.invokeLater(() -> {
                try {
                    new ClientGUI(username).setVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            JOptionPane.showMessageDialog(null, "Username cannot be empty. Exiting.");
        }
    }
}

