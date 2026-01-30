package com.example.perfin;

public class Expense {

    // 🔥 Firestore document ID
    private String id;

    private String title;
    private String category;
    private double amount;
    private long timestamp;

    // 🔹 REQUIRED empty constructor for Firestore
    public Expense() {
    }

    // 🔹 Constructor WITHOUT id (for adding new expense)
    public Expense(String title, String category, double amount, long timestamp) {
        this.title = title;
        this.category = category;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    // 🔹 Constructor WITH id (for edit/delete)
    public Expense(String id, String title, String category, double amount, long timestamp) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    // ---------------- GETTERS ----------------

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // ---------------- SETTERS (IMPORTANT) ----------------

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
