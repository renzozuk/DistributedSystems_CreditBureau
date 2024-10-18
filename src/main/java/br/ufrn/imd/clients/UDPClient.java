package br.ufrn.imd.clients;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class UDPClient {
    public UDPClient(int port) {
        System.out.printf("UDP client connected on port %d.\n", port);

        Scanner sc = new Scanner(System.in);

        try (DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress inetAddress = InetAddress.getByName("localhost");

            byte[] sendMessage;

            while (true) {
                System.out.print("Your command: ");

                String message = sc.nextLine();

                if("quit".equalsIgnoreCase(message)){
                    break;
                }

                sendMessage = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendMessage, sendMessage.length, inetAddress, port);
                clientSocket.send(sendPacket);

                byte[] receiveMessage = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveMessage, receiveMessage.length);
                clientSocket.receive(receivePacket);
                System.out.println(new String(receivePacket.getData(), 0, receivePacket.getLength()));

            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException ignored) {}

        System.out.println("UDP client terminating.");

        sc.close();
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            new UDPClient(Integer.parseInt(args[0]));
        } else {
            new UDPClient(8080);
        }
    }
}
