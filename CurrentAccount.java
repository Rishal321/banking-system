package model;

public class CurrentAccount extends Account {

    private static final double INTEREST_RATE  = 0.01; // 1% per annum
    private static final double OVERDRAFT_LIMIT = 10000.0;

    public CurrentAccount(String accountNumber, String ownerName, double initialBalance) {
        super(accountNumber, ownerName, initialBalance);
    }

    @Override
    public String getAccountType() { return "CURRENT"; }

    @Override
    public double getInterestRate() { return INTEREST_RATE; }

    public double getOverdraftLimit() { return OVERDRAFT_LIMIT; }

    /** Current accounts can go into overdraft up to the limit */
    public double getAvailableBalance() {
        return getBalance() + OVERDRAFT_LIMIT;
    }
}
