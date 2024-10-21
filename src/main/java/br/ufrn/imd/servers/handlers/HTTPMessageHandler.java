package br.ufrn.imd.servers.handlers;

import br.ufrn.imd.db.DbException;
import br.ufrn.imd.model.dao.CustomerDao;
import br.ufrn.imd.model.dao.DaoFactory;
import br.ufrn.imd.model.dao.ScoreDao;
import br.ufrn.imd.model.entities.Customer;
import br.ufrn.imd.model.entities.Score;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.StringTokenizer;

public class HTTPMessageHandler implements Runnable {
    private final Socket socket;
    private final CustomerDao customerDao = DaoFactory.createCustomerDao();
    private final ScoreDao scoreDao = DaoFactory.createScoreDao();

    public HTTPMessageHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("\nHTTP Message Handler started for " + this.socket);
        handleRequest(this.socket);
        System.out.println("HTTP Message Handler terminated for " + this.socket + "\n");
    }

    public void handleRequest(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String headerLine = in.readLine();
            StringTokenizer tokenizer = new StringTokenizer(headerLine);
            String httpMethod = tokenizer.nextToken();

            String httpQueryString = tokenizer.nextToken();
            String[] queryParameters = httpQueryString.split("/");

            if (httpMethod.equals("GET")) {
                if (queryParameters[1].equals("heart")) {
                    sendResponse(socket, 200, "beat");
                } else {
                    try {
                        Customer customer = customerDao.findBySsn(queryParameters[1]);
                        customer.updateScores(scoreDao.findByCustomerSsn(queryParameters[1]));

                        sendResponse(socket, 200, String.valueOf(customer));
                    } catch (NullPointerException e) {
                        sendResponse(socket, 404, "Customer not found.");
                    } catch (ArrayIndexOutOfBoundsException e) {
                        sendResponse(socket, 400, "Quantity of parameters is not enough.");
                    }
                }
            } else if (httpMethod.equals("POST")) {
                try {
                    if (queryParameters.length >= 3) {
                        try {
                            customerDao.insert(new Customer(queryParameters[1], LocalDate.parse(queryParameters[2])));
                            sendResponse(socket, 201, "Customer created successfully.");
                        } catch (DateTimeParseException e) {
                            sendResponse(socket, 400, "Signup date typed incorrectly.");
                        }
                    } else {
                        customerDao.insert(new Customer(queryParameters[1]));
                        sendResponse(socket, 201, "Customer created successfully.");
                    }
                } catch (DbException e) {
                    sendResponse(socket, 409, "Customer already exists.");
                } catch (ArrayIndexOutOfBoundsException e) {
                    sendResponse(socket, 400, "Quantity of parameters is not enough.");
                }
            } else if (httpMethod.equals("PUT")) {
                try {
                    scoreDao.insert(queryParameters[1], new Score(Integer.parseInt(queryParameters[2]), Integer.parseInt(queryParameters[3]), Integer.parseInt(queryParameters[4]), Integer.parseInt(queryParameters[5])));
                    sendResponse(socket, 200, "Customer score updated successfully.");
                } catch (NullPointerException e) {
                    sendResponse(socket, 404, "Customer not found.");
                } catch (ArrayIndexOutOfBoundsException e) {
                    sendResponse(socket, 400, "Quantity of parameters is not enough.");
                } catch (NumberFormatException e) {
                    sendResponse(socket, 400, "The score should be an integer.");
                }
            } else if (httpMethod.equals("DELETE")) {
                try {
                    customerDao.deleteBySsn(queryParameters[1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    sendResponse(socket, 400, "Quantity of parameters is not enough.");
                } catch (NullPointerException ignored) {}
                sendResponse(socket, 204, "");
            } else {
                System.out.println("The HTTP method is not recognized");
                sendResponse(socket, 405, "Method Not Allowed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendResponse(Socket socket, int statusCode, String responseString) {
        String statusLine;
        String serverHeader = "Server: WebServer\r\n";
        String contentTypeHeader = "Content-Type: text/html\r\n";
        try (DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            if (statusCode == 200) {
                statusLine = "HTTP/1.0 200 OK" + "\r\n";
                String contentLengthHeader = "Content-Length: " + responseString.length() + "\r\n";
                out.writeBytes(statusLine);
                out.writeBytes(serverHeader);
                out.writeBytes(contentTypeHeader);
                out.writeBytes(contentLengthHeader);
                out.writeBytes("\r\n");
                out.writeBytes(responseString);
            } else if (statusCode == 201) {
                statusLine = "HTTP/1.0 201 Created" + "\r\n";
                String contentLengthHeader = "Content-Length: " + responseString.length() + "\r\n";
                out.writeBytes(statusLine);
                out.writeBytes(serverHeader);
                out.writeBytes(contentTypeHeader);
                out.writeBytes(contentLengthHeader);
                out.writeBytes("\r\n");
                out.writeBytes(responseString);
            } else if (statusCode == 204) {
                statusLine = "HTTP/1.0 204 No Content" + "\r\n";
                out.writeBytes(statusLine);
                out.writeBytes("\r\n");
            } else if (statusCode == 400) {
                statusLine = "HTTP/1.0 400 Bad Request" + "\r\n";
                String contentLengthHeader = "Content-Length: " + responseString.length() + "\r\n";
                out.writeBytes(statusLine);
                out.writeBytes(serverHeader);
                out.writeBytes(contentTypeHeader);
                out.writeBytes(contentLengthHeader);
                out.writeBytes("\r\n");
                out.writeBytes(responseString);
            } else if (statusCode == 405) {
                statusLine = "HTTP/1.0 405 Method Not Allowed" + "\r\n";
                out.writeBytes(statusLine);
                out.writeBytes("\r\n");
            } else if (statusCode == 409) {
                statusLine = "HTTP/1.0 409 Conflict" + "\r\n";
                out.writeBytes(statusLine);
                out.writeBytes("\r\n");
            } else {
                statusLine = "HTTP/1.0 404 Not Found" + "\r\n";
                out.writeBytes(statusLine);
                out.writeBytes("\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
