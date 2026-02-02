package org.itnaf.banking.repository;

import org.itnaf.banking.model.Recipient;
import org.itnaf.banking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Recipient entity.
 * Provides database operations for managing saved transfer recipients.
 */
@Repository
public interface RecipientRepository extends JpaRepository<Recipient, Long> {

    /**
     * Find all recipients belonging to a specific user.
     *
     * @param user the user who owns the recipients
     * @return list of recipients
     */
    List<Recipient> findByUserOrderByNameAsc(User user);

    /**
     * Count the number of recipients for a user.
     *
     * @param user the user
     * @return number of recipients
     */
    long countByUser(User user);
}
