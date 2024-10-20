package br.ufrn.imd.clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class HTTPClient extends Client {
    public HTTPClient(int port) {
        super(port);
    }

    public HTTPClient(String address, int port) {
        super(address, port);
    }

    @Override
    public void startClient() {
        System.out.printf("HTTP Client started on port %d.", super.getPort());

        try {
            InetAddress serverInetAddress = InetAddress.getByName("localhost");
            Socket connection = new Socket(serverInetAddress, super.getPort());
            try (OutputStream out = connection.getOutputStream();
                 BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                sendGet(out);
                System.out.println(getResponse(in));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendGet(OutputStream out) {
        try {
            out.write("GET /default\r\n".getBytes());
            out.write("User-Agent: Mozilla/5.0\r\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getResponse(BufferedReader in) {
        try {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine).append("\n");
            }
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
