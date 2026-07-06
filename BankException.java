package service;

/**
 * Base checked exception for all banking-related errors.
 */
public class BankException extends Exception {
    public BankException(String message) {
        super(message);
    }
}
