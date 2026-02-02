package org.itnaf.banking.service;

import lombok.RequiredArgsConstructor;
import org.itnaf.banking.model.Recipient;
import org.itnaf.banking.model.User;
import org.itnaf.banking.repository.RecipientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing saved recipients.
 * Handles recipient creation, retrieval, and deletion.
 */
@Service
@RequiredArgsConstructor
public class RecipientService {

    private final RecipientRepository recipientRepository;

    /**
     * Get all recipients for a user.
     *
     * @param user the user
     * @return list of recipients ordered by name
     */
    public List<Recipient> getRecipientsForUser(User user) {
        return recipientRepository.findByUserOrderByNameAsc(user);
    }

    /**
     * Add a new recipient for a user.
     *
     * @param user the user
     * @param recipient the recipient to add
     * @return the saved recipient
     */
    @Transactional
    public Recipient addRecipient(User user, Recipient recipient) {
        recipient.setUser(user);
        return recipientRepository.save(recipient);
    }

    /**
     * Find a recipient by ID and verify it belongs to the user.
     *
     * @param recipientId the recipient ID
     * @param user the user
     * @return Optional containing the recipient if found and owned by user
     */
    public Optional<Recipient> getRecipientByIdAndUser(Long recipientId, User user) {
        return recipientRepository.findById(recipientId)
                .filter(recipient -> recipient.getUser().getId().equals(user.getId()));
    }

    /**
     * Delete a recipient.
     *
     * @param recipientId the recipient ID
     * @param user the user (for security check)
     */
    @Transactional
    public void deleteRecipient(Long recipientId, User user) {
        getRecipientByIdAndUser(recipientId, user)
                .ifPresent(recipientRepository::delete);
    }

    /**
     * Count recipients for a user.
     *
     * @param user the user
     * @return number of recipients
     */
    public long countRecipients(User user) {
        return recipientRepository.countByUser(user);
    }
}
