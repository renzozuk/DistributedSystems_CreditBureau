package br.ufrn.imd.model.entities;

import br.ufrn.imd.model.entities.enums.Status;

import java.time.Instant;
import java.time.LocalDate;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

public class Customer {
    private final String id;
    private final String ssn;
    private final LocalDate signupDate;
    private final NavigableMap<VersionedKey, Score> keyScore;

    public Customer(String ssn) {
        if(ssn.length() != 9){
            throw new IllegalArgumentException("Invalid SSN");
        }

        this.id = UUID.nameUUIDFromBytes(ssn.getBytes()).toString();
        this.ssn = ssn;
        this.signupDate = LocalDate.now();
        this.keyScore = new ConcurrentSkipListMap<>();
    }

    public Customer(String ssn, LocalDate signupDate) {
        if(ssn.length() != 9){
            throw new IllegalArgumentException("Invalid SSN");
        }

        this.id = UUID.nameUUIDFromBytes(ssn.getBytes()).toString();
        this.ssn = ssn;
        this.signupDate = signupDate;
        this.keyScore = new ConcurrentSkipListMap<>();
    }

    public String getId() {
        return id;
    }

    public String getSsn() {
        return ssn;
    }

    public LocalDate getSignupDate() {
        return signupDate;
    }

    public void showScoreHistory() {
        System.out.printf("==========================================\nSocial Security Number (SSN): %s\n\n", ssn);
        System.out.println("version_key: score");
        keyScore.forEach((key, value) -> System.out.printf("%s: [Score: %d (%s)]\n", key, value.getFinalScore(this), Status.fromScore(value.getFinalScore(this))));
        System.out.println("==========================================");
    }

    public void showDetailedScoreHistory() {
        System.out.printf("==========================================\nSocial Security Number (SSN): %s\n\n", ssn);
        System.out.println("version_key: score");
        keyScore.forEach((key, value) -> System.out.printf("%s: [Payment History Score: %d] [Credit Utilization Score: %d] [Credit History Length Score: %d] [Amount Score: %d] [Available Credit Score: %d] [Score: %d (%s)]\n", key, value.getPaymentHistoryScore(), value.getCreditUtilizationScore(), value.getCreditHistoryLengthScore(this), value.getAmountScore(), value.getAvailableCreditScore(), value.getFinalScore(this), Status.fromScore(value.getFinalScore(this))));
        System.out.println("==========================================");
    }

    public void showLastScore() {
        System.out.printf("==========================================\nSocial Security Number (SSN): %s\n\n", ssn);
        System.out.println("version_key: score");
        System.out.printf("%s: %d (%s)\n", keyScore.lastEntry().getKey(), keyScore.lastEntry().getValue().getFinalScore(this), Status.fromScore(keyScore.lastEntry().getValue().getFinalScore(this)));
        System.out.println("==========================================");
    }

    public void updatePaymentHistoryScore(int paymentHistoryScore) {
        refreshKeyScore(paymentHistoryScore,
                keyScore.lastEntry().getValue().getCreditUtilizationScore(),
                keyScore.lastEntry().getValue().getAmountScore(),
                keyScore.lastEntry().getValue().getAvailableCreditScore());
    }

    public void updateCreditUtilizationScore(int creditUtilizationScore) {
        refreshKeyScore(keyScore.lastEntry().getValue().getPaymentHistoryScore(),
                creditUtilizationScore,
                keyScore.lastEntry().getValue().getAmountScore(),
                keyScore.lastEntry().getValue().getAvailableCreditScore());
    }

    public void updateAmountScore(int amountScore) {
        refreshKeyScore(keyScore.lastEntry().getValue().getPaymentHistoryScore(),
                keyScore.lastEntry().getValue().getCreditUtilizationScore(),
                amountScore,
                keyScore.lastEntry().getValue().getAvailableCreditScore());
    }

    public void updateAvailableCreditScore(int availableCreditScore) {
        refreshKeyScore(keyScore.lastEntry().getValue().getPaymentHistoryScore(),
                keyScore.lastEntry().getValue().getCreditUtilizationScore(),
                keyScore.lastEntry().getValue().getAmountScore(),
                availableCreditScore);
    }

    public void updateAllScores(VersionedKey versionedKey, Score score) {
        keyScore.put(versionedKey, score);
    }

    public void updateAllScores(NavigableMap<VersionedKey, Score> keyScore) {
        this.keyScore.putAll(keyScore);
    }

    private void refreshKeyScore(int paymentHistoryScore, int creditUtilizationScore, int amountScore, int availableCreditScore) {
        keyScore.put(new VersionedKey(Instant.now().toString(), keyScore.size() + 1), new Score(paymentHistoryScore, creditUtilizationScore, amountScore, availableCreditScore));
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
        StringBuilder result = new StringBuilder();

        result.append("ID: ").append(id).append("\n")
                .append("SSN: ").append(ssn).append("\n")
                .append("Signup date: ").append(signupDate).append("\n");

        keyScore.forEach((key, value) -> result.append(key).append(": ").append(value.getFinalScore(this)).append("\n"));

        return result.toString();
    }
}
