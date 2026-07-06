import cli.BankCLI;
import service.BankService;

public class Main {
    public static void main(String[] args) {
        BankService bankService = new BankService("NIT Federal Bank");
        BankCLI cli = new BankCLI(bankService);
        cli.start();
    }
}
