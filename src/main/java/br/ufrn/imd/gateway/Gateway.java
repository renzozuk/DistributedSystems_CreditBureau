package br.ufrn.imd.gateway;

import br.ufrn.imd.clients.HTTPClient;
import br.ufrn.imd.clients.UDPClient;
import br.ufrn.imd.model.entities.enums.ProtocolType;
import br.ufrn.imd.patterns.Heartbeat;
import br.ufrn.imd.servers.HTTPServer;
import br.ufrn.imd.servers.TCPServer;
import br.ufrn.imd.servers.handlers.HTTPMessageHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.StringTokenizer;

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
                break;
            case HTTP:
                startHTTPListener(quantity);
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

    public void startTCPListener(int quantity) {}

    public void startHTTPListener(int quantity) {
        Random random = new Random();

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            while (true) {
                Socket socket = serverSocket.accept();

                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String headerLine = in.readLine();
                    StringTokenizer tokenizer = new StringTokenizer(headerLine);
                    String httpMethod = tokenizer.nextToken();

                    String httpQueryString = tokenizer.nextToken();

                    int randomPort = random.nextInt(11001, 11001 + quantity);

                    if (Heartbeat.sendHeartbeatToHTTPServer(randomPort).equals(HEARTBEAT_SUCCESS_RESPONSE)) {
                        HttpResponse<String> response = HTTPClient.sendMessage(11001, httpMethod, httpQueryString);

                        HTTPMessageHandler.sendResponse(socket, response.statusCode(), response.body());
                    } else {
                        HTTPMessageHandler.sendResponse(socket, 500, "Server is not alive.");
                    }
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
