package org.itnaf.banking.service;

import lombok.RequiredArgsConstructor;
import org.itnaf.banking.model.BankAccount;
import org.itnaf.banking.model.Transaction;
import org.itnaf.banking.model.User;
import org.itnaf.banking.repository.BankAccountRepository;
import org.itnaf.banking.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service class for bank account operations.
 * Handles account retrieval and transaction history.
 */
@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Get all bank accounts for a user.
     *
     * @param user the user who owns the accounts
     * @return list of bank accounts
     */
    public List<BankAccount> getAccountsForUser(User user) {
        return bankAccountRepository.findByUser(user);
    }

    /**
     * Calculate total balance across all accounts for a user.
     *
     * @param user the user
     * @return total balance
     */
    public BigDecimal getTotalBalance(User user) {
        return getAccountsForUser(user).stream()
                .map(BankAccount::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get recent transactions for a user across all their accounts.
     *
     * @param userId the user's ID
     * @return list of transactions ordered by date descending
     */
    public List<Transaction> getRecentTransactions(Long userId) {
        return transactionRepository.findByUserIdOrderByDateDesc(userId);
    }
}
