package br.ufrn.imd.clients;

import br.ufrn.imd.model.entities.enums.ProtocolType;

public class ClientFactory {
    public static Client build(int port, ProtocolType protocolType) {
        return switch (protocolType) {
            case UDP -> new UDPClient(port);
            case TCP -> new TCPClient(port);
            case HTTP -> new HTTPClient(port);
        };
    }

    public static Client build(String address, int port, ProtocolType protocolType) {
        return switch (protocolType) {
            case UDP -> new UDPClient(address, port);
            case TCP -> new TCPClient(address, port);
            case HTTP -> new HTTPClient(address, port);
        };
    }
}
