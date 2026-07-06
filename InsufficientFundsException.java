package service;

public class InsufficientFundsException extends BankException {
    public InsufficientFundsException(double shortfall) {
        super(String.format("Insufficient funds. You are short by ₹%.2f", shortfall));
    }
}
