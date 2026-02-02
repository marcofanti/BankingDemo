package org.itnaf.banking.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itnaf.banking.model.BankAccount;
import org.itnaf.banking.model.Transaction;
import org.itnaf.banking.model.User;
import org.itnaf.banking.repository.BankAccountRepository;
import org.itnaf.banking.repository.TransactionRepository;
import org.itnaf.banking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Data initializer that populates the H2 in-memory database with demo users,
 * bank accounts, and transaction history on application startup.
 *
 * This creates 3 demo users, each with checking, savings, and investment accounts,
 * plus realistic transaction history.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Initializing demo data...");

        // Create demo users
        User user1 = createUser("John Doe", "john.doe@securebank.com", "password123");
        User user2 = createUser("Jane Smith", "jane.smith@securebank.com", "password123");
        User user3 = createUser("Demo User", "demo@securebank.com", "password123");

        // Create bank accounts and transactions for each user
        createAccountsAndTransactionsForUser(user1);
        createAccountsAndTransactionsForUser(user2);
        createAccountsAndTransactionsForUser(user3);

        log.info("Demo data initialized successfully!");
        log.info("Demo users created:");
        log.info("  - john.doe@securebank.com / password123");
        log.info("  - jane.smith@securebank.com / password123");
        log.info("  - demo@securebank.com / password123");
    }

    /**
     * Create a user with hashed password.
     */
    private User createUser(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    /**
     * Create checking, savings, and investment accounts for a user,
     * along with realistic transaction history.
     */
    private void createAccountsAndTransactionsForUser(User user) {
        // Create Checking Account
        BankAccount checking = new BankAccount();
        checking.setUser(user);
        checking.setName("Primary Checking");
        checking.setType(BankAccount.AccountType.CHECKING);
        checking.setAccountNumber("****" + (4521 + user.getId()));
        checking.setBalance(new BigDecimal("45230.50"));
        checking.setAvailable(new BigDecimal("45230.50"));
        checking.setInstitution("SecureBank");
        bankAccountRepository.save(checking);

        // Create Savings Account
        BankAccount savings = new BankAccount();
        savings.setUser(user);
        savings.setName("High-Yield Savings");
        savings.setType(BankAccount.AccountType.SAVINGS);
        savings.setAccountNumber("****" + (8892 + user.getId()));
        savings.setBalance(new BigDecimal("67890.25"));
        savings.setAvailable(new BigDecimal("67890.25"));
        savings.setInstitution("SecureBank");
        savings.setApy(4.5);
        bankAccountRepository.save(savings);

        // Create Investment Account
        BankAccount investment = new BankAccount();
        investment.setUser(user);
        investment.setName("Investment Portfolio");
        investment.setType(BankAccount.AccountType.INVESTMENT);
        investment.setAccountNumber("****" + (3347 + user.getId()));
        investment.setBalance(new BigDecimal("11469.00"));
        investment.setAvailable(new BigDecimal("11469.00"));
        investment.setInstitution("SecureBank Invest");
        investment.setChangePercent(12.5);
        bankAccountRepository.save(investment);

        // Create transaction history for the checking account
        createTransactions(checking);
    }

    /**
     * Create realistic transaction history for a bank account.
     */
    private void createTransactions(BankAccount account) {
        List<Transaction> transactions = List.of(
                new Transaction(account, "Apple Store", "Shopping",
                        LocalDate.now().minusDays(1), new BigDecimal("-999.00"),
                        Transaction.TransactionStatus.COMPLETED),
                new Transaction(account, "Salary Deposit - ACME Corp", "Income",
                        LocalDate.now().minusDays(2), new BigDecimal("5400.00"),
                        Transaction.TransactionStatus.COMPLETED),
                new Transaction(account, "Netflix Subscription", "Entertainment",
                        LocalDate.now().minusDays(3), new BigDecimal("-15.99"),
                        Transaction.TransactionStatus.COMPLETED),
                new Transaction(account, "Whole Foods Market", "Groceries",
                        LocalDate.now().minusDays(4), new BigDecimal("-127.43"),
                        Transaction.TransactionStatus.COMPLETED),
                new Transaction(account, "Transfer to Savings", "Transfer",
                        LocalDate.now().minusDays(5), new BigDecimal("-500.00"),
                        Transaction.TransactionStatus.COMPLETED),
                new Transaction(account, "Uber Ride", "Transportation",
                        LocalDate.now().minusDays(6), new BigDecimal("-24.50"),
                        Transaction.TransactionStatus.COMPLETED),
                new Transaction(account, "Electric Bill - PG&E", "Utilities",
                        LocalDate.now().minusDays(7), new BigDecimal("-145.00"),
                        Transaction.TransactionStatus.PENDING),
                new Transaction(account, "Amazon Purchase", "Shopping",
                        LocalDate.now().minusDays(8), new BigDecimal("-67.89"),
                        Transaction.TransactionStatus.COMPLETED),
                new Transaction(account, "Starbucks", "Food & Dining",
                        LocalDate.now().minusDays(9), new BigDecimal("-12.45"),
                        Transaction.TransactionStatus.COMPLETED),
                new Transaction(account, "ATM Withdrawal", "Cash",
                        LocalDate.now().minusDays(10), new BigDecimal("-200.00"),
                        Transaction.TransactionStatus.COMPLETED)
        );

        transactionRepository.saveAll(transactions);
    }
}
