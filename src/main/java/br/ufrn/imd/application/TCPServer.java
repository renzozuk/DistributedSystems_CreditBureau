package br.ufrn.imd.application;

import br.ufrn.imd.util.RequisitionHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    public TCPServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress("localhost", port));

            System.out.printf("TCP server started on port %d.\n", port);

            while (true) {
                Socket socket = serverSocket.accept();

                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                byte[] buffer = new byte[1024];
                int bytesRead = inputStream.read(buffer);
                String message = new String(buffer, 0, bytesRead);

                String reply = RequisitionHandler.processRequisition(message);

                outputStream.write(reply.getBytes());
                outputStream.flush();

                outputStream.close();
                inputStream.close();
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
