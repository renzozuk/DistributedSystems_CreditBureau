package br.ufrn.imd.application;

import java.util.HashMap;
import java.util.Map;

public class GatewayFactory {
    private final Map<String, Gateway> gateways = new HashMap<>();

    public GatewayFactory() {
        UDPServer udpServer = new UDPServer(8080);
        TCPServer tcpServer = new TCPServer(8081);
        HTTPServer httpServer = new HTTPServer(8082);
    }

    public void registerGateway(String key, Gateway gateway) {
        gateways.put(key, gateway);
    }

    public Gateway getGateway(String key) {
        return gateways.get(key);
    }
}
