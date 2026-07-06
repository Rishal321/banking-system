package service;

public class AccountInactiveException extends BankException {
    public AccountInactiveException(String accountNumber) {
        super("Account is closed/inactive: " + accountNumber);
    }
}
