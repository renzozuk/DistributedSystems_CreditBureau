package br.ufrn.imd.model.entities.enums;

public enum CustomerStatus {
    POOR(300, 579),
    FAIR(580, 669),
    GOOD(670, 739),
    VERY_GOOD(740, 799),
    EXCELLENT(800, 850);

    private final int min;
    private final int max;

    CustomerStatus(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public static CustomerStatus fromScore(int score) {
        for (CustomerStatus customerStatus : CustomerStatus.values()) {
            if (customerStatus.min <= score && score <= customerStatus.max) {
                return customerStatus;
            }
        }

        throw new IllegalArgumentException("Invalid score: " + score);
    }
}
