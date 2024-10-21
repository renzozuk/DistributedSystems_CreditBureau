package br.ufrn.imd.patterns;

import br.ufrn.imd.servers.Server;
import br.ufrn.imd.servers.UDPServer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;
import java.util.stream.IntStream;

public class Heartbeat {
    public static final String HEARTBEAT_MESSAGE = "Are you alive?";
    public static final String HEARTBEAT_SUCCESS_RESPONSE = "Yes, I'm alive!";
    public static final String HEARTBEAT_FAIL_RESPONSE = "No response from server. It may be down.";
//    private static final int TIMEOUT = 2000;

    public static void refreshServers(Set<Server> servers, int quantity) {
        servers.removeIf(server -> !sendHeartbeatToUDPServer(server.getPort()).equals(HEARTBEAT_SUCCESS_RESPONSE));

        IntStream.range(9001, 9001 + quantity).forEach(i -> {
            if (sendHeartbeatToUDPServer(i).equals("Yes, I'm alive!")) {
                servers.add(new UDPServer(i));
            }
        });
    }

    public static String sendHeartbeatToUDPServer(int port) {
        return sendHeartbeatToUDPServer("localhost", port);
    }

    public static String sendHeartbeatToUDPServer(String address, int port) {
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
                        return HEARTBEAT_FAIL_RESPONSE;
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

        return HEARTBEAT_FAIL_RESPONSE;
    }

    public static String sendHeartbeatToHTTPServer(int port) {
        return sendHeartbeatToHTTPServer("localhost", port);
    }

    public static String sendHeartbeatToHTTPServer(String address, int port) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI(String.format("http://%s:%d/heart", address, port)))
                    .GET()
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            return HEARTBEAT_FAIL_RESPONSE;
        }

        return response.body().equals("beat") ? HEARTBEAT_SUCCESS_RESPONSE : "Unexpected response.";
    }
}
