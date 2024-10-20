package br.ufrn.imd.servers;

import br.ufrn.imd.model.entities.enums.ProtocolType;

public class ServerFactory {
    public static Server build(int port, ProtocolType protocolType) {
        return switch (protocolType) {
            case UDP -> new UDPServer(port);
            case TCP -> new TCPServer(port);
            case HTTP -> new HTTPServer(port);
        };
    }

    public static Server build(String address, int port, ProtocolType protocolType) {
        return switch (protocolType) {
            case UDP -> new UDPServer(address, port);
            case TCP -> new TCPServer(address, port);
            case HTTP -> new HTTPServer(address, port);
        };
    }
}
