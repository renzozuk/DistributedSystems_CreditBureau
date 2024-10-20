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
