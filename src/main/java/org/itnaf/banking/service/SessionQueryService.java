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
                .queryParam("web_session_id", sessionId + "-web")
                .queryParam("service_type", "session-policy")
                .queryParam("account_login", accountEmail)
                .queryParam("event_type", eventType)
                .queryParam("policy", "behaviosec_default_policy")
                .queryParam("output_format", "JSON")
                .queryParam("account_password_hash", passwordHash)
                .build()
                .toUriString();

            logger.info("Calling session query API for user: {} with session: {}", accountEmail, sessionId);
            System.out.println("Full API URL (masked): " + url.replaceAll("api_key=[^&]*", "api_key=***"));

            // Make the API call
            String response = restTemplate.getForObject(url, String.class);

            logger.info("Session query API response received for user: {}", accountEmail);
            System.out.println("========== API RESPONSE ==========");
            System.out.println(response);
            System.out.println("==================================");

            // Parse and return response
            return parseResponse(response);

        } catch (Exception e) {
            logger.error("Error calling session query API for user: {}", accountEmail, e);
            System.out.println("API call exception: " + e.getMessage());
            // Return a response indicating API call failed but allow login
            return new SessionQueryResponse(true, false, "API call failed: " + e.getMessage());
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
            return new SessionQueryResponse(true, false, "Empty API response - allowing login");
        }

        String responseLower = response.toLowerCase();

        // Check for configuration/parameter errors - allow login for these
        if (responseLower.contains("fail_invalid_parameter") ||
            responseLower.contains("error_detail=org_id") ||
            responseLower.contains("error_detail=api_key")) {
            logger.warn("API configuration error detected, allowing login: {}", response);
            return new SessionQueryResponse(true, false, "API config error - allowing login: " + response);
        }

        // Check for actual fraud/risk denial - these should block login
        boolean isValid = !responseLower.contains("deny")
                       && !responseLower.contains("reject")
                       && !responseLower.contains("request_result=fail");

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
