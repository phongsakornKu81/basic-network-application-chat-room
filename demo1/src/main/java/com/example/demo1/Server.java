package com.example.demo1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final int port;
    private final List<ClientHandler> clients = new ArrayList<>();

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message, String username, ClientHandler sender) {
        String messageWithUsername =  ": " + message;
        System.out.println("Status code: 200 OK");
        System.out.println("Broadcasting message: " + messageWithUsername);

        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(messageWithUsername);
            }
        }
    }


    public void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client disconnected: " + client.getClientSocket());
    }

    public static void main(String[] args) {
        Server server = new Server(8080);
        server.start();
    }
}
