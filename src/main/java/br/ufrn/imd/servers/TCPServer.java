package br.ufrn.imd.servers;

import br.ufrn.imd.util.RequisitionHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    public TCPServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("TCP server started on port %d.\n", port);

            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println("Received: " + message);

                        if (message.startsWith("quit")) {
                            break;
                        }

                        String reply = RequisitionHandler.processRequisition(message);
                        out.println(reply);
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
}
