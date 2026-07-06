package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TransactionType type;
    private final double amount;
    private final double balanceAfter;
    private final String description;
    private final LocalDateTime timestamp;

    public Transaction(TransactionType type, double amount,
                       double balanceAfter, String description) {
        this.type         = type;
        this.amount       = amount;
        this.balanceAfter = balanceAfter;
        this.description  = description;
        this.timestamp    = LocalDateTime.now();
    }

    public TransactionType getType()    { return type; }
    public double getAmount()           { return amount; }
    public double getBalanceAfter()     { return balanceAfter; }
    public String getDescription()      { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("%-10s | %8.2f | Balance: %10.2f | %-30s | %s",
                type, amount, balanceAfter, description, timestamp.format(FMT));
    }
}
