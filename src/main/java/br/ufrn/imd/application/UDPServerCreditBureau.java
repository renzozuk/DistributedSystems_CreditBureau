package br.ufrn.imd.application;

import br.ufrn.imd.util.RequisitionHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServerCreditBureau {
    public UDPServerCreditBureau(int port) {
        System.out.printf("UDP server started on port %d.\n", port);

        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
            while (true) {
                byte[] receiveMessage = new byte[1024];

                DatagramPacket receivePacket = new DatagramPacket(receiveMessage, receiveMessage.length);

                serverSocket.receive(receivePacket);

                String message = new String(receivePacket.getData());

                String reply = RequisitionHandler.processRequisition(message);

                byte[] replyMessage = reply.getBytes();

                DatagramPacket datagramPacket = new DatagramPacket(replyMessage, replyMessage.length, receivePacket.getAddress(), receivePacket.getPort());

                serverSocket.send(datagramPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            new UDPServerCreditBureau(Integer.parseInt(args[0]));
        } else {
            new UDPServerCreditBureau(8080);
        }
    }
}
