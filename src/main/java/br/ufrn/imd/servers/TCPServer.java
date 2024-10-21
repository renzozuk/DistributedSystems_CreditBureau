package br.ufrn.imd.servers;

import br.ufrn.imd.servers.handlers.TCPMessageHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends Server {
    public TCPServer(int port) {
        this("localhost", port);
    }

    public TCPServer(String address, int port) {
        super(address, port);

        if (port != 8080 && (port < 10001 || port > 11000)) {
            throw new IllegalArgumentException("Invalid port number. The port number must be between 10001 and 11000.");
        }
    }

    @Override
    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(super.getPort())) {
            System.out.printf("TCP server started on port %d.\n", super.getPort());

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new TCPMessageHandler(socket)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
