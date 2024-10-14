package br.ufrn.imd.application;

import br.ufrn.imd.util.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
    public WebServer(int port) {
        System.out.println("Webserver Started");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                System.out.println("Waiting for client request");
                Socket remote = serverSocket.accept();
                System.out.println("Connection made");
                new Thread(new ClientHandler(remote)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        if (args.length > 0) {
            new WebServer(Integer.parseInt(args[0]));
        } else {
            new WebServer(8080);
        }
    }
}
