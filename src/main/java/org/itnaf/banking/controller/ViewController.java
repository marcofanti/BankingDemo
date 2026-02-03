package org.itnaf.banking.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for serving HTML views.
 * Handles landing page, login page, and signup page.
 */
@Controller
public class ViewController {

    @Value("${app.org.id}")
    private String orgId;

    @Value("${app.page.id}")
    private String pageId;

    @Value("${app.profiling.server}")
    private String profilingServer;

    /**
     * Landing page - displays marketing content and call to action.
     * Accessible to all users (authenticated and unauthenticated).
     */
    @GetMapping("/")
    public String landingPage(Model model) {
        model.addAttribute("orgId", orgId);
        model.addAttribute("pageId", pageId);
        model.addAttribute("profilingServer", profilingServer);
        return "landing";
    }

    /**
     * Login page - displays login form.
     * Redirects to dashboard if already authenticated.
     *
     * @param error indicates if login failed
     * @param logout indicates if user just logged out
     * @param model Spring MVC model
     */
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "login";
    }

    /**
     * Signup page - displays registration form.
     */
    @GetMapping("/signup")
    public String signupPage(
            @RequestParam(value = "error", required = false) String error,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "signup";
    }
}
