package cli;

import model.Account;
import model.CurrentAccount;
import model.Transaction;
import service.AccountNotFoundException;
import service.BankException;
import service.BankService;
import service.InvalidAmountException;

import java.util.List;
import java.util.Scanner;

/**
 * Command-Line Interface for the Banking System.
 * Handles all user interaction and delegates to BankService.
 */
public class BankCLI {

    private final BankService bankService;
    private final Scanner scanner;

    private static final String DIVIDER  = "─".repeat(60);
    private static final String DIVIDER2 = "═".repeat(60);

    public BankCLI(BankService bankService) {
        this.bankService = bankService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1  -> createAccount();
                case 2  -> deposit();
                case 3  -> withdraw();
                case 4  -> transfer();
                case 5  -> checkBalance();
                case 6  -> viewTransactionHistory();
                case 7  -> viewAllAccounts();
                case 8  -> applyInterest();
                case 9  -> closeAccount();
                case 0  -> { running = false; printGoodbye(); }
                default -> printError("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    // ── Menu ──────────────────────────────────────────────────────────────

    private void printBanner() {
        System.out.println();
        System.out.println(DIVIDER2);
        System.out.println("       Welcome to " + bankService.getBankName());
        System.out.println("            Banking System CLI");
        System.out.println(DIVIDER2);
    }

    private void printMainMenu() {
        System.out.println("\n" + DIVIDER);
        System.out.println("  MAIN MENU");
        System.out.println(DIVIDER);
        System.out.println("  1. Create Account");
        System.out.println("  2. Deposit");
        System.out.println("  3. Withdraw");
        System.out.println("  4. Transfer");
        System.out.println("  5. Check Balance");
        System.out.println("  6. Transaction History");
        System.out.println("  7. View All Accounts");
        System.out.println("  8. Apply Interest (Savings)");
        System.out.println("  9. Close Account");
        System.out.println("  0. Exit");
        System.out.println(DIVIDER);
    }

    // ── Operations ────────────────────────────────────────────────────────

    private void createAccount() {
        System.out.println("\n  CREATE ACCOUNT");
        System.out.println("  1. Savings Account (4% interest, min balance ₹500)");
        System.out.println("  2. Current Account (1% interest, ₹10,000 overdraft)");
        int type = readInt("  Select account type: ");

        String name = readString("  Enter account holder name: ");
        double initial = readDouble("  Enter initial deposit amount: ₹");

        try {
            Account account;
            if (type == 1) {
                account = bankService.createSavingsAccount(name, initial);
            } else if (type == 2) {
                account = bankService.createCurrentAccount(name, initial);
            } else {
                printError("Invalid account type.");
                return;
            }
            printSuccess("Account created successfully!");
            System.out.println("  Account Number : " + account.getAccountNumber());
            System.out.println("  Account Type   : " + account.getAccountType());
            System.out.println("  Owner          : " + account.getOwnerName());
            System.out.printf ("  Balance        : ₹%.2f%n", account.getBalance());
        } catch (InvalidAmountException e) {
            printError(e.getMessage());
        }
    }

    private void deposit() {
        System.out.println("\n  DEPOSIT");
        String accNo  = readString("  Account number: ");
        double amount = readDouble("  Amount to deposit: ₹");
        try {
            bankService.deposit(accNo, amount);
            Account acc = bankService.getAccount(accNo);
            printSuccess(String.format("Deposited ₹%.2f successfully!", amount));
            System.out.printf("  New Balance: ₹%.2f%n", acc.getBalance());
        } catch (BankException e) {
            printError(e.getMessage());
        }
    }

    private void withdraw() {
        System.out.println("\n  WITHDRAW");
        String accNo  = readString("  Account number: ");
        double amount = readDouble("  Amount to withdraw: ₹");
        try {
            bankService.withdraw(accNo, amount);
            Account acc = bankService.getAccount(accNo);
            printSuccess(String.format("Withdrawn ₹%.2f successfully!", amount));
            System.out.printf("  New Balance: ₹%.2f%n", acc.getBalance());
        } catch (BankException e) {
            printError(e.getMessage());
        }
    }

    private void transfer() {
        System.out.println("\n  TRANSFER");
        String from   = readString("  From account number: ");
        String to     = readString("  To account number:   ");
        double amount = readDouble("  Amount to transfer: ₹");
        try {
            bankService.transfer(from, to, amount);
            printSuccess(String.format("Transferred ₹%.2f from %s to %s!", amount, from, to));
            System.out.printf("  Your new balance: ₹%.2f%n",
                    bankService.getAccount(from).getBalance());
        } catch (BankException e) {
            printError(e.getMessage());
        }
    }

    private void checkBalance() {
        System.out.println("\n  BALANCE ENQUIRY");
        String accNo = readString("  Account number: ");
        try {
            Account acc = bankService.getAccount(accNo);
            System.out.println(DIVIDER);
            System.out.println("  Account   : " + acc.getAccountNumber());
            System.out.println("  Owner     : " + acc.getOwnerName());
            System.out.println("  Type      : " + acc.getAccountType());
            System.out.printf ("  Balance   : ₹%.2f%n", acc.getBalance());
            if (acc instanceof CurrentAccount ca) {
                System.out.printf("  Available : ₹%.2f (incl. overdraft)%n",
                        ca.getAvailableBalance());
            }
            System.out.println("  Status    : " + (acc.isActive() ? "Active" : "Closed"));
            System.out.println(DIVIDER);
        } catch (AccountNotFoundException e) {
            printError(e.getMessage());
        }
    }

    private void viewTransactionHistory() {
        System.out.println("\n  TRANSACTION HISTORY");
        String accNo = readString("  Account number: ");
        try {
            Account acc = bankService.getAccount(accNo);
            List<Transaction> history = acc.getTransactionHistory();
            if (history.isEmpty()) {
                System.out.println("  No transactions found.");
                return;
            }
            System.out.println(DIVIDER);
            System.out.printf("  %-10s | %8s | %-14s | %-30s | %s%n",
                    "TYPE", "AMOUNT", "BALANCE AFTER", "DESCRIPTION", "TIMESTAMP");
            System.out.println(DIVIDER);
            for (Transaction t : history) {
                System.out.println("  " + t);
            }
            System.out.println(DIVIDER);
        } catch (AccountNotFoundException e) {
            printError(e.getMessage());
        }
    }

    private void viewAllAccounts() {
        System.out.println("\n  ALL ACCOUNTS");
        System.out.println(DIVIDER);
        var all = bankService.getAllAccounts();
        if (all.isEmpty()) {
            System.out.println("  No accounts found.");
        } else {
            all.forEach(a -> System.out.println("  " + a));
        }
        System.out.println(DIVIDER);
        System.out.printf("  Total deposits held: ₹%.2f%n", bankService.getTotalDeposits());
        System.out.println(DIVIDER);
    }

    private void applyInterest() {
        bankService.applyInterestToAll();
        printSuccess("Annual interest applied to all active Savings accounts.");
    }

    private void closeAccount() {
        System.out.println("\n  CLOSE ACCOUNT");
        String accNo = readString("  Account number to close: ");
        System.out.print("  Are you sure? (yes/no): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("  Operation cancelled.");
            return;
        }
        try {
            bankService.closeAccount(accNo);
            printSuccess("Account " + accNo + " has been closed.");
        } catch (BankException e) {
            printError(e.getMessage());
        }
    }

    // ── Input Helpers ─────────────────────────────────────────────────────

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                printError("Please enter a valid number.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                printError("Please enter a valid amount.");
            }
        }
    }

    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private void printSuccess(String msg) {
        System.out.println("\n  ✔ " + msg);
    }

    private void printError(String msg) {
        System.out.println("\n  ✘ ERROR: " + msg);
    }

    private void printGoodbye() {
        System.out.println("\n" + DIVIDER2);
        System.out.println("  Thank you for using " + bankService.getBankName() + "!");
        System.out.println("  Goodbye.");
        System.out.println(DIVIDER2 + "\n");
    }
}
