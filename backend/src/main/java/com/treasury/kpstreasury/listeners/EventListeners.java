package com.treasury.kpstreasury.listeners;

import com.treasury.kpstreasury.events.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventListeners {

    @KafkaListener(topics = "user-events", groupId = "treasury-group")
    public void handleUserEvent(@Payload UserEvent event,
                               @Header(KafkaHeaders.RECEIVED_KEY) String key,
                               @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                               @Header(KafkaHeaders.OFFSET) long offset,
                               Acknowledgment acknowledgment) {
        try {
            log.info("Received user event: {} for user: {} at partition: {} offset: {}", 
                    event.getUserEventType(), event.getUsername(), partition, offset);
            
            // Process the user event
            processUserEvent(event);
            
            // Acknowledge the message
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Error processing user event: {}", e.getMessage(), e);
            // Don't acknowledge on error - message will be retried
        }
    }

    @KafkaListener(topics = "treasury-events", groupId = "treasury-group")
    public void handleTreasuryEvent(@Payload TreasuryEvent event,
                                   @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                   @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                   @Header(KafkaHeaders.OFFSET) long offset,
                                   Acknowledgment acknowledgment) {
        try {
            log.info("Received treasury event: {} for account: {} at partition: {} offset: {}", 
                    event.getTreasuryEventType(), event.getAccountNumber(), partition, offset);
            
            // Process the treasury event
            processTreasuryEvent(event);
            
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Error processing treasury event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "collateral-events", groupId = "treasury-group")
    public void handleCollateralEvent(@Payload CollateralEvent event,
                                     @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                     @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                     @Header(KafkaHeaders.OFFSET) long offset,
                                     Acknowledgment acknowledgment) {
        try {
            log.info("Received collateral event: {} for collateral: {} at partition: {} offset: {}", 
                    event.getCollateralEventType(), event.getCollateralId(), partition, offset);
            
            // Process the collateral event
            processCollateralEvent(event);
            
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Error processing collateral event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "audit-events", groupId = "audit-group")
    public void handleAuditEvent(@Payload AuditEvent event,
                                @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                @Header(KafkaHeaders.OFFSET) long offset,
                                Acknowledgment acknowledgment) {
        try {
            log.info("Received audit event: {} for entity: {} at partition: {} offset: {}", 
                    event.getAuditEventType(), event.getEntityId(), partition, offset);
            
            // Process the audit event (store in audit database, etc.)
            processAuditEvent(event);
            
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Error processing audit event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "notification-events", groupId = "notification-group")
    public void handleNotificationEvent(@Payload NotificationEvent event,
                                       @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                       @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                       @Header(KafkaHeaders.OFFSET) long offset,
                                       Acknowledgment acknowledgment) {
        try {
            log.info("Received notification event: {} for recipient: {} at partition: {} offset: {}", 
                    event.getNotificationType(), event.getRecipient(), partition, offset);
            
            // Process the notification event
            processNotificationEvent(event);
            
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Error processing notification event: {}", e.getMessage(), e);
        }
    }

    private void processUserEvent(UserEvent event) {
        switch (event.getUserEventType()) {
            case USER_CREATED:
                log.info("Processing user creation for: {}", event.getUsername());
                // Send welcome email, create user profile, etc.
                break;
            case USER_LOGIN:
                log.info("Processing user login for: {}", event.getUsername());
                // Update last login time, log security event, etc.
                break;
            case PASSWORD_CHANGED:
                log.info("Processing password change for: {}", event.getUsername());
                // Send security notification, log event, etc.
                break;
            default:
                log.info("Processing user event: {}", event.getUserEventType());
        }
    }

    private void processTreasuryEvent(TreasuryEvent event) {
        switch (event.getTreasuryEventType()) {
            case ACCOUNT_CREATED:
                log.info("Processing treasury account creation: {}", event.getAccountNumber());
                // Initialize account monitoring, send notifications, etc.
                break;
            case FUNDS_TRANSFERRED:
                log.info("Processing funds transfer from {} to {}", 
                        event.getFromAccountId(), event.getToAccountId());
                // Update balances, create transaction records, etc.
                break;
            case LOW_BALANCE_ALERT:
                log.info("Processing low balance alert for account: {}", event.getAccountNumber());
                // Send alerts to treasury managers
                break;
            default:
                log.info("Processing treasury event: {}", event.getTreasuryEventType());
        }
    }

    private void processCollateralEvent(CollateralEvent event) {
        switch (event.getCollateralEventType()) {
            case COLLATERAL_CREATED:
                log.info("Processing collateral creation: {}", event.getDescription());
                // Start monitoring, schedule revaluation, etc.
                break;
            case COLLATERAL_MATURED:
                log.info("Processing collateral maturity: {}", event.getDescription());
                // Update status, notify stakeholders, etc.
                break;
            case MARKET_VALUE_UPDATED:
                log.info("Processing market value update for collateral: {}", event.getCollateralId());
                // Recalculate risk metrics, update reports, etc.
                break;
            default:
                log.info("Processing collateral event: {}", event.getCollateralEventType());
        }
    }

    private void processAuditEvent(AuditEvent event) {
        log.info("Storing audit event: {} for entity: {}", event.getAction(), event.getEntityType());
        // Store in audit database for compliance and reporting
        // This could write to a dedicated audit database or data warehouse
    }

    private void processNotificationEvent(NotificationEvent event) {
        log.info("Processing notification: {} for recipient: {}", event.getTitle(), event.getRecipient());
        
        switch (event.getNotificationType()) {
            case EMAIL:
                // Send email notification
                sendEmailNotification(event);
                break;
            case SMS:
                // Send SMS notification
                sendSmsNotification(event);
                break;
            case SYSTEM_ALERT:
                // Create system alert
                createSystemAlert(event);
                break;
            default:
                log.info("Processing notification of type: {}", event.getNotificationType());
        }
    }

    private void sendEmailNotification(NotificationEvent event) {
        // Implementation for email sending
        log.info("Sending email to {}: {}", event.getRecipient(), event.getTitle());
    }

    private void sendSmsNotification(NotificationEvent event) {
        // Implementation for SMS sending
        log.info("Sending SMS to {}: {}", event.getRecipient(), event.getMessage());
    }

    private void createSystemAlert(NotificationEvent event) {
        // Implementation for system alerts
        log.info("Creating system alert: {}", event.getTitle());
    }
}