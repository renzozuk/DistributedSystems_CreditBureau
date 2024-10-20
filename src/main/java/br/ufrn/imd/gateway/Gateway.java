package br.ufrn.imd.gateway;

import br.ufrn.imd.patterns.Heartbeat;
import br.ufrn.imd.servers.HTTPServer;
import br.ufrn.imd.servers.Server;
import br.ufrn.imd.servers.TCPServer;
import br.ufrn.imd.servers.UDPServer;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Gateway {
    private final UDPServer udpServer;
    private final TCPServer tcpServer;
    private final HTTPServer httpServer;
    private Set<Server> servers;

    public Gateway() {
        this.udpServer = new UDPServer(8080);
        this.tcpServer = new TCPServer(8081);
        this.httpServer = new HTTPServer(8082);

        servers = new HashSet<>();

        ExecutorService executor = Executors.newFixedThreadPool(4);
        CountDownLatch latch = new CountDownLatch(3);

        try {
            executor.submit(udpServer::startServer);
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            latch.countDown();
        }

        try {
            executor.submit(tcpServer::startServer);
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            latch.countDown();
        }

        try {
            executor.submit(httpServer::startServer);
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
                    Heartbeat.refreshServers(servers);
                    System.out.printf("There are %d servers up.\n", servers.size());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        executor.close();
    }

    public static void main(String[] args) {
        new Gateway();
    }
}
