package com.treasury.kpstreasury.models.dto;

import com.treasury.kpstreasury.enums.AccountStatus;
import com.treasury.kpstreasury.enums.AccountType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreasuryDto {

    private Long id;

    @NotBlank(message = "Account Number is Required")
    @Size(max = 50, message = "Account Number should have at max 50 characters")
    private String accountNumber;

    @NotBlank(message = "Currency is Required")
    @Size(min = 3, max = 3, message = "Currency must be composed of 3 charcters only (ISO 4217)")
    private String currency;

    @NotNull(message = "balance is required")
    @DecimalMin(value = "0.0", message = "Balance must be greater orequal to 0")
    @Digits(integer = 17, fraction = 2, message = "Balance must have at max 17 digits and 2 decimal cases")
    private BigDecimal balance;

    @NotNull(message = "available balance is required")
    @DecimalMin(value = "0.0", message = "Available balance must be greater or equal to 0")
    @Digits(integer = 17, fraction = 2, message = "Available balancemust have at max 17 digits and 2 decimal cases")
    private BigDecimal availableBalance;

    @NotNull(message = "Account type  is required")
    private AccountType accountType;

    @NotNull(message = "Account Status is required")
    private AccountStatus status;

    @NotBlank(message = "Bank Name  is required")
    @Size(max = 100, message = "Bank name cannot exceed 100 characters")
    private String bankName;

    @NotBlank(message = "Agency code is required")
    @Size(max = 20, message = "Agency code must not exceed 20 characters")
    private String branchCode;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * custumized validation for total .
     */
    @AssertTrue(message = "Available balance cannot be grater than total balance")
    public boolean isAvailableBalanceValid() {
        if (balance == null || availableBalance == null) {
            return true; // deixa outras validações cuidarem dos nulls
        }
        return availableBalance.compareTo(balance) <= 0;
    }
}
