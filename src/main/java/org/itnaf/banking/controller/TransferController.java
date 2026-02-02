package org.itnaf.banking.controller;

import lombok.RequiredArgsConstructor;
import org.itnaf.banking.model.BankAccount;
import org.itnaf.banking.model.Recipient;
import org.itnaf.banking.model.User;
import org.itnaf.banking.service.BankAccountService;
import org.itnaf.banking.service.RecipientService;
import org.itnaf.banking.service.TransferService;
import org.itnaf.banking.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Controller for all transfer-related operations.
 * Handles transfers between accounts, to recipients, and quick transfers.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/transfer")
public class TransferController {

    private final UserService userService;
    private final BankAccountService bankAccountService;
    private final RecipientService recipientService;
    private final TransferService transferService;

    /**
     * Main transfer page - shows transfer options.
     */
    @GetMapping
    public String transferPage(Authentication authentication, Model model) {
        User user = getAuthenticatedUser(authentication);
        List<BankAccount> accounts = bankAccountService.getAccountsForUser(user);
        List<Recipient> recipients = recipientService.getRecipientsForUser(user);

        model.addAttribute("user", user);
        model.addAttribute("accounts", accounts);
        model.addAttribute("recipientCount", recipients.size());

        return "transfer";
    }

    /**
     * Transfer between own accounts - show form.
     */
    @GetMapping("/own-accounts")
    public String transferOwnAccountsForm(Authentication authentication, Model model) {
        User user = getAuthenticatedUser(authentication);
        List<BankAccount> accounts = bankAccountService.getAccountsForUser(user);

        model.addAttribute("user", user);
        model.addAttribute("accounts", accounts);

        return "transfer-own";
    }

    /**
     * Process transfer between own accounts.
     */
    @PostMapping("/own-accounts")
    public String processTransferOwnAccounts(
            Authentication authentication,
            @RequestParam("fromAccountId") Long fromAccountId,
            @RequestParam("toAccountId") Long toAccountId,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam(value = "memo", required = false) String memo,
            RedirectAttributes redirectAttributes) {

        User user = getAuthenticatedUser(authentication);
        List<BankAccount> accounts = bankAccountService.getAccountsForUser(user);

        // Find the accounts
        BankAccount fromAccount = accounts.stream()
                .filter(a -> a.getId().equals(fromAccountId))
                .findFirst()
                .orElse(null);

        BankAccount toAccount = accounts.stream()
                .filter(a -> a.getId().equals(toAccountId))
                .findFirst()
                .orElse(null);

        if (fromAccount == null || toAccount == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid account selection");
            return "redirect:/transfer/own-accounts";
        }

        if (fromAccountId.equals(toAccountId)) {
            redirectAttributes.addFlashAttribute("error", "Cannot transfer to the same account");
            return "redirect:/transfer/own-accounts";
        }

        // Process the transfer (demo only)
        Map<String, Object> result = transferService.transferBetweenAccounts(
                user, fromAccount, toAccount, amount, memo);

        if ((Boolean) result.get("success")) {
            redirectAttributes.addFlashAttribute("success", result.get("message"));
            redirectAttributes.addFlashAttribute("transferDetails", result);
        } else {
            redirectAttributes.addFlashAttribute("error", result.get("message"));
        }

        return "redirect:/transfer/own-accounts";
    }

    /**
     * Transfer to saved recipient - show form.
     */
    @GetMapping("/to-recipient")
    public String transferToRecipientForm(Authentication authentication, Model model) {
        User user = getAuthenticatedUser(authentication);
        List<BankAccount> accounts = bankAccountService.getAccountsForUser(user);
        List<Recipient> recipients = recipientService.getRecipientsForUser(user);

        model.addAttribute("user", user);
        model.addAttribute("accounts", accounts);
        model.addAttribute("recipients", recipients);

        return "transfer-recipient";
    }

    /**
     * Process transfer to saved recipient.
     */
    @PostMapping("/to-recipient")
    public String processTransferToRecipient(
            Authentication authentication,
            @RequestParam("fromAccountId") Long fromAccountId,
            @RequestParam("recipientId") Long recipientId,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam(value = "memo", required = false) String memo,
            RedirectAttributes redirectAttributes) {

        User user = getAuthenticatedUser(authentication);

        // Find the account
        BankAccount fromAccount = bankAccountService.getAccountsForUser(user).stream()
                .filter(a -> a.getId().equals(fromAccountId))
                .findFirst()
                .orElse(null);

        // Find the recipient
        Recipient recipient = recipientService.getRecipientByIdAndUser(recipientId, user)
                .orElse(null);

        if (fromAccount == null || recipient == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid selection");
            return "redirect:/transfer/to-recipient";
        }

        // Process the transfer (demo only)
        Map<String, Object> result = transferService.transferToRecipient(
                user, fromAccount, recipient, amount, memo);

        if ((Boolean) result.get("success")) {
            redirectAttributes.addFlashAttribute("success", result.get("message"));
            redirectAttributes.addFlashAttribute("transferDetails", result);
        } else {
            redirectAttributes.addFlashAttribute("error", result.get("message"));
        }

        return "redirect:/transfer/to-recipient";
    }

    /**
     * Quick transfer to new recipient - show form.
     */
    @GetMapping("/quick")
    public String quickTransferForm(Authentication authentication, Model model) {
        User user = getAuthenticatedUser(authentication);
        List<BankAccount> accounts = bankAccountService.getAccountsForUser(user);

        model.addAttribute("user", user);
        model.addAttribute("accounts", accounts);

        return "transfer-quick";
    }

    /**
     * Process quick transfer.
     */
    @PostMapping("/quick")
    public String processQuickTransfer(
            Authentication authentication,
            @RequestParam("fromAccountId") Long fromAccountId,
            @RequestParam("recipientName") String recipientName,
            @RequestParam("recipientAccount") String recipientAccount,
            @RequestParam("amount") BigDecimal amount,
            @RequestParam(value = "memo", required = false) String memo,
            @RequestParam(value = "saveRecipient", required = false) boolean saveRecipient,
            RedirectAttributes redirectAttributes) {

        User user = getAuthenticatedUser(authentication);

        // Find the account
        BankAccount fromAccount = bankAccountService.getAccountsForUser(user).stream()
                .filter(a -> a.getId().equals(fromAccountId))
                .findFirst()
                .orElse(null);

        if (fromAccount == null) {
            redirectAttributes.addFlashAttribute("error", "Invalid account selection");
            return "redirect:/transfer/quick";
        }

        // Process the transfer (demo only)
        Map<String, Object> result = transferService.quickTransfer(
                user, fromAccount, recipientName, recipientAccount, amount, memo);

        if ((Boolean) result.get("success")) {
            // Save recipient if requested
            if (saveRecipient) {
                Recipient newRecipient = new Recipient();
                newRecipient.setName(recipientName);
                newRecipient.setAccountNumber(recipientAccount);
                recipientService.addRecipient(user, newRecipient);
                result.put("message", result.get("message") + " - Recipient saved for future use.");
            }

            redirectAttributes.addFlashAttribute("success", result.get("message"));
            redirectAttributes.addFlashAttribute("transferDetails", result);
        } else {
            redirectAttributes.addFlashAttribute("error", result.get("message"));
        }

        return "redirect:/transfer/quick";
    }

    /**
     * Helper method to get authenticated user.
     */
    private User getAuthenticatedUser(Authentication authentication) {
        String email = authentication.getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
