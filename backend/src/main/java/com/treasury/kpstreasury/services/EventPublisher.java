package com.treasury.kpstreasury.services;

import com.treasury.kpstreasury.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String USER_EVENTS_TOPIC = "user-events";
    private static final String TREASURY_EVENTS_TOPIC = "treasury-events";
    private static final String COLLATERAL_EVENTS_TOPIC = "collateral-events";
    private static final String AUDIT_EVENTS_TOPIC = "audit-events";
    private static final String NOTIFICATION_EVENTS_TOPIC = "notification-events";

    public void publishUserEvent(UserEvent event) {
        publishEvent(USER_EVENTS_TOPIC, event.getUserId().toString(), event);
    }

    public void publishTreasuryEvent(TreasuryEvent event) {
        String key = event.getAccountId() != null ? event.getAccountId().toString() : 
                    event.getAccountNumber() != null ? event.getAccountNumber() : "unknown";
        publishEvent(TREASURY_EVENTS_TOPIC, key, event);
    }

    public void publishCollateralEvent(CollateralEvent event) {
        publishEvent(COLLATERAL_EVENTS_TOPIC, event.getCollateralId().toString(), event);
    }

    public void publishAuditEvent(AuditEvent event) {
        String key = event.getEntityId() != null ? event.getEntityId() : 
                    event.getUserId() != null ? event.getUserId() : "system";
        publishEvent(AUDIT_EVENTS_TOPIC, key, event);
    }

    public void publishNotificationEvent(NotificationEvent event) {
        String key = event.getRecipient() != null ? event.getRecipient() : "broadcast";
        publishEvent(NOTIFICATION_EVENTS_TOPIC, key, event);
    }

    private void publishEvent(String topic, String key, BaseEvent event) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Event published successfully to topic [{}] with key [{}]: {}", 
                            topic, key, event.getEventType());
                } else {
                    log.error("Failed to publish event to topic [{}] with key [{}]: {}", 
                            topic, key, ex.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("Error publishing event to topic [{}]: {}", topic, e.getMessage());
        }
    }

    // Utility methods for common events
    public void publishUserCreated(Long userId, String username, String email, 
                                  com.treasury.kpstreasury.enums.Role role, String performedBy) {
        UserEvent event = UserEvent.userCreated(userId, username, email, role, performedBy);
        publishUserEvent(event);
    }

    public void publishUserLogin(Long userId, String username) {
        UserEvent event = UserEvent.userLogin(userId, username);
        publishUserEvent(event);
    }

    public void publishTreasuryAccountCreated(Long accountId, String accountNumber, 
                                            com.treasury.kpstreasury.enums.AccountType accountType,
                                            String currency, java.math.BigDecimal initialBalance, 
                                            String performedBy) {
        TreasuryEvent event = TreasuryEvent.accountCreated(accountId, accountNumber, accountType, 
                                                         currency, initialBalance, performedBy);
        publishTreasuryEvent(event);
    }

    public void publishFundsTransferred(Long fromAccountId, Long toAccountId, 
                                      java.math.BigDecimal amount, String performedBy) {
        TreasuryEvent event = TreasuryEvent.fundsTransferred(fromAccountId, toAccountId, amount, performedBy);
        publishTreasuryEvent(event);
    }

    public void publishCollateralCreated(Long collateralId, 
                                       com.treasury.kpstreasury.enums.CollateralType collateralType,
                                       String description, java.math.BigDecimal marketValue, 
                                       String currency, String performedBy) {
        CollateralEvent event = CollateralEvent.collateralCreated(collateralId, collateralType, 
                                                                 description, marketValue, currency, performedBy);
        publishCollateralEvent(event);
    }

    public void publishLowBalanceAlert(String recipient, String accountNumber, String balance) {
        NotificationEvent event = NotificationEvent.lowBalanceAlert(recipient, accountNumber, balance);
        publishNotificationEvent(event);
    }
}