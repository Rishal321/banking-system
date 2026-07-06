package service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates simple, sequential, unique account numbers per account type prefix.
 */
public class AccountNumberGenerator {

    private static final AtomicInteger counter = new AtomicInteger(1000);

    private AccountNumberGenerator() {
        // utility class
    }

    public static String generate(String prefix) {
        return prefix + counter.incrementAndGet();
    }
}
