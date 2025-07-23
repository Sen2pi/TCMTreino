package com.treasury.kpstreasury.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "eventType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = UserEvent.class, name = "USER_EVENT"),
    @JsonSubTypes.Type(value = TreasuryEvent.class, name = "TREASURY_EVENT"),
    @JsonSubTypes.Type(value = CollateralEvent.class, name = "COLLATERAL_EVENT"),
    @JsonSubTypes.Type(value = AuditEvent.class, name = "AUDIT_EVENT"),
    @JsonSubTypes.Type(value = NotificationEvent.class, name = "NOTIFICATION_EVENT")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEvent {
    
    private String eventId = UUID.randomUUID().toString();
    private LocalDateTime timestamp = LocalDateTime.now();
    private String source = "KPS-Treasury-System";
    private String version = "1.0";
    
    public abstract String getEventType();
}