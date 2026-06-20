package com.northcode.waterplatform.security.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for security checks (file scanning, pattern matching, etc.)
 */
public class SecurityCheckUtil {

    private static final Logger logger = LoggerFactory.getLogger(SecurityCheckUtil.class);

    private static final String[] SECRET_PATTERNS = {
        "(?i)password\\s*[=:]+\\s*['\"]?[a-zA-Z0-9]{6,}['\"]?",
        "(?i)api[._-]?key\\s*[=:]+\\s*['\"]?[a-zA-Z0-9_]{10,}['\"]?",
        "(?i)secret\\s*[=:]+\\s*['\"]?[a-zA-Z0-9_]{10,}['\"]?",
        "(?i)token\\s*[=:]+\\s*['\"]?[a-zA-Z0-9_]{20,}['\"]?",
        "(?i)aws[._-]?key\\s*[=:]+",
        "(?i)private[._-]?key\\s*[=:]+"
    };

    /**
     * Scan a config file for hardcoded secrets matching known patterns
     */
    public static boolean containsHardcodedSecrets(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            for (String pattern : SECRET_PATTERNS) {
                if (content.matches("(?s).*" + pattern + ".*")) {
                    logger.warn("Found potential hardcoded secret in " + filePath);
                    return true;
                }
            }
        } catch (Exception e) {
            logger.warn("Could not scan file " + filePath + ": " + e.getMessage());
        }
        return false;
    }

    /**
     * Check if a string contains suspicious secret-like patterns
     */
    public static boolean looksLikeSecret(String value) {
        if (value == null || value.length() < 6) {
            return false;
        }
        return value.matches("^[a-zA-Z0-9_\\-]{15,}$") || 
               value.matches("^[a-zA-Z0-9+/]{20,}={0,2}$"); // base64-like
    }
}
