package com.northcode.waterplatform.security.dto;

import java.util.ArrayList;
import java.util.List;

public class SecurityCheckResult {

    private String checkName;
    private String status; // PASSED, WARNING, FAILED, ERROR
    private String message;
    private List<String> issues;
    private String recommendation;

    public SecurityCheckResult(String checkName) {
        this.checkName = checkName;
        this.status = "UNKNOWN";
        this.issues = new ArrayList<>();
    }

    public String getCheckName() {
        return checkName;
    }

    public void setCheckName(String checkName) {
        this.checkName = checkName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getIssues() {
        return issues;
    }

    public void setIssues(List<String> issues) {
        this.issues = issues;
    }

    public void addIssue(String issue) {
        this.issues.add(issue);
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}
