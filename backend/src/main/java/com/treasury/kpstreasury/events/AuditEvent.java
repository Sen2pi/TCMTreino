package com.treasury.kpstreasury.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuditEvent extends BaseEvent {
    
    public enum AuditEventType {
        CREATE,
        UPDATE,
        DELETE,
        LOGIN,
        LOGOUT,
        ACCESS_DENIED,
        SYSTEM_ERROR,
        DATA_EXPORT,
        CONFIGURATION_CHANGE
    }
    
    private AuditEventType auditEventType;
    private String entityType;
    private String entityId;
    private String action;
    private String userId;
    private String username;
    private String ipAddress;
    private String userAgent;
    private String details;
    private Map<String, Object> oldValues;
    private Map<String, Object> newValues;
    private boolean success;
    private String errorMessage;
    
    @Override
    public String getEventType() {
        return "AUDIT_EVENT";
    }
    
    public static AuditEvent createSuccessfulLogin(String userId, String username, String ipAddress) {
        AuditEvent event = new AuditEvent();
        event.setAuditEventType(AuditEventType.LOGIN);
        event.setUserId(userId);
        event.setUsername(username);
        event.setIpAddress(ipAddress);
        event.setAction("LOGIN");
        event.setSuccess(true);
        event.setDetails("User logged in successfully");
        return event;
    }
    
    public static AuditEvent createEntityModification(String entityType, String entityId, String action, 
                                                     String userId, Map<String, Object> oldValues, 
                                                     Map<String, Object> newValues) {
        AuditEvent event = new AuditEvent();
        event.setAuditEventType(AuditEventType.UPDATE);
        event.setEntityType(entityType);
        event.setEntityId(entityId);
        event.setAction(action);
        event.setUserId(userId);
        event.setOldValues(oldValues);
        event.setNewValues(newValues);
        event.setSuccess(true);
        event.setDetails(String.format("%s %s modified", entityType, entityId));
        return event;
    }
}