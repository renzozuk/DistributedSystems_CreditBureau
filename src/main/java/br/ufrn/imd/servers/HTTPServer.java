package br.ufrn.imd.servers;

import br.ufrn.imd.servers.handlers.HTTPMessageHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTPServer extends Server {
    public HTTPServer(int port) {
        super(port);
    }

    public HTTPServer(String address, int port) {
        super(address, port);
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
