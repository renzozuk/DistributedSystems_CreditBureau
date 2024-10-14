package br.ufrn.imd.application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {
    public TCPClient(int port) {
        Scanner sc = new Scanner(System.in);

        try (Socket socket = new Socket("localhost", port)) {
            while (true) {
                System.out.print("Your command: ");

                OutputStream outputStream = socket.getOutputStream();

                String message = sc.nextLine();

                if("quit".equalsIgnoreCase(message)){
                    break;
                }
                outputStream.write(message.getBytes());

                InputStream inputStream = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int bytesRead = inputStream.read(buffer);
                String reply = new String(buffer, 0, bytesRead);

                System.out.println(reply);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sc.close();
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            new TCPClient(Integer.parseInt(args[0]));
        } else {
            new TCPClient(8080);
        }
    }
}
