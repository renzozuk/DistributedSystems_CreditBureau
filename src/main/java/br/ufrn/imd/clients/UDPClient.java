package br.ufrn.imd.clients;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class UDPClient extends Client {
    public UDPClient(int port) {
        super(port);
    }

    public UDPClient(String address, int port) {
        super(address, port);
    }

    @Override
    public void startClient() {
        System.out.printf("UDP client connected on port %d.\n", super.getPort());

        Scanner sc = new Scanner(System.in);

        try (DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress inetAddress = InetAddress.getByName(super.getAddress());

            byte[] sendMessage;

            while (true) {
                System.out.print("Your command: ");

                String message = sc.nextLine();

                if("quit".equalsIgnoreCase(message)){
                    break;
                }

                sendMessage = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendMessage, sendMessage.length, inetAddress, super.getPort());
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

    public static String sendMessage(int port, String message) {
        return sendMessage("localhost", port, message);
    }

    public static String sendMessage(String address, int port, String message) {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress inetAddress = InetAddress.getByName(address);

            byte[] sendMessage = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendMessage, sendMessage.length, inetAddress, port);
            clientSocket.send(sendPacket);

            byte[] receiveMessage = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveMessage, receiveMessage.length);
            clientSocket.receive(receivePacket);

            return new String(receivePacket.getData(), 0, receivePacket.getLength());
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException ignored) {}

        return "";
    }
}
