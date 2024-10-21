package br.ufrn.imd.gateway;

import br.ufrn.imd.model.entities.enums.ProtocolType;
import br.ufrn.imd.patterns.Heartbeat;
import br.ufrn.imd.servers.HTTPServer;
import br.ufrn.imd.servers.Server;
import br.ufrn.imd.servers.TCPServer;
import br.ufrn.imd.servers.UDPServer;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Gateway {
    private final int GATEWAY_PORT = 8080;
    private final ProtocolType protocolType;
    private Set<Server> servers;

    public Gateway(ProtocolType protocolType) {
        this.protocolType = protocolType;
        servers = new HashSet<>();
    }

    public void startGateway(int quantity) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);

        try {
            switch (protocolType) {
                case UDP:
                    UDPServer udpServer = new UDPServer(GATEWAY_PORT);
                    udpServer.start();
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
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            latch.countDown();
        }

        executor.submit(() -> {
            try {
                latch.await();

                while (true) {
                    Heartbeat.refreshServers(servers, Math.min(1000, quantity));
//                    System.out.printf("There are %d servers up.\n", servers.size());
                    if (!servers.isEmpty()) {
                        Random random = new Random();


                        System.out.printf("The server found was %d.\n", servers.stream().toList().get(random.nextInt(servers.size())).getPort());
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        executor.close();
    }
}
