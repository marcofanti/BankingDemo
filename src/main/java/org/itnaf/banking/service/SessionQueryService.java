package org.itnaf.banking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

/**
 * Service for querying the session validation API after user login.
 * Validates user sessions against external fraud detection service.
 */
@Service
public class SessionQueryService {

    private static final Logger logger = LoggerFactory.getLogger(SessionQueryService.class);

    @Value("${app.api.base-url}")
    private String apiBaseUrl;

    @Value("${app.api.key}")
    private String apiKey;

    @Value("${app.org.id}")
    private String orgId;

    private final RestTemplate restTemplate;

    public SessionQueryService() {
        this.restTemplate = new RestTemplate();
        // Set timeout to 5 seconds
        this.restTemplate.getRequestFactory();
    }

    /**
     * Query the session validation API.
     *
     * @param sessionId Session ID from the landing page
     * @param accountEmail User's email address
     * @param plainPassword User's plain text password (will be hashed)
     * @param eventType Type of event (e.g., "login", "signup")
     * @return SessionQueryResponse containing validation result
     */
    public SessionQueryResponse querySession(String sessionId, String accountEmail, String plainPassword, String eventType) {
        try {
            // Hash the password using SHA-256
            String passwordHash = hashPassword(plainPassword);

            // Build the API URL with query parameters
            String url = UriComponentsBuilder.fromHttpUrl(apiBaseUrl)
                    .path("/api/session-query")
                    .queryParam("org_id", orgId)
                    .queryParam("api_key", apiKey)
                    .queryParam("session_id", sessionId)
                    .queryParam("service_type", "session-policy")
                    .queryParam("account_login", accountEmail)
                    .queryParam("account_email", accountEmail)
                    .queryParam("password_hash", passwordHash)
                    .queryParam("event_type", eventType)
                    .build()
                    .toUriString();

            logger.info("Calling session query API for user: {} with session: {}", accountEmail, sessionId);
            logger.debug("API URL: {}", url.replaceAll("api_key=[^&]*", "api_key=***"));

            // Make the API call
            String response = restTemplate.getForObject(url, String.class);

            logger.info("Session query API response received for user: {}", accountEmail);
            logger.debug("Response: {}", response);

            // Parse and return response
            return parseResponse(response);

        } catch (Exception e) {
            logger.error("Error calling session query API for user: {}", accountEmail, e);
            // Return a response indicating API call failed but allow login
            return new SessionQueryResponse(false, true, "API call failed: " + e.getMessage());
        }
    }

    /**
     * Hash password using SHA-256.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString().toUpperCase();

        } catch (NoSuchAlgorithmException e) {
            logger.error("Error hashing password", e);
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    /**
     * Parse the API response.
     * This is a simple implementation - adjust based on actual API response format.
     */
    private SessionQueryResponse parseResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            logger.warn("Empty response from session query API");
            return new SessionQueryResponse(false, true, "Empty API response");
        }

        // Simple parsing - adjust based on actual API response format
        // Assuming API returns JSON or similar format indicating success/failure
        boolean isValid = !response.toLowerCase().contains("deny")
                       && !response.toLowerCase().contains("reject")
                       && !response.toLowerCase().contains("fail");

        return new SessionQueryResponse(isValid, true, response);
    }

    /**
     * Response object from session query API.
     */
    public static class SessionQueryResponse {
        private final boolean valid;
        private final boolean apiCallSucceeded;
        private final String message;

        public SessionQueryResponse(boolean valid, boolean apiCallSucceeded, String message) {
            this.valid = valid;
            this.apiCallSucceeded = apiCallSucceeded;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public boolean isApiCallSucceeded() {
            return apiCallSucceeded;
        }

        public String getMessage() {
            return message;
        }
    }
}
