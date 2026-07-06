package model;

public class SavingsAccount extends Account {

    private static final double INTEREST_RATE = 0.04; // 4% per annum
    private static final double MIN_BALANCE    = 500.0;

    public SavingsAccount(String accountNumber, String ownerName, double initialBalance) {
        super(accountNumber, ownerName, initialBalance);
    }

    @Override
    public String getAccountType() { return "SAVINGS"; }

    @Override
    public double getInterestRate() { return INTEREST_RATE; }

    public double getMinBalance() { return MIN_BALANCE; }

    /** Apply annual interest to balance */
    public void applyInterest() {
        double interest = getBalance() * INTEREST_RATE;
        deposit(interest);
        addTransaction(new Transaction(
            TransactionType.INTEREST,
            interest,
            getBalance(),
            "Annual interest applied"
        ));
    }
}
