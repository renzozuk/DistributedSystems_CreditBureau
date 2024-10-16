package br.ufrn.imd.application;

import br.ufrn.imd.util.RequisitionHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Gateway {
    public TCPServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("TCP server started on port %d.\n", port);

            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter outputStream = new PrintWriter(socket.getOutputStream(), true)) {

                    String message;
                    while ((message = inputStream.readLine()) != null) {
                        System.out.println("Received: " + message);

                        if (message.startsWith("quit")) {
                            System.out.println("Client requested to quit.");
                            break;
                        }

                        String reply = RequisitionHandler.processRequisition(message);
                        outputStream.println(reply);
                    }

                } catch (IOException e) {
                    System.err.println("Error handling client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not start server: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            new TCPServer(Integer.parseInt(args[0]));
        } else {
            new TCPServer(8080);
        }
    }

    @Override
    public void execute() {

    }
}
