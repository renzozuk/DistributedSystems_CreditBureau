package br.ufrn.imd.model.entities.enums;

public enum Status {
    POOR(300, 579),
    FAIR(580, 669),
    GOOD(670, 739),
    VERY_GOOD(740, 799),
    EXCELLENT(800, 850);

    private final int min;
    private final int max;

    Status(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public static Status fromScore(int score) {
        for (Status status : Status.values()) {
            if (status.min <= score && score <= status.max) {
                return status;
            }
        }

        throw new IllegalArgumentException("Invalid score: " + score);
    }
}
