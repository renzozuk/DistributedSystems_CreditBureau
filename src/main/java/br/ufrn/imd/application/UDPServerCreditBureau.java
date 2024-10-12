package br.ufrn.imd.application;

import br.ufrn.imd.db.DbException;
import br.ufrn.imd.model.dao.CustomerDao;
import br.ufrn.imd.model.dao.DaoFactory;
import br.ufrn.imd.model.dao.ScoreDao;
import br.ufrn.imd.model.entities.Customer;
import br.ufrn.imd.model.entities.Score;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.IntStream;

public class UDPServerCreditBureau {
    private final CustomerDao customerDao = DaoFactory.createCustomerDao();
    private final ScoreDao scoreDao = DaoFactory.createScoreDao();

    public UDPServerCreditBureau(String port) {
        System.out.println("UDP server started.");

        try {
            DatagramSocket serverSocket = new DatagramSocket(Integer.parseInt(port));

            while (true) {
                byte[] receiveMessage = new byte[1024];

                DatagramPacket receivePacket = new DatagramPacket(receiveMessage, receiveMessage.length);

                serverSocket.receive(receivePacket);

                String message = new String(receivePacket.getData());

                StringTokenizer tokenizer = new StringTokenizer(message, ";");

                List<String> tokens = new ArrayList<>();

                while (tokenizer.hasMoreTokens()) {
                    IntStream.range(0, tokenizer.countTokens()).forEach(i -> tokens.add(tokenizer.nextToken()));
                }

//                IntStream.range(0, tokens.size()).forEach(i -> System.out.println(tokens.get(i).trim()));

                String reply = null;

                switch (tokens.get(0).trim().toLowerCase()) {
                    case "create", "post":
                        try {
                            if (tokens.size() >= 3) {
                                customerDao.insert(new Customer(tokens.get(1).trim(), LocalDate.parse(tokens.get(2).trim())));
                            } else {
                                customerDao.insert(new Customer(tokens.get(1).trim()));
                            }

                            reply = String.format("An account [SSN: %s] was created successfully.", tokens.get(1));
                        } catch (DbException e) {
                            reply = String.format("It wasn't possible to create an account [SSN: %s]. Please, check if there's already an account with that SSN.", tokens.get(1));
                        } catch (IllegalArgumentException e) {
                            reply = "The SSN is invalid.";
                        }
                        break;
                    case "get":
                        Customer customer = customerDao.findBySsn(tokens.get(1).trim());
                        reply = customer.toString();
                        break;
                    case "delete":
                        customerDao.deleteBySsn(tokens.get(1).trim());
                        reply = String.format("User [SSN: %s] deleted successfully.", tokens.get(1).trim());
                        break;
                    case "updatescore", "update_score":
                        scoreDao.insert(tokens.get(1).trim(), new Score(Integer.parseInt(tokens.get(2).trim()),
                                Integer.parseInt(tokens.get(3).trim()),
                                Integer.parseInt(tokens.get(4).trim()),
                                Integer.parseInt(tokens.get(5).trim())));
                        reply = String.format("User score [SSN: %s] updated successfully.", tokens.get(1).trim());
                        break;
                }

                byte[] replyMessage = reply.getBytes();

                DatagramPacket datagramPacket = new DatagramPacket(replyMessage, replyMessage.length, receivePacket.getAddress(), receivePacket.getPort());

                serverSocket.send(datagramPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new UDPServerCreditBureau(args[0]);
    }
}
