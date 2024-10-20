package br.ufrn.imd.servers;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Server extends Thread {
    private ExecutorService executorService;
    private final String address;
    private final int port;

    public Server(int port) {
        this.address = "localhost";
        this.port = port;
    }

    public Server(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public abstract void startServer() throws IOException;

    @Override
    public void run() {
        if (executorService == null || executorService.isTerminated()) {
            executorService = Executors.newVirtualThreadPerTaskExecutor();
        }

        try {
            startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        executorService.shutdown();
    }

    @Override
    public boolean equals(Object o) {
        return this.getClass() == o.getClass() && this.port == ((Server) o).port;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(port);
    }
}
