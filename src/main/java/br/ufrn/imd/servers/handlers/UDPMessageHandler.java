package br.ufrn.imd.servers.handlers;

import java.util.StringTokenizer;
import java.util.stream.IntStream;

public class UDPMessageHandler {
    public static String handleRequest(String message) {
        StringTokenizer tokenizer = new StringTokenizer(message, ";");

        return RequisitionHandler.processRequisition(IntStream.range(0, tokenizer.countTokens()).mapToObj(i -> tokenizer.nextToken()).toList());
    }
}
