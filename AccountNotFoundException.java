package service;

public class AccountNotFoundException extends BankException {
    public AccountNotFoundException(String accountNumber) {
        super("Account not found: " + accountNumber);
    }
}
