package br.ufrn.imd.clients;

public abstract class Client {
    private final String address;
    private final int port;

    public Client(int port) {
        this("localhost", port);
    }

    public Client(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public abstract void startClient();
}
