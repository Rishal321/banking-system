package service;

import model.Account;
import model.CurrentAccount;
import model.SavingsAccount;
import model.Transaction;
import model.TransactionType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Core banking service handling all account operations.
 * Uses HashMap for O(1) account lookup.
 */
public class BankService {

    private final Map<String, Account> accounts = new HashMap<>();
    private final String bankName;

    public BankService(String bankName) {
        this.bankName = bankName;
    }

    // ── Account Management ────────────────────────────────────────────────

    public SavingsAccount createSavingsAccount(String ownerName, double initialDeposit)
            throws InvalidAmountException {
        validatePositiveAmount(initialDeposit);
        if (initialDeposit < 500) {
            throw new InvalidAmountException("Minimum initial deposit for Savings is 500.00");
        }
        String accNo = AccountNumberGenerator.generate("SAV");
        SavingsAccount account = new SavingsAccount(accNo, ownerName, initialDeposit);
        account.addTransaction(new Transaction(
                TransactionType.ACCOUNT_OPEN, initialDeposit, initialDeposit,
                "Account opened with initial deposit"));
        accounts.put(accNo, account);
        return account;
    }

    public CurrentAccount createCurrentAccount(String ownerName, double initialDeposit)
            throws InvalidAmountException {
        validatePositiveAmount(initialDeposit);
        String accNo = AccountNumberGenerator.generate("CUR");
        CurrentAccount account = new CurrentAccount(accNo, ownerName, initialDeposit);
        account.addTransaction(new Transaction(
                TransactionType.ACCOUNT_OPEN, initialDeposit, initialDeposit,
                "Account opened with initial deposit"));
        accounts.put(accNo, account);
        return account;
    }

    public void closeAccount(String accountNumber)
            throws AccountNotFoundException, AccountInactiveException {
        Account account = getActiveAccount(accountNumber);
        account.setActive(false);
    }

    // ── Core Transactions ─────────────────────────────────────────────────

    public void deposit(String accountNumber, double amount)
            throws AccountNotFoundException, AccountInactiveException, InvalidAmountException {
        validatePositiveAmount(amount);
        Account account = getActiveAccount(accountNumber);
        account.deposit(amount);
        account.addTransaction(new Transaction(
                TransactionType.DEPOSIT, amount, account.getBalance(), "Cash deposit"));
    }

    public void withdraw(String accountNumber, double amount)
            throws AccountNotFoundException, AccountInactiveException,
                   InvalidAmountException, InsufficientFundsException {
        validatePositiveAmount(amount);
        Account account = getActiveAccount(accountNumber);

        double available = getAvailableBalance(account);
        if (amount > available) {
            throw new InsufficientFundsException(amount - available);
        }

        // Savings: enforce minimum balance
        if (account instanceof SavingsAccount savings) {
            double balanceAfter = account.getBalance() - amount;
            if (balanceAfter < savings.getMinBalance()) {
                throw new InsufficientFundsException(
                        savings.getMinBalance() - balanceAfter);
            }
        }

        account.withdraw(amount);
        account.addTransaction(new Transaction(
                TransactionType.WITHDRAWAL, amount, account.getBalance(), "Cash withdrawal"));
    }

    public void transfer(String fromAccountNumber, String toAccountNumber, double amount)
            throws AccountNotFoundException, AccountInactiveException,
                   InvalidAmountException, InsufficientFundsException {
        validatePositiveAmount(amount);
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new InvalidAmountException("Cannot transfer to the same account.");
        }

        Account from = getActiveAccount(fromAccountNumber);
        Account to   = getActiveAccount(toAccountNumber);

        double available = getAvailableBalance(from);
        if (amount > available) {
            throw new InsufficientFundsException(amount - available);
        }

        // Atomic transfer
        from.withdraw(amount);
        to.deposit(amount);

        from.addTransaction(new Transaction(
                TransactionType.TRANSFER_OUT, amount, from.getBalance(),
                "Transfer to " + toAccountNumber));
        to.addTransaction(new Transaction(
                TransactionType.TRANSFER_IN, amount, to.getBalance(),
                "Transfer from " + fromAccountNumber));
    }

    // ── Interest ──────────────────────────────────────────────────────────

    public void applyInterestToAll() {
        for (Account account : accounts.values()) {
            if (account.isActive() && account instanceof SavingsAccount savings) {
                savings.applyInterest();
            }
        }
    }

    // ── Queries ───────────────────────────────────────────────────────────

    public Account getAccount(String accountNumber) throws AccountNotFoundException {
        Account account = accounts.get(accountNumber);
        if (account == null) throw new AccountNotFoundException(accountNumber);
        return account;
    }

    public Collection<Account> getAllAccounts() {
        return accounts.values();
    }

    public double getTotalDeposits() {
        return accounts.values().stream()
                .filter(Account::isActive)
                .mapToDouble(Account::getBalance)
                .sum();
    }

    public String getBankName() { return bankName; }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Account getActiveAccount(String accountNumber)
            throws AccountNotFoundException, AccountInactiveException {
        Account account = getAccount(accountNumber);
        if (!account.isActive()) throw new AccountInactiveException(accountNumber);
        return account;
    }

    private double getAvailableBalance(Account account) {
        if (account instanceof CurrentAccount current) {
            return current.getAvailableBalance();
        }
        return account.getBalance();
    }

    private void validatePositiveAmount(double amount) throws InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException("Amount must be greater than zero.");
    }
}
