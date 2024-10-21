package br.ufrn.imd.servers.handlers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.stream.IntStream;

public class TCPMessageHandler {
    public static void handleRequest(Socket socket) {
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
