package br.ufrn.imd.model.entities;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

public class Customer {
    private final String id;
    private final NavigableMap<VersionedKey, Integer> keyScore;
    private int paymentHistoryScore;
    private int creditUtilizationScore;
    private final LocalDate signupDate;
    private int amountScore; // amount recently reported score
    private int availableCreditScore;

    public Customer() {
        this.id = UUID.randomUUID().toString();
        this.signupDate = LocalDate.now();
        this.keyScore = new ConcurrentSkipListMap<>();
        this.paymentHistoryScore = 300;
        this.creditUtilizationScore = 300;
        this.amountScore = 300;
        this.availableCreditScore = 300;
        refreshKeyScore();
    }

    public Customer(LocalDate signupDate) {
        this.id = UUID.randomUUID().toString();
        this.signupDate = signupDate;
        this.keyScore = new ConcurrentSkipListMap<>();
        this.paymentHistoryScore = 300;
        this.creditUtilizationScore = 300;
        this.amountScore = 300;
        this.availableCreditScore = 300;
        refreshKeyScore();
    }

    public Customer(int paymentHistoryScore, int creditUtilizationScore, int amountScore, int availableCreditScore) {
        this.id = UUID.randomUUID().toString();
        this.signupDate = LocalDate.now();
        this.keyScore = new ConcurrentSkipListMap<>();
        this.paymentHistoryScore = Math.max(Math.min(paymentHistoryScore, 850), 300);
        this.creditUtilizationScore = Math.max(Math.min(creditUtilizationScore, 850), 300);
        this.amountScore = Math.max(Math.min(amountScore, 850), 300);
        this.availableCreditScore = Math.max(Math.min(availableCreditScore, 850), 300);
        refreshKeyScore();
    }

    public Customer(int paymentHistoryScore, int creditUtilizationScore, LocalDate signupDate, int amountScore, int availableCreditScore) {
        this.id = UUID.randomUUID().toString();
        this.keyScore = new ConcurrentSkipListMap<>();
        this.paymentHistoryScore = Math.max(Math.min(paymentHistoryScore, 850), 300);
        this.creditUtilizationScore = Math.max(Math.min(creditUtilizationScore, 850), 300);
        this.signupDate = signupDate;
        this.amountScore = Math.max(Math.min(amountScore, 850), 300);
        this.availableCreditScore = Math.max(Math.min(availableCreditScore, 850), 300);
        refreshKeyScore();
    }

//    public String getId() {
//        return id;
//    }
//
//    public Date getSignupDate() {
//        return signupDate;
//    }

//    public Map<Long, Integer> getKeyScore() {
//        return keyScore;
//    }

    public void showScoreHistorySortedByKey() {
        keyScore.forEach((key, value) -> System.out.printf("%s: %d\n", key, value));
    }

//    public int getPaymentHistoryScore() {
//        return paymentHistoryScore;
//    }

    public void setPaymentHistoryScore(int paymentHistoryScore) {
        this.paymentHistoryScore = Math.max(Math.min(paymentHistoryScore, 850), 300);
        refreshKeyScore();
    }

//    public int getCreditUtilizationScore() {
//        return creditUtilizationScore;
//    }

    public void setCreditUtilizationScore(int creditUtilizationScore) {
        this.creditUtilizationScore = Math.max(Math.min(creditUtilizationScore, 850), 300);
        refreshKeyScore();
    }

    public int getCreditHistoryLengthScore() {
        return (int) Math.max(Math.min(ChronoUnit.DAYS.between(signupDate, LocalDate.now()), 850), 300);
    }

//    public int getAmountScore() {
//        return amountScore;
//    }

    public void setAmountScore(int amountScore) {
        this.amountScore = Math.max(Math.min(amountScore, 850), 300);
        refreshKeyScore();
    }

//    public int getAvailableCreditScore() {
//        return availableCreditScore;
//    }

    public void setAvailableCreditScore(int availableCreditScore) {
        this.availableCreditScore = Math.max(Math.min(availableCreditScore, 850), 300);
        refreshKeyScore();
    }

    private void refreshKeyScore() {
        keyScore.put(new VersionedKey(Instant.now().toString(), keyScore.size() + 1), (int) Math.floor(0.4494 * paymentHistoryScore + 0.2247 * creditUtilizationScore + 0.236 * getCreditHistoryLengthScore() + 0.0562 * amountScore + 0.0337 * availableCreditScore));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", signupDate=" + signupDate +
                ", paymentHistoryScore=" + paymentHistoryScore +
                ", creditUtilizationScore=" + creditUtilizationScore +
                ", creditHistoryLengthScore=" + getCreditHistoryLengthScore() +
                ", amountScore=" + amountScore +
                ", availableCreditScore=" + availableCreditScore +
                '}';
    }
}
