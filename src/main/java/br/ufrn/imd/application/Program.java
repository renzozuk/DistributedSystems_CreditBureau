package br.ufrn.imd.application;

import br.ufrn.imd.model.entities.Customer;

import java.time.LocalDate;

public class Program {
    public static void main(String[] args) {
        Customer c1 = new Customer(1050, 450, 550, 650);
        c1.showScoreHistorySortedByKey();

        System.out.println();
        c1.setPaymentHistoryScore(550);
        c1.showScoreHistorySortedByKey();

        System.out.println();
        c1.setCreditUtilizationScore(650);
        c1.showScoreHistorySortedByKey();

        System.out.println();
        c1.setAmountScore(555);
        c1.showScoreHistorySortedByKey();

        System.out.println();
        c1.setAvailableCreditScore(960);
        c1.showScoreHistorySortedByKey();

        System.out.println();
        Customer c2 = new Customer( 1050, 450, LocalDate.parse("2023-07-01"),550, 650);
        c2.showScoreHistorySortedByKey();

        System.out.println();
        System.out.println(c1.getCreditHistoryLengthScore());
        System.out.println(c2.getCreditHistoryLengthScore());
    }
}
