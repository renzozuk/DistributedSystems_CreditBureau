package br.ufrn.imd.application;

import br.ufrn.imd.model.dao.CustomerDao;
import br.ufrn.imd.model.dao.DaoFactory;
import br.ufrn.imd.model.dao.ScoreDao;
import br.ufrn.imd.model.entities.Customer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.LocalDate;
import java.util.StringTokenizer;

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

                String operation = null;
                String firstParameter = null;
                String secondParameter = null;

                while (tokenizer.hasMoreTokens()) {
                    operation = tokenizer.nextToken();
                    firstParameter = tokenizer.nextToken();
                    secondParameter = tokenizer.nextToken();
                }

                switch (operation) {
                    case "create", "post":
                        customerDao.insert(new Customer(firstParameter, LocalDate.parse(secondParameter)));
                    case "get":
                        customerDao.findBySsn(firstParameter);
                }

                String reply = String.format("Confirmo recebimento de: %s", message);

                byte[] replyMessage = reply.getBytes();

                DatagramPacket datagramPacket = new DatagramPacket(replyMessage, replyMessage.length, receivePacket.getAddress(), receivePacket.getPort());

                serverSocket.send(datagramPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }
}
