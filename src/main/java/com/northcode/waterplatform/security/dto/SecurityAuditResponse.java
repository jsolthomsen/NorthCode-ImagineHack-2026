package com.northcode.waterplatform.security.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SecurityAuditResponse {

    private List<SecurityCheckResult> checks;
    private LocalDateTime timestamp;
    private String summary;

    public SecurityAuditResponse() {
        this.checks = new ArrayList<>();
        this.timestamp = LocalDateTime.now();
    }

    public void addCheck(SecurityCheckResult check) {
        this.checks.add(check);
    }

    public String generateSummary() {
        long passed = checks.stream().filter(c -> "PASSED".equals(c.getStatus())).count();
        long failed = checks.stream().filter(c -> "FAILED".equals(c.getStatus())).count();
        long warnings = checks.stream().filter(c -> "WARNING".equals(c.getStatus())).count();
        this.summary = String.format("Passed: %d | Warnings: %d | Failed: %d", passed, warnings, failed);
        return this.summary;
    }

    public List<SecurityCheckResult> getChecks() {
        return checks;
    }

    public void setChecks(List<SecurityCheckResult> checks) {
        this.checks = checks;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
