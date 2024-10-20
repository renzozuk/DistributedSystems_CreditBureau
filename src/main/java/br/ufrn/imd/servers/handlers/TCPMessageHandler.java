package br.ufrn.imd.servers.handlers;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class TCPMessageHandler implements Runnable {
    private final Socket socket;
    private static final Lock lock = new ReentrantLock();

    public TCPMessageHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        lock.lock();
        System.out.println("\nTCP Message Handler started for " + this.socket);
        handleRequest(this.socket);
        System.out.println("TCP Message Handler terminated for " + this.socket + "\n");
        lock.unlock();
    }

    public void handleRequest(Socket socket) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            StringTokenizer tokenizer = new StringTokenizer(br.readLine(), ";");

            String reply = RequisitionHandler.processRequisition(IntStream.range(0, tokenizer.countTokens()).mapToObj(i -> tokenizer.nextToken()).toList());

            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
                bw.write(reply);
                bw.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
