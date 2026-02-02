package org.itnaf.banking.repository;

import org.itnaf.banking.model.BankAccount;
import org.itnaf.banking.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Transaction entity.
 * Provides database operations for transaction history.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Find all transactions for accounts owned by a specific user.
     *
     * @param userId the user's ID
     * @return list of transactions ordered by date descending
     */
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId ORDER BY t.date DESC")
    List<Transaction> findByUserIdOrderByDateDesc(@Param("userId") Long userId);
}
