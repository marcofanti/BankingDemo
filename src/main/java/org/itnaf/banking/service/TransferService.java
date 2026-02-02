package org.itnaf.banking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.itnaf.banking.model.BankAccount;
import org.itnaf.banking.model.Recipient;
import org.itnaf.banking.model.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class for handling money transfers.
 *
 * NOTE: This is a DEMO-ONLY implementation. Transfers are logged but do not
 * actually update account balances. In a production system, this would:
 * 1. Validate sufficient funds
 * 2. Create transaction records
 * 3. Update account balances atomically
 * 4. Handle errors and rollbacks
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {

    /**
     * Simulate transfer between user's own accounts.
     * Demo only - does not update balances.
     *
     * @param user the user initiating the transfer
     * @param fromAccount the source account
     * @param toAccount the destination account
     * @param amount the amount to transfer
     * @param memo optional memo/note
     * @return result map with status and message
     */
    public Map<String, Object> transferBetweenAccounts(
            User user, BankAccount fromAccount, BankAccount toAccount,
            BigDecimal amount, String memo) {

        Map<String, Object> result = new HashMap<>();

        // Validate accounts belong to user
        if (!fromAccount.getUser().getId().equals(user.getId()) ||
            !toAccount.getUser().getId().equals(user.getId())) {
            result.put("success", false);
            result.put("message", "Invalid account selection");
            return result;
        }

        // Validate amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            result.put("success", false);
            result.put("message", "Amount must be greater than zero");
            return result;
        }

        // Validate sufficient funds (demo check)
        if (fromAccount.getAvailable().compareTo(amount) < 0) {
            result.put("success", false);
            result.put("message", "Insufficient funds");
            return result;
        }

        // Log the transfer (demo only - not actually updating balances)
        log.info("DEMO TRANSFER: User {} transferring {} from {} to {} - Memo: {}",
                user.getEmail(), amount, fromAccount.getName(), toAccount.getName(), memo);

        result.put("success", true);
        result.put("message", "Transfer completed successfully (Demo Mode - balances not updated)");
        result.put("fromAccount", fromAccount.getName());
        result.put("toAccount", toAccount.getName());
        result.put("amount", amount);

        return result;
    }

    /**
     * Simulate transfer to a saved recipient.
     * Demo only - does not update balances.
     *
     * @param user the user initiating the transfer
     * @param fromAccount the source account
     * @param recipient the recipient
     * @param amount the amount to transfer
     * @param memo optional memo/note
     * @return result map with status and message
     */
    public Map<String, Object> transferToRecipient(
            User user, BankAccount fromAccount, Recipient recipient,
            BigDecimal amount, String memo) {

        Map<String, Object> result = new HashMap<>();

        // Validate account belongs to user
        if (!fromAccount.getUser().getId().equals(user.getId())) {
            result.put("success", false);
            result.put("message", "Invalid account selection");
            return result;
        }

        // Validate recipient belongs to user
        if (!recipient.getUser().getId().equals(user.getId())) {
            result.put("success", false);
            result.put("message", "Invalid recipient");
            return result;
        }

        // Validate amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            result.put("success", false);
            result.put("message", "Amount must be greater than zero");
            return result;
        }

        // Validate sufficient funds (demo check)
        if (fromAccount.getAvailable().compareTo(amount) < 0) {
            result.put("success", false);
            result.put("message", "Insufficient funds");
            return result;
        }

        // Log the transfer (demo only - not actually updating balances)
        log.info("DEMO TRANSFER: User {} transferring {} from {} to {} (Account: {}) - Memo: {}",
                user.getEmail(), amount, fromAccount.getName(),
                recipient.getName(), recipient.getAccountNumber(), memo);

        result.put("success", true);
        result.put("message", "Transfer completed successfully (Demo Mode - balances not updated)");
        result.put("fromAccount", fromAccount.getName());
        result.put("recipient", recipient.getName());
        result.put("amount", amount);

        return result;
    }

    /**
     * Simulate quick transfer to a new recipient (not saved).
     * Demo only - does not update balances.
     *
     * @param user the user initiating the transfer
     * @param fromAccount the source account
     * @param recipientName the recipient's name
     * @param recipientAccount the recipient's account number
     * @param amount the amount to transfer
     * @param memo optional memo/note
     * @return result map with status and message
     */
    public Map<String, Object> quickTransfer(
            User user, BankAccount fromAccount, String recipientName,
            String recipientAccount, BigDecimal amount, String memo) {

        Map<String, Object> result = new HashMap<>();

        // Validate account belongs to user
        if (!fromAccount.getUser().getId().equals(user.getId())) {
            result.put("success", false);
            result.put("message", "Invalid account selection");
            return result;
        }

        // Validate amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            result.put("success", false);
            result.put("message", "Amount must be greater than zero");
            return result;
        }

        // Validate sufficient funds (demo check)
        if (fromAccount.getAvailable().compareTo(amount) < 0) {
            result.put("success", false);
            result.put("message", "Insufficient funds");
            return result;
        }

        // Log the transfer (demo only - not actually updating balances)
        log.info("DEMO QUICK TRANSFER: User {} transferring {} from {} to {} (Account: {}) - Memo: {}",
                user.getEmail(), amount, fromAccount.getName(),
                recipientName, recipientAccount, memo);

        result.put("success", true);
        result.put("message", "Transfer completed successfully (Demo Mode - balances not updated)");
        result.put("fromAccount", fromAccount.getName());
        result.put("recipient", recipientName);
        result.put("amount", amount);

        return result;
    }
}
