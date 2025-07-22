package com.treasury.kpstreasury.models.dto;


import com.treasury.kpstreasury.enums.CollateralType;
import com.treasury.kpstreasury.enums.Rating;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollateralSummaryDto {

    private CollateralType collateralType;
    private Rating rating;
    private String currency;
    private BigDecimal totalMarketValue;
    private BigDecimal totalEligibleValue;
    private Long count;
    private BigDecimal averageHaircut;
}