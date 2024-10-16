package br.ufrn.imd.application;

import br.ufrn.imd.util.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTPServer implements Gateway {
    public HTTPServer(int port) {
        System.out.println("Webserver started.");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket remote = serverSocket.accept();
                new Thread(new ClientHandler(remote)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        if (args.length > 0) {
            new HTTPServer(Integer.parseInt(args[0]));
        } else {
            new HTTPServer(8080);
        }
    }

    @Override
    public void execute() {

    }
}
