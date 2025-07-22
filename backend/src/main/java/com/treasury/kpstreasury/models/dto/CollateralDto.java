package com.treasury.kpstreasury.models.dto;

import com.treasury.kpstreasury.enums.CollateralStatus;
import com.treasury.kpstreasury.enums.CollateralType;
import com.treasury.kpstreasury.enums.Rating;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollateralDto {

    private Long id;

    @NotNull(message = "Collateral type is required")
    private CollateralType collateralType;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description cannot have more than 255 characters")
    private String description;

    @NotNull(message = "Market value is required")
    @DecimalMin(value = "0.01", message = "Market value must be greater than zero")
    @Digits(integer = 17, fraction = 2, message = "Market value must have at most 17 integer digits and 2 decimals")
    private BigDecimal marketValue;

    @NotNull(message = "Haircut is required")
    @DecimalMin(value = "0.0", message = "Haircut must be greater than or equal to zero")
    @DecimalMax(value = "1.0", message = "Haircut must be less than or equal to 1.0 (100%)")
    @Digits(integer = 1, fraction = 4, message = "Haircut must have format 0.XXXX")
    private BigDecimal haircut;

    private BigDecimal eligibleValue; // Calculated automatically

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must have 3 characters (ISO 4217)")
    private String currency;

    @NotNull(message = "Rating is required")
    private Rating rating;

    @NotNull(message = "Maturity date is required")
    @Future(message = "Maturity date must be in the future")
    private LocalDate maturityDate;

    @NotNull(message = "Status is required")
    private CollateralStatus status;

    @NotBlank(message = "Counterparty is required")
    @Size(max = 100, message = "Counterparty cannot have more than 100 characters")
    private String counterparty;

    @NotBlank(message = "Location is required")
    @Size(max = 100, message = "Location cannot have more than 100 characters")
    private String location;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
