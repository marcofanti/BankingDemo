package org.itnaf.banking.controller;

import lombok.RequiredArgsConstructor;
import org.itnaf.banking.model.BankAccount;
import org.itnaf.banking.model.Transaction;
import org.itnaf.banking.model.User;
import org.itnaf.banking.service.BankAccountService;
import org.itnaf.banking.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for the dashboard view.
 * Displays user's bank accounts, transactions, and financial overview.
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final BankAccountService bankAccountService;

    /**
     * Display the user dashboard.
     * Shows accounts, total balance, and recent transactions.
     *
     * @param authentication Spring Security authentication object
     * @param model Spring MVC model
     * @return dashboard template name
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        // Get the currently logged-in user
        String email = authentication.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get user's bank accounts
        List<BankAccount> accounts = bankAccountService.getAccountsForUser(user);

        // Calculate total balance
        BigDecimal totalBalance = bankAccountService.getTotalBalance(user);

        // Get recent transactions
        List<Transaction> recentTransactions = bankAccountService.getRecentTransactions(user.getId());

        // Add data to model
        model.addAttribute("user", user);
        model.addAttribute("accounts", accounts);
        model.addAttribute("totalBalance", totalBalance);
        model.addAttribute("transactions", recentTransactions);

        // Calculate greeting based on time of day
        int hour = java.time.LocalTime.now().getHour();
        String greeting = hour < 12 ? "morning" : (hour < 18 ? "afternoon" : "evening");
        model.addAttribute("greeting", greeting);

        return "dashboard";
    }
}
