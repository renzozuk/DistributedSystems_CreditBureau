package br.ufrn.imd.application;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class UDPClientCreditBureau {
    public UDPClientCreditBureau() {
        System.out.println("UDP client started.");

        Scanner sc = new Scanner(System.in);

        try {
            DatagramSocket clientSocket = new DatagramSocket();

            InetAddress inetAddress = InetAddress.getByName("localhost");

            byte[] sendMessage;

            while (true) {
                System.out.print("Enter a message: ");

                String message = sc.nextLine();

                if("quit".equalsIgnoreCase(message)){
                    break;
                }

                sendMessage = message.getBytes();

                DatagramPacket sendPacket = new DatagramPacket(sendMessage, sendMessage.length, inetAddress, 8080);

                clientSocket.send(sendPacket);
            }

            clientSocket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException ignored) {}

        System.out.println("UDP client terminating.");
    }

    public static void main(String[] args) {
        new UDPClientCreditBureau();
    }
}
