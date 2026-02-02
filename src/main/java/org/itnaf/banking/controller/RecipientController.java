package org.itnaf.banking.controller;

import lombok.RequiredArgsConstructor;
import org.itnaf.banking.model.Recipient;
import org.itnaf.banking.model.User;
import org.itnaf.banking.service.RecipientService;
import org.itnaf.banking.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for managing saved recipients.
 * Handles viewing, adding, and deleting recipients.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/recipients")
public class RecipientController {

    private final UserService userService;
    private final RecipientService recipientService;

    /**
     * View all saved recipients.
     */
    @GetMapping
    public String viewRecipients(Authentication authentication, Model model) {
        User user = getAuthenticatedUser(authentication);
        List<Recipient> recipients = recipientService.getRecipientsForUser(user);

        model.addAttribute("user", user);
        model.addAttribute("recipients", recipients);

        return "recipients";
    }

    /**
     * Show form to add a new recipient.
     */
    @GetMapping("/add")
    public String addRecipientForm(Authentication authentication, Model model) {
        User user = getAuthenticatedUser(authentication);
        model.addAttribute("user", user);
        return "add-recipient";
    }

    /**
     * Process adding a new recipient.
     */
    @PostMapping("/add")
    public String addRecipient(
            Authentication authentication,
            @RequestParam("name") String name,
            @RequestParam("accountNumber") String accountNumber,
            @RequestParam(value = "nickname", required = false) String nickname,
            RedirectAttributes redirectAttributes) {

        User user = getAuthenticatedUser(authentication);

        try {
            Recipient recipient = new Recipient();
            recipient.setName(name);
            recipient.setAccountNumber(accountNumber);
            recipient.setNickname(nickname);

            recipientService.addRecipient(user, recipient);

            redirectAttributes.addFlashAttribute("success", "Recipient added successfully!");
            return "redirect:/recipients";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add recipient. Please try again.");
            return "redirect:/recipients/add";
        }
    }

    /**
     * Delete a recipient.
     */
    @PostMapping("/delete/{id}")
    public String deleteRecipient(
            Authentication authentication,
            @PathVariable("id") Long recipientId,
            RedirectAttributes redirectAttributes) {

        User user = getAuthenticatedUser(authentication);

        try {
            recipientService.deleteRecipient(recipientId, user);
            redirectAttributes.addFlashAttribute("success", "Recipient deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete recipient.");
        }

        return "redirect:/recipients";
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
