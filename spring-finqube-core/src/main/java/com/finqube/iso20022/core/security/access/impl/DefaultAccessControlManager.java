package com.finqube.iso20022.core.security.access.impl;

import com.finqube.iso20022.core.security.SecurityException;
import com.finqube.iso20022.core.security.access.*;
import com.finqube.iso20022.core.security.audit.AuditLogLevel;
import com.finqube.iso20022.core.security.audit.AuditLogger;
import com.finqube.iso20022.core.security.audit.RiskLevel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Thread-safe, in-memory RBAC manager.
 */
@Component
public class DefaultAccessControlManager implements AccessControlManager {

    private static final Logger log = LoggerFactory.getLogger(DefaultAccessControlManager.class);

    private final String managerId = UUID.randomUUID().toString();
    private final String version = "0.1.0";

    private final ConcurrentHashMap<String, Set<Role>> userRoles = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Role, Set<ResourceAction>> rolePermissions = new ConcurrentHashMap<>();

    private final AuditLogger auditLogger;

    @Autowired
    public DefaultAccessControlManager(AuditLogger auditLogger) {
        this.auditLogger = auditLogger;
        bootstrapDefaultPermissions();
    }

    private void bootstrapDefaultPermissions() {
        // ADMIN: wildcard (*) permission
        rolePermissions.put(Role.ADMIN, Collections.newSetFromMap(new ConcurrentHashMap<>()));

        // USER: basic message operations
        CopyOnWriteArraySet<ResourceAction> userPerms = new CopyOnWriteArraySet<>();
        userPerms.add(new ResourceAction("MESSAGE", "SEND"));
        userPerms.add(new ResourceAction("MESSAGE", "READ"));
        rolePermissions.put(Role.USER, userPerms);

        // AUDITOR: read-only access
        CopyOnWriteArraySet<ResourceAction> auditorPerms = new CopyOnWriteArraySet<>();
        auditorPerms.add(new ResourceAction("MESSAGE", "READ"));
        auditorPerms.add(new ResourceAction("AUDIT", "READ"));
        rolePermissions.put(Role.AUDITOR, auditorPerms);
    }

    @Override
    public AccessDecision decide(String userId, String resource, String action) {
        ResourceAction requested = new ResourceAction(resource, action);
        Set<Role> roles = userRoles.getOrDefault(userId, Set.of());
        boolean granted = roles.stream().anyMatch(role -> isPermitted(role, requested));

        // Log authorization event
        String status = granted ? "GRANTED" : "DENIED";
        String message = granted ? "Access granted" : "Access denied";
        AuditLogLevel level = granted ? AuditLogLevel.SECURITY : AuditLogLevel.ERROR;
        RiskLevel risk = granted ? RiskLevel.LOW : RiskLevel.HIGH;
        auditLogger.logAuthorization(userId, status, resource, action, message, level, risk);

        if (!granted) {
            log.warn("RBAC: access denied – user='{}' resource='{}' action='{}' roles={} at {}",
                    userId, resource, action, roles, LocalDateTime.now());
        } else {
            log.debug("RBAC: access granted – user='{}' resource='{}' action='{}' roles={}",
                    userId, resource, action, roles);
        }
        return granted ? AccessDecision.GRANTED : AccessDecision.DENIED;
    }

    private boolean isPermitted(Role role, ResourceAction requested) {
        // ADMIN wildcard permits everything
        if (Role.ADMIN.equals(role)) {
            return true;
        }
        return rolePermissions.getOrDefault(role, Set.of())
                .stream()
                .anyMatch(p -> p.resource().equals(requested.resource()) && p.action().equals(requested.action()));
    }

    @Override
    public void grantRole(String userId, Role role) {
        userRoles.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(role);
        log.info("RBAC: granted role '{}' to user '{}'", role, userId);
    }

    @Override
    public void revokeRole(String userId, Role role) {
        userRoles.getOrDefault(userId, Set.of()).remove(role);
        log.info("RBAC: revoked role '{}' from user '{}'", role, userId);
    }

    @Override
    public Set<Role> getUserRoles(String userId) {
        return Collections.unmodifiableSet(userRoles.getOrDefault(userId, Set.of()));
    }

    @Override
    public String getAccessControlManagerId() {
        return managerId;
    }

    @Override
    public String getDisplayName() {
        return "Default Access Control Manager";
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public AccessControlHealthCheck healthCheck() {
        boolean healthy = true; // simple check for now
        String status = healthy ? "HEALTHY" : "UNHEALTHY";
        String message = healthy ? "Access control subsystem operational" : "Access control issues detected";
        return new AccessControlHealthCheck(managerId, LocalDateTime.now(), status, message, healthy);
    }
}
