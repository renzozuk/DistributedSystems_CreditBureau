package br.ufrn.imd.gateway;

import br.ufrn.imd.clients.UDPClient;
import br.ufrn.imd.model.entities.enums.ProtocolType;
import br.ufrn.imd.patterns.Heartbeat;
import br.ufrn.imd.servers.HTTPServer;
import br.ufrn.imd.servers.TCPServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.Random;

import static br.ufrn.imd.patterns.Heartbeat.HEARTBEAT_SUCCESS_RESPONSE;

public class Gateway {
    private final int GATEWAY_PORT = 8080;
    private final ProtocolType protocolType;

    public Gateway(ProtocolType protocolType) {
        this.protocolType = protocolType;
    }

    public void startGateway(int quantity) {
        switch (protocolType) {
            case UDP:
                startUDPListener(quantity);
                break;
            case TCP:
                TCPServer tcpServer = new TCPServer(GATEWAY_PORT);
                tcpServer.start();
                break;
            case HTTP:
                HTTPServer httpServer = new HTTPServer(GATEWAY_PORT);
                httpServer.start();
                break;
        }
    }

    public void startUDPListener(int quantity) {
        Random random = new Random();

        try (DatagramSocket serverSocket = new DatagramSocket(8080)) {

            while (true) {
                byte[] receiveMessage = new byte[1024];

                DatagramPacket receivePacket = new DatagramPacket(receiveMessage, receiveMessage.length);

                serverSocket.receive(receivePacket);

                int randomPort = random.nextInt(9001, 9001 + quantity);

                String reply = Heartbeat.sendHeartbeatToUDPServer(randomPort).equals(HEARTBEAT_SUCCESS_RESPONSE) ? UDPClient.sendMessage(randomPort, new String(receivePacket.getData())) : "Error: Server is not alive.";

                byte[] replyMessage = reply.getBytes();

                DatagramPacket datagramPacket = new DatagramPacket(replyMessage, replyMessage.length, receivePacket.getAddress(), receivePacket.getPort());

                serverSocket.send(datagramPacket);
            }
        } catch (SocketTimeoutException ignored) {} catch (IOException e) {
            e.printStackTrace();
        }
    }
}
