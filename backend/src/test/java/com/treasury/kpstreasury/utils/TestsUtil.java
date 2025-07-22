package com.treasury.kpstreasury.utils;

import com.treasury.kpstreasury.models.entity.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class TestsUtil {

    public TreasuryEntity createTreasuryEntityA() {
        TreasuryEntity treasury = TreasuryEntity.builder()
                .accountNumber("ACC-001")
                .currency("EUR")
                .balance(new BigDecimal("1000000.00"))
                .availableBalance(new BigDecimal("800000.00"))
                .accountType(AccountType.CHECKING)
                .status(AccountStatus.ACTIVE)
                .bankName("Banco Comercial Português")
                .branchCode("BCP-LX001")
                .build();

        return treasury;
    }

    public CollateralEntity createCollateralEntityA() {
        CollateralEntity collateral = CollateralEntity.builder()
                .collateralType(CollateralType.GOVERNMENT_BOND)
                .description("Portuguese Government Bond 2025")
                .marketValue(new BigDecimal("500000.00"))
                .haircut(new BigDecimal("0.0500"))
                .currency("EUR")
                .rating(Rating.AAA)
                .maturityDate(LocalDate.of(2025, 12, 31))
                .status(CollateralStatus.ELIGIBLE)
                .counterparty("Portuguese Republic")
                .location("Euroclear")
                .build();

        return collateral;
    }

    public UserEntity createUserEntityA() {
        UserEntity user = UserEntity.builder()
                .username("johnDoe")
                .password("encoded_password")
                .email("johndoe@gmail.com")
                .firstName("John")
                .lastName("Doe")
                .role(Role.TREASURY)
                .enabled(true)
                .build();

        return user;
    }


}
