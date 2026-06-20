package com.northcode.waterplatform.controller;

import com.northcode.waterplatform.security.dto.SecurityAuditResponse;
import com.northcode.waterplatform.security.service.SecurityCheckService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API endpoints for security audit results and status.
 */
@RestController
@RequestMapping("/api/security")
public class SecurityController {

    private final SecurityCheckService securityCheckService;

    public SecurityController(SecurityCheckService securityCheckService) {
        this.securityCheckService = securityCheckService;
    }

    /**
     * GET /api/security/status
     * Returns security audit results with overall status (green/amber/red)
     */
    @GetMapping("/status")
    public SecurityAuditResponse getSecurityStatus() {
        return securityCheckService.runFullAudit();
    }
}
