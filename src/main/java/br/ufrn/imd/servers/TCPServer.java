package br.ufrn.imd.servers;

import br.ufrn.imd.servers.handlers.TCPMessageHandler;
import br.ufrn.imd.servers.handlers.UDPMessageHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends Server {
    public TCPServer(int port) {
        super(port);
    }

    @Override
    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(super.getPort())) {
            System.out.printf("TCP server started on port %d.\n", super.getPort());

            while (true) {
                Socket remote = serverSocket.accept();
                new Thread(new TCPMessageHandler(remote)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
