package com.treasury.kpstreasury.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NotificationEvent extends BaseEvent {
    
    public enum NotificationType {
        EMAIL,
        SMS,
        PUSH_NOTIFICATION,
        SYSTEM_ALERT,
        DASHBOARD_ALERT
    }
    
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
    
    private NotificationType notificationType;
    private Priority priority;
    private String title;
    private String message;
    private String recipient;
    private List<String> recipients;
    private String templateId;
    private String relatedEntity;
    private String relatedEntityId;
    private boolean sent;
    private String errorMessage;
    
    @Override
    public String getEventType() {
        return "NOTIFICATION_EVENT";
    }
    
    public static NotificationEvent lowBalanceAlert(String recipient, String accountNumber, String balance) {
        NotificationEvent event = new NotificationEvent();
        event.setNotificationType(NotificationType.EMAIL);
        event.setPriority(Priority.HIGH);
        event.setTitle("Low Balance Alert");
        event.setMessage(String.format("Account %s has a low balance: %s", accountNumber, balance));
        event.setRecipient(recipient);
        event.setRelatedEntity("TreasuryAccount");
        event.setRelatedEntityId(accountNumber);
        return event;
    }
    
    public static NotificationEvent collateralExpiryWarning(String recipient, String collateralDescription, 
                                                           String expiryDate) {
        NotificationEvent event = new NotificationEvent();
        event.setNotificationType(NotificationType.EMAIL);
        event.setPriority(Priority.MEDIUM);
        event.setTitle("Collateral Expiry Warning");
        event.setMessage(String.format("Collateral '%s' will expire on %s", collateralDescription, expiryDate));
        event.setRecipient(recipient);
        event.setRelatedEntity("Collateral");
        return event;
    }
}