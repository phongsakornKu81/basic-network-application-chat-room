package com.example.demo1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Server server;
    private final PrintWriter out;
    private final BufferedReader in;

    public ClientHandler(Socket clientSocket, Server server) throws IOException {
        this.clientSocket = clientSocket;
        this.server = server;
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            // Read the username sent by the client
            String username = in.readLine();
            System.out.println("Client connected: " + username);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                server.broadcastMessage(inputLine, username, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.removeClient(this);
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public void sendMessage(String message) {
        out.println(message);
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
}


