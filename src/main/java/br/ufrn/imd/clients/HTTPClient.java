package br.ufrn.imd.clients;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

                request = switch (parameters[0].toLowerCase()) {
                    case "get" -> HttpRequest.newBuilder()
                            .uri(new URI(String.format("http://%s:%d%s", super.getAddress(), super.getPort(), parameters[1])))
                            .GET()
                            .build();
                    case "post" -> HttpRequest.newBuilder()
                            .uri(new URI(String.format("http://%s:%d%s", super.getAddress(), super.getPort(), parameters[1])))
                            .POST(HttpRequest.BodyPublishers.noBody())
                            .build();
                    case "put" -> HttpRequest.newBuilder()
                            .uri(new URI(String.format("http://%s:%d%s", super.getAddress(), super.getPort(), parameters[1])))
                            .PUT(HttpRequest.BodyPublishers.noBody())
                            .build();
                    case "delete" -> HttpRequest.newBuilder()
                            .uri(new URI(String.format("http://%s:%d%s", super.getAddress(), super.getPort(), parameters[1])))
                            .DELETE()
                            .build();
                    default -> request;
                };

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("Response Code: " + response.statusCode());
                System.out.println(response.body());
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpResponse<String> sendMessage(int port, String httpMethod, String message) {
        return sendMessage("localhost", port, httpMethod, message);
    }

    public static HttpResponse<String> sendMessage(String address, int port, String httpMethod, String message) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            switch (httpMethod) {
                case "GET":
                    return client.send(HttpRequest.newBuilder()
                            .uri(new URI(String.format("http://%s:%d%s", address, port, message)))
                            .GET()
                            .build(), HttpResponse.BodyHandlers.ofString());
                case "POST":
                    return client.send(HttpRequest.newBuilder()
                            .uri(new URI(String.format("http://%s:%d%s", address, port, message)))
                            .POST(HttpRequest.BodyPublishers.noBody())
                            .build(), HttpResponse.BodyHandlers.ofString());
                case "PUT":
                    return client.send(HttpRequest.newBuilder()
                            .uri(new URI(String.format("http://%s:%d%s", address, port, message)))
                            .PUT(HttpRequest.BodyPublishers.noBody())
                            .build(), HttpResponse.BodyHandlers.ofString());
                case "DELETE":
                    return client.send(HttpRequest.newBuilder()
                            .uri(new URI(String.format("http://%s:%d%s", address, port, message)))
                            .DELETE()
                            .build(), HttpResponse.BodyHandlers.ofString());

            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
