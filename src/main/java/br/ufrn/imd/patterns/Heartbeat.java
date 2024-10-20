package br.ufrn.imd.patterns;

import br.ufrn.imd.servers.Server;
import br.ufrn.imd.servers.UDPServer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.stream.IntStream;

public class Heartbeat {
    private static final String HEARTBEAT_MESSAGE = "Are you alive?";
    private static final int TIMEOUT = 2000;

    public static void refreshServers(Set<Server> servers, int quantity) {
        servers.removeIf(server -> !sendHeartbeat(server.getPort()).equals("Yes, I'm alive!"));

        IntStream.range(9001, 9001 + quantity).forEach(i -> {
            if (sendHeartbeat(i).equals("Yes, I'm alive!")) {
                servers.add(new UDPServer(i));
            }
        });
    }

    public static String sendHeartbeat(int port) {
        return sendHeartbeat("localhost", port);
    }

    public static String sendHeartbeat(String address, int port) {
        try (DatagramSocket socket = new DatagramSocket()) {
            try {
                InetAddress serverAddress = InetAddress.getByName(address);
                try {
                    byte[] buffer = HEARTBEAT_MESSAGE.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, port);
                    socket.send(packet);

                    socket.setSoTimeout(5);
                    byte[] responseBuffer = new byte[1024];
                    DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);

                    try {
                        socket.receive(responsePacket);
                        return new String(responsePacket.getData(), 0, responsePacket.getLength());
                    } catch (Exception e) {
                        return "No response from server. It may be down.";
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        return "No response from server. It may be down.";
    }
}
