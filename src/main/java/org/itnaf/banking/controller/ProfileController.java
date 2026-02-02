package org.itnaf.banking.controller;

import lombok.RequiredArgsConstructor;
import org.itnaf.banking.model.User;
import org.itnaf.banking.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for user profile operations.
 * Handles logout confirmation and password changes.
 */
@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Show logout confirmation page.
     */
    @GetMapping("/logout-confirm")
    public String logoutConfirm(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            userService.findByEmail(email).ifPresent(user ->
                    model.addAttribute("user", user)
            );
        }
        return "logout-confirm";
    }

    /**
     * Show change password form.
     */
    @GetMapping("/change-password")
    public String changePasswordForm(Authentication authentication, Model model) {
        User user = getAuthenticatedUser(authentication);
        model.addAttribute("user", user);
        return "change-password";
    }

    /**
     * Process password change.
     */
    @PostMapping("/change-password")
    public String changePassword(
            Authentication authentication,
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {

        User user = getAuthenticatedUser(authentication);

        // Validate current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
            return "redirect:/change-password";
        }

        // Validate new password length
        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "New password must be at least 6 characters");
            return "redirect:/change-password";
        }

        // Validate passwords match
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match");
            return "redirect:/change-password";
        }

        // Validate new password is different from current
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "New password must be different from current password");
            return "redirect:/change-password";
        }

        try {
            // Update password
            userService.updatePassword(user, newPassword);

            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to change password. Please try again.");
            return "redirect:/change-password";
        }
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
