package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Account {

    private final String accountNumber;
    private final String ownerName;
    private double balance;
    private final LocalDateTime createdAt;
    private final List<Transaction> transactionHistory;
    private boolean active;

    public Account(String accountNumber, String ownerName, double initialBalance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = initialBalance;
        this.createdAt = LocalDateTime.now();
        this.transactionHistory = new ArrayList<>();
        this.active = true;
    }

    public abstract String getAccountType();
    public abstract double getInterestRate();

    public void deposit(double amount) {
        this.balance += amount;
    }

    public void withdraw(double amount) {
        this.balance -= amount;
    }

    public void addTransaction(Transaction t) {
        transactionHistory.add(t);
    }

    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    // ── Getters ──
    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName()     { return ownerName; }
    public double getBalance()       { return balance; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isActive()        { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Owner: %s | Balance: %.2f | Status: %s",
                getAccountType(), accountNumber, ownerName, balance, active ? "Active" : "Closed");
    }
}
