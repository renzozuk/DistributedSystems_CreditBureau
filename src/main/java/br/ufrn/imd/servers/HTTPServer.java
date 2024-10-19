package br.ufrn.imd.servers;

import br.ufrn.imd.servers.handlers.HTTPMessageHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTPServer extends Server {
    public HTTPServer(int port) {
        super(port);

    }

    @Override
    public void startServer() {
        System.out.println("Webserver started.");

        try (ServerSocket serverSocket = new ServerSocket(super.getPort())) {
            while (true) {
                Socket remote = serverSocket.accept();
                new Thread(new HTTPMessageHandler(remote)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
