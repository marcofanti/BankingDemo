package org.itnaf.banking.security;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.itnaf.banking.service.SessionQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom authentication success handler that validates the session
 * with an external API before allowing the user to proceed.
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    @Value("${app.validation.ignore.emails:}")
    private String validationIgnoreEmailsRaw;

    private List<String> validationIgnoreEmails;

    @Autowired
    private SessionQueryService sessionQueryService;

    @PostConstruct
    private void init() {
        validationIgnoreEmails = Arrays.stream(validationIgnoreEmailsRaw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String email = authentication.getName();
        String sessionId = request.getParameter("session_id");
        String plainPassword = request.getParameter("password");

        logger.info("User {} authenticated successfully. Validating session with API...", email);

        // If session_id is missing, log warning but allow login
        if (sessionId == null || sessionId.trim().isEmpty()) {
            logger.warn("No session_id provided for user {}. Allowing login without API validation.", email);
            response.sendRedirect("/dashboard");
            return;
        }

        try {
            // Call the session query API
            SessionQueryService.SessionQueryResponse apiResponse =
                    sessionQueryService.querySession(sessionId, email, plainPassword, "login");

            // If API call failed but we allow login anyway
            if (!apiResponse.isApiCallSucceeded()) {
                logger.warn("Session query API call failed for user {}. Allowing login anyway. Error: {}",
                        email, apiResponse.getMessage());
                response.sendRedirect("/dashboard");
                return;
            }

            // If API call succeeded, check if session is valid
            if (apiResponse.isValid()) {
                logger.info("Session validated successfully for user {}. Redirecting to dashboard.", email);
                response.sendRedirect("/dashboard");
            } else {
                // Session validation failed
                logger.warn("Session validation failed for user {}. Reason: {}",
                        email, apiResponse.getMessage());

                // Check if user is in ignore list - allow login anyway
                if (validationIgnoreEmails.contains(email)) {
                    logger.info("User {} is in validation ignore list. Allowing login despite validation failure.", email);
                    response.sendRedirect("/dashboard");
                } else {
                    // Block login for users not in ignore list
                    logger.warn("Blocking access for user {}.", email);

                    // Invalidate the Spring Security session
                    request.getSession().invalidate();

                    // Redirect to login with error
                    response.sendRedirect("/login?error=Session validation failed. Please try again.");
                }
            }

        } catch (Exception e) {
            logger.error("Unexpected error during session validation for user {}. Allowing login.", email, e);
            response.sendRedirect("/dashboard");
        }
    }
}
