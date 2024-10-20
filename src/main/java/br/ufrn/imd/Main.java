package br.ufrn.imd;

import br.ufrn.imd.clients.Client;
import br.ufrn.imd.clients.ClientFactory;
import br.ufrn.imd.gateway.Gateway;
import br.ufrn.imd.model.entities.enums.ProtocolType;
import br.ufrn.imd.servers.Server;
import br.ufrn.imd.servers.ServerFactory;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        switch (args[0].toLowerCase()) {
            case "client":
                if (args.length == 3) {
                    Client client = ClientFactory.build(Integer.parseInt(args[2]), ProtocolType.valueOf(args[1].toUpperCase()));
                    client.startClient();
                } else if (args.length == 4) {
                    Client client = ClientFactory.build(args[2], Integer.parseInt(args[3]), ProtocolType.valueOf(args[1].toUpperCase()));
                    client.startClient();
                }
                break;
            case "gateway":
                Gateway gateway = new Gateway(ProtocolType.valueOf(args[1].toUpperCase()));
                gateway.startGateway(Integer.parseInt(args[2]));
            case "server":
                if (args.length == 3) {
                    Server server = ServerFactory.build(Integer.parseInt(args[2]), ProtocolType.valueOf(args[1].toUpperCase()));
                    server.startServer();
                } else if (args.length == 4) {
                    Server server = ServerFactory.build(args[2], Integer.parseInt(args[3]), ProtocolType.valueOf(args[1].toUpperCase()));
                    server.startServer();
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown command: " + args[0]);
        }

    }
}
