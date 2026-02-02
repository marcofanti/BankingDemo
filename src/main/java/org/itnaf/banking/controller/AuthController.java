package org.itnaf.banking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.itnaf.banking.model.User;
import org.itnaf.banking.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for authentication operations.
 * Handles user registration (login is handled by Spring Security).
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * Handle user registration (signup).
     * Validates input, checks for duplicate emails, and creates new user account.
     *
     * @param user the user data from signup form
     * @param bindingResult validation results
     * @param redirectAttributes for flash messages
     * @return redirect to login page on success, signup page on error
     */
    @PostMapping("/signup")
    public String registerUser(
            @Valid @ModelAttribute User user,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please check your input and try again.");
            return "redirect:/signup?error=Please check your input";
        }

        // Check if email already exists
        if (userService.emailExists(user.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "Email address is already registered.");
            return "redirect:/signup?error=Email already registered";
        }

        try {
            // Register the user
            userService.registerUser(user);

            // Redirect to login with success message
            redirectAttributes.addFlashAttribute("message", "Account created successfully! Please log in.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed. Please try again.");
            return "redirect:/signup?error=Registration failed";
        }
    }
}
