package org.itnaf.banking.repository;

import org.itnaf.banking.model.BankAccount;
import org.itnaf.banking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for BankAccount entity.
 * Provides database operations for account management.
 */
@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    /**
     * Find all bank accounts belonging to a specific user.
     *
     * @param user the user who owns the accounts
     * @return list of bank accounts
     */
    List<BankAccount> findByUser(User user);
}
