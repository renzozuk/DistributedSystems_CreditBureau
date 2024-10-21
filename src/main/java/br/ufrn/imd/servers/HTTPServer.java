package br.ufrn.imd.servers;

import br.ufrn.imd.servers.handlers.HTTPMessageHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTPServer extends Server {
    public HTTPServer(int port) {
        this("localhost", port);
    }

    public HTTPServer(String address, int port) {
        super(address, port);

        if (port != 8080 && (port < 11001 || port > 12000)) {
            throw new IllegalArgumentException("Invalid port number. The port number must be between 11001 and 12000.");
        }
    }

    @Override
    public void startServer() {
        System.out.printf("HTTP server started on port %d.\n", super.getPort());

        try (ServerSocket serverSocket = new ServerSocket(super.getPort())) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new HTTPMessageHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
