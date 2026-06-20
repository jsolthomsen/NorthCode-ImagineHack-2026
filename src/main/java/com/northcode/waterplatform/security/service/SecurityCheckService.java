package com.northcode.waterplatform.security.service;

import com.northcode.waterplatform.security.dto.SecurityAuditResponse;
import com.northcode.waterplatform.security.dto.SecurityCheckResult;
import com.northcode.waterplatform.security.util.SecurityCheckUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Service to run security audits on application startup.
 * Checks for common vulnerabilities and misconfigurations.
 */
@Service
public class SecurityCheckService {

    private static final Logger logger = LoggerFactory.getLogger(SecurityCheckService.class);
    private final Environment environment;

    public SecurityCheckService(Environment environment) {
        this.environment = environment;
    }

    /**
     * Run all security checks and return audit results
     */
    public SecurityAuditResponse runFullAudit() {
        SecurityAuditResponse response = new SecurityAuditResponse();

        response.addCheck(checkForHardcodedSecrets());
        response.addCheck(checkHttpsEnforcement());
        response.addCheck(checkEndpointAuthentication());
        response.addCheck(checkActuatorExposure());

        response.generateSummary();
        logger.info("Security Audit Complete: " + response.getSummary());

        return response;
    }

    /**
     * Check 1: Scan for hardcoded secrets in config files
     */
    private SecurityCheckResult checkForHardcodedSecrets() {
        SecurityCheckResult result = new SecurityCheckResult("Hardcoded Secrets Scan");

        try {
            String[] configFiles = {
                "src/main/resources/application.properties",
                "src/main/resources/application.yml",
                "src/main/resources/application-prod.properties"
            };

            boolean found = false;
            for (String configFile : configFiles) {
                if (SecurityCheckUtil.containsHardcodedSecrets(configFile)) {
                    result.addIssue("Found potential hardcoded secret in " + configFile);
                    found = true;
                }
            }

            if (found) {
                result.setStatus("FAILED");
                result.setRecommendation("Use environment variables or Spring Cloud Config instead of hardcoded secrets");
            } else {
                result.setStatus("PASSED");
                result.setMessage("No obvious hardcoded secrets found in config files");
            }
        } catch (Exception e) {
            logger.error("Error during hardcoded secrets check: " + e.getMessage());
            result.setStatus("ERROR");
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * Check 2: Verify HTTPS is enforced in production
     */
    private SecurityCheckResult checkHttpsEnforcement() {
        SecurityCheckResult result = new SecurityCheckResult("HTTPS Enforcement");

        try {
            String[] profiles = environment.getActiveProfiles();
            boolean isProd = Arrays.asList(profiles).contains("prod");

            if (isProd) {
                boolean httpsEnabled = environment.getProperty("server.ssl.enabled") != null;
                if (!httpsEnabled) {
                    result.setStatus("FAILED");
                    result.addIssue("HTTPS is not enabled in production");
                    result.setRecommendation("Set server.ssl.enabled=true in production config");
                } else {
                    result.setStatus("PASSED");
                    result.setMessage("HTTPS is properly configured for production");
                }
            } else {
                result.setStatus("PASSED");
                result.setMessage("Development/Test environment - HTTPS check skipped");
            }
        } catch (Exception e) {
            logger.error("Error during HTTPS check: " + e.getMessage());
            result.setStatus("ERROR");
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * Check 3: Verify critical endpoints require authentication
     */
    private SecurityCheckResult checkEndpointAuthentication() {
        SecurityCheckResult result = new SecurityCheckResult("Endpoint Authentication");

        try {
            boolean hasSpringSecurityConfig = 
                environment.getProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri") != null ||
                environment.getProperty("spring.security.user.password") != null;

            if (!hasSpringSecurityConfig) {
                result.setStatus("WARNING");
                result.addIssue("Spring Security not fully configured - ensure critical endpoints are protected with @PreAuthorize");
                result.setMessage("No JWT or OAuth2 config detected");
                result.setRecommendation("Configure authentication for critical endpoints like /api/sites/{id}, /api/upload");
            } else {
                result.setStatus("PASSED");
                result.setMessage("Spring Security is configured");
            }

            String corsAllowed = environment.getProperty("app.cors.allowed-origins");
            if ("*".equals(corsAllowed)) {
                result.setStatus("WARNING");
                result.addIssue("CORS allows all origins (*) - potential security risk");
                result.setRecommendation("Restrict CORS to specific trusted domains");
            }
        } catch (Exception e) {
            logger.error("Error during authentication check: " + e.getMessage());
            result.setStatus("ERROR");
            result.setMessage(e.getMessage());
        }

        return result;
    }

    /**
     * Check 4: Verify Spring Boot Actuator is not over-exposed
     */
    private SecurityCheckResult checkActuatorExposure() {
        SecurityCheckResult result = new SecurityCheckResult("Actuator Exposure");

        try {
            String exposedEndpoints = environment.getProperty("management.endpoints.web.exposure.include");

            if ("*".equals(exposedEndpoints)) {
                result.setStatus("FAILED");
                result.addIssue("Spring Boot Actuator exposes ALL endpoints - potential information disclosure");
                result.setRecommendation("Limit to: management.endpoints.web.exposure.include=health,metrics");
            } else if (exposedEndpoints != null && exposedEndpoints.contains("env")) {
                result.setStatus("WARNING");
                result.addIssue("Actuator /env endpoint is exposed - may leak configuration details");
                result.setRecommendation("Remove 'env' from exposed endpoints");
            } else {
                result.setStatus("PASSED");
                result.setMessage("Actuator endpoints are properly restricted");
            }
        } catch (Exception e) {
            logger.error("Error during actuator check: " + e.getMessage());
            result.setStatus("ERROR");
            result.setMessage(e.getMessage());
        }

        return result;
    }
}
