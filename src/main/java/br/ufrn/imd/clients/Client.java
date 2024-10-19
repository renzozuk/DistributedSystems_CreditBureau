package br.ufrn.imd.clients;

public abstract class Client {
    private final int port;

    public Client(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
