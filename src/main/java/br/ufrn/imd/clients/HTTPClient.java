package br.ufrn.imd.clients;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class HTTPClient extends Client {
    public HTTPClient(int port) {
        super(port);
    }

    public HTTPClient(String address, int port) {
        super(address, port);
    }

    @Override
    public void startClient() {
        System.out.printf("HTTP Client started on port %d.\n", super.getPort());

        try {
            HttpClient client = HttpClient.newHttpClient();

            Scanner sc = new Scanner(System.in);

            while (true) {
                System.out.print("Your command: ");
                String message = sc.nextLine();
                String[] parameters = message.split(";");

                HttpRequest request = null;

                if (parameters[0].equalsIgnoreCase("quit")) {
                    client.close();
                    sc.close();
                    break;
                }

                switch (parameters[0].toLowerCase()) {
                    case "get":
                        request = HttpRequest.newBuilder()
                                .uri(new URI(String.format("http://%s:%d%s", super.getAddress(), super.getPort(), parameters[1])))
                                .GET()
                                .build();
                        break;
                    case "post":
                        request = HttpRequest.newBuilder()
                                .uri(new URI(String.format("http://%s:%d%s", super.getAddress(), super.getPort(), parameters[1])))
                                .POST(HttpRequest.BodyPublishers.noBody())
                                .build();
                        break;
                    case "put":
                        request = HttpRequest.newBuilder()
                                .uri(new URI(String.format("http://%s:%d%s", super.getAddress(), super.getPort(), parameters[1])))
                                .PUT(HttpRequest.BodyPublishers.noBody())
                                .build();
                        break;
                    case "delete":
                        request = HttpRequest.newBuilder()
                                .uri(new URI(String.format("http://%s:%d%s", super.getAddress(), super.getPort(), parameters[1])))
                                .DELETE()
                                .build();
                        break;
                }

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("Response Code: " + response.statusCode());
                System.out.println(response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
