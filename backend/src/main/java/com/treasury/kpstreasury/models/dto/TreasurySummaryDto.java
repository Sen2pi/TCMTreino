package com.treasury.kpstreasury.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para resumos/relatórios de Treasury.
 * Usado em dashboards e APIs de sumários.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreasurySummaryDto {

    private String currency;
    private BigDecimal totalBalance;
    private BigDecimal totalAvailableBalance;
    private Long accountCount;
    private BigDecimal averageBalance;
}