package br.ufrn.imd.servers;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Server extends Thread {
    private ExecutorService executorService;
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public abstract void startServer();

    @Override
    public void run() {
        if (executorService == null || executorService.isTerminated()) {
            executorService = Executors.newVirtualThreadPerTaskExecutor();
        }

        startServer();

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
