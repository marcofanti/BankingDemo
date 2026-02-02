package org.itnaf.banking.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.itnaf.banking.service.SessionQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom authentication success handler that validates the session
 * with an external API before allowing the user to proceed.
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    @Autowired
    private SessionQueryService sessionQueryService;

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
                // Session validation failed - block login
                logger.warn("Session validation failed for user {}. Blocking access. Reason: {}",
                        email, apiResponse.getMessage());

                // Invalidate the Spring Security session
                request.getSession().invalidate();

                // Redirect to login with error
                response.sendRedirect("/login?error=Session validation failed. Please try again.");
            }

        } catch (Exception e) {
            logger.error("Unexpected error during session validation for user {}. Allowing login.", email, e);
            response.sendRedirect("/dashboard");
        }
    }
}
