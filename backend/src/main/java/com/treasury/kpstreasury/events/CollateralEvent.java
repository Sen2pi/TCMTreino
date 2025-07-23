package com.treasury.kpstreasury.events;

import com.treasury.kpstreasury.enums.CollateralStatus;
import com.treasury.kpstreasury.enums.CollateralType;
import com.treasury.kpstreasury.enums.Rating;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CollateralEvent extends BaseEvent {
    
    public enum CollateralEventType {
        COLLATERAL_CREATED,
        COLLATERAL_UPDATED,
        COLLATERAL_DELETED,
        MARKET_VALUE_UPDATED,
        STATUS_CHANGED,
        COLLATERAL_MATURED,
        COLLATERAL_REVALUED,
        HAIRCUT_UPDATED,
        EXPIRY_WARNING
    }
    
    private CollateralEventType collateralEventType;
    private Long collateralId;
    private CollateralType collateralType;
    private String description;
    private BigDecimal marketValue;
    private BigDecimal previousMarketValue;
    private BigDecimal haircut;
    private BigDecimal eligibleValue;
    private String currency;
    private Rating rating;
    private LocalDate maturityDate;
    private CollateralStatus status;
    private CollateralStatus previousStatus;
    private String counterparty;
    private String location;
    private String performedBy;
    private String details;
    
    @Override
    public String getEventType() {
        return "COLLATERAL_EVENT";
    }
    
    public static CollateralEvent collateralCreated(Long collateralId, CollateralType collateralType, 
                                                   String description, BigDecimal marketValue, 
                                                   String currency, String performedBy) {
        CollateralEvent event = new CollateralEvent();
        event.setCollateralEventType(CollateralEventType.COLLATERAL_CREATED);
        event.setCollateralId(collateralId);
        event.setCollateralType(collateralType);
        event.setDescription(description);
        event.setMarketValue(marketValue);
        event.setCurrency(currency);
        event.setPerformedBy(performedBy);
        event.setDetails("New collateral created");
        return event;
    }
    
    public static CollateralEvent collateralMatured(Long collateralId, String description, 
                                                   LocalDate maturityDate, String performedBy) {
        CollateralEvent event = new CollateralEvent();
        event.setCollateralEventType(CollateralEventType.COLLATERAL_MATURED);
        event.setCollateralId(collateralId);
        event.setDescription(description);
        event.setMaturityDate(maturityDate);
        event.setStatus(CollateralStatus.MATURED);
        event.setPerformedBy(performedBy);
        event.setDetails("Collateral has reached maturity");
        return event;
    }
}