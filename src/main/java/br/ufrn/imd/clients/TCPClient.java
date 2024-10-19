package br.ufrn.imd.clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient extends Client {
    public TCPClient(int port) {
        super(port);

        try (Socket socket = new Socket("localhost", port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner sc = new Scanner(System.in)) {

            while (true) {
                System.out.print("Your command: ");
                String message = sc.nextLine();

                if ("quit".equalsIgnoreCase(message)) {
                    break;
                }

                out.println(message);

                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                    if (line.isEmpty()) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error communicating with the server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            new TCPClient(Integer.parseInt(args[0]));
        } else {
            new TCPClient(8080);
        }
    }
}
