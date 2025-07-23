package com.treasury.kpstreasury.events;

import com.treasury.kpstreasury.enums.AccountStatus;
import com.treasury.kpstreasury.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TreasuryEvent extends BaseEvent {
    
    public enum TreasuryEventType {
        ACCOUNT_CREATED,
        ACCOUNT_UPDATED,
        ACCOUNT_DELETED,
        BALANCE_UPDATED,
        AVAILABLE_BALANCE_UPDATED,
        FUNDS_TRANSFERRED,
        ACCOUNT_ACTIVATED,
        ACCOUNT_DEACTIVATED,
        ACCOUNT_SUSPENDED,
        LOW_BALANCE_ALERT
    }
    
    private TreasuryEventType treasuryEventType;
    private Long accountId;
    private String accountNumber;
    private AccountType accountType;
    private AccountStatus status;
    private String currency;
    private BigDecimal amount;
    private BigDecimal previousAmount;
    private Long fromAccountId;
    private Long toAccountId;
    private String performedBy;
    private String details;
    
    @Override
    public String getEventType() {
        return "TREASURY_EVENT";
    }
    
    public static TreasuryEvent accountCreated(Long accountId, String accountNumber, AccountType accountType, 
                                             String currency, BigDecimal initialBalance, String performedBy) {
        TreasuryEvent event = new TreasuryEvent();
        event.setTreasuryEventType(TreasuryEventType.ACCOUNT_CREATED);
        event.setAccountId(accountId);
        event.setAccountNumber(accountNumber);
        event.setAccountType(accountType);
        event.setCurrency(currency);
        event.setAmount(initialBalance);
        event.setPerformedBy(performedBy);
        event.setDetails("New treasury account created");
        return event;
    }
    
    public static TreasuryEvent fundsTransferred(Long fromAccountId, Long toAccountId, BigDecimal amount, 
                                               String performedBy) {
        TreasuryEvent event = new TreasuryEvent();
        event.setTreasuryEventType(TreasuryEventType.FUNDS_TRANSFERRED);
        event.setFromAccountId(fromAccountId);
        event.setToAccountId(toAccountId);
        event.setAmount(amount);
        event.setPerformedBy(performedBy);
        event.setDetails(String.format("Transferred %s from account %d to account %d", amount, fromAccountId, toAccountId));
        return event;
    }
}