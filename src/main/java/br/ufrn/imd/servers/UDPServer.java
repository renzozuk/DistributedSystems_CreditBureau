package br.ufrn.imd.servers;

import br.ufrn.imd.servers.handlers.UDPMessageHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer extends Server {
    public UDPServer(int port) {
        super(port);
    }

    public UDPServer(String address, int port) {
        super(address, port);
    }

    @Override
    public void startServer() {
        System.out.printf("UDP server started on port %d.\n", super.getPort());

        try (DatagramSocket serverSocket = new DatagramSocket(super.getPort())) {
            while (true) {
                byte[] receiveMessage = new byte[1024];

                DatagramPacket receivePacket = new DatagramPacket(receiveMessage, receiveMessage.length);

                serverSocket.receive(receivePacket);

                String reply = UDPMessageHandler.handleRequest(new String(receivePacket.getData()));

                byte[] replyMessage = reply.getBytes();

                DatagramPacket datagramPacket = new DatagramPacket(replyMessage, replyMessage.length, receivePacket.getAddress(), receivePacket.getPort());

                serverSocket.send(datagramPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
