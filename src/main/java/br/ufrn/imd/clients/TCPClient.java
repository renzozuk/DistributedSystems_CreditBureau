package br.ufrn.imd.clients;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient extends Client {
    public TCPClient(int port) {
        super(port);
    }

    public TCPClient(String address, int port) {
        super(address, port);
    }

    @Override
    public void startClient() {
        System.out.printf("TCP Client started on port %d.\n", super.getPort());

        try (Socket socket = new Socket(super.getAddress(), super.getPort());
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner sc = new Scanner(System.in)) {

            while (true) {
                System.out.print("Your command: ");
                String message = sc.nextLine();

                if ("quit".equalsIgnoreCase(message)) {
                    break;
                }

                out.write(message);

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
}
