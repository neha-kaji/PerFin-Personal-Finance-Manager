package com.example.perfin;

public class SavingsGoal {

    private double monthlySavings;
    private long timestamp;

    public SavingsGoal() {}

    public SavingsGoal(double monthlySavings, long timestamp) {
        this.monthlySavings = monthlySavings;
        this.timestamp = timestamp;
    }

    public double getMonthlySavings() {
        return monthlySavings;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
