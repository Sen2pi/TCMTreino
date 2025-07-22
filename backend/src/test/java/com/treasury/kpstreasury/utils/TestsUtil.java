package com.treasury.kpstreasury.utils;

import com.treasury.kpstreasury.enums.*;
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
    public TreasuryEntity createTreasuryEntityB() {
        TreasuryEntity treasury = TreasuryEntity.builder()
                .accountNumber("ACC-002")
                .currency("USD")
                .balance(new BigDecimal("750000.00"))
                .availableBalance(new BigDecimal("650000.00"))
                .accountType(AccountType.SAVINGS)
                .status(AccountStatus.ACTIVE)
                .bankName("Caixa Geral de Depósitos")
                .branchCode("CGD-PT002")
                .build();

        return treasury;
    }

    public TreasuryEntity createTreasuryEntityC() {
        TreasuryEntity treasury = TreasuryEntity.builder()
                .accountNumber("ACC-003")
                .currency("GBP")
                .balance(new BigDecimal("2500000.00"))
                .availableBalance(new BigDecimal("2000000.00"))
                .accountType(AccountType.DEPOSIT)
                .status(AccountStatus.ACTIVE)
                .bankName("Banco Santander Totta")
                .branchCode("BST-OPO003")
                .build();

        return treasury;
    }

    public TreasuryEntity createTreasuryEntityD() {
        TreasuryEntity treasury = TreasuryEntity.builder()
                .accountNumber("ACC-004")
                .currency("EUR")
                .balance(new BigDecimal("500000.00"))
                .availableBalance(new BigDecimal("300000.00"))
                .accountType(AccountType.CHECKING)
                .status(AccountStatus.SUSPENDED)
                .bankName("Novo Banco")
                .branchCode("NB-AVR004")
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
    public CollateralEntity createCollateralEntityB() {
        CollateralEntity collateral = CollateralEntity.builder()
                .collateralType(CollateralType.CORPORATE_BOND)
                .description("EDP Corporate Bond 2026")
                .marketValue(new BigDecimal("300000.00"))
                .haircut(new BigDecimal("0.1000"))
                .currency("EUR")
                .rating(Rating.BBB)
                .maturityDate(LocalDate.of(2026, 6, 15))
                .status(CollateralStatus.ELIGIBLE)
                .counterparty("EDP - Energias de Portugal")
                .location("Interbolsa")
                .build();

        return collateral;
    }

    public CollateralEntity createCollateralEntityC() {
        CollateralEntity collateral = CollateralEntity.builder()
                .collateralType(CollateralType.EQUITY)
                .description("Jerónimo Martins Shares")
                .marketValue(new BigDecimal("150000.00"))
                .haircut(new BigDecimal("0.2000"))
                .currency("EUR")
                .rating(Rating.A)
                .maturityDate(null)
                .status(CollateralStatus.PLEDGED)
                .counterparty("Jerónimo Martins SGPS")
                .location("Euronext Lisbon")
                .build();

        return collateral;
    }

    public CollateralEntity createCollateralEntityD() {
        CollateralEntity collateral = CollateralEntity.builder()
                .collateralType(CollateralType.REAL_STATE)
                .description("Commercial Property Porto")
                .marketValue(new BigDecimal("800000.00"))
                .haircut(new BigDecimal("0.3000"))
                .currency("EUR")
                .rating(Rating.AA)
                .maturityDate(LocalDate.of(2030, 3, 20))
                .status(CollateralStatus.INELIGIBLE)
                .counterparty("Sonae Sierra")
                .location("Porto, Portugal")
                .build();

        return collateral;
    }

    public UserEntity createUserEntityA() {
        UserEntity user = UserEntity.builder()
                .username("treasury.user")
                .password("encoded_password")
                .email("johndoe@gmail.com")
                .firstName("John")
                .lastName("Doe")
                .role(Role.TREASURY)
                .enabled(true)
                .build();

        return user;
    }
    public UserEntity createUserEntityB() {
        UserEntity user = UserEntity.builder()
                .username("mariasilva")
                .password("encoded_password_2")
                .email("maria.silva@company.com")
                .firstName("Maria")
                .lastName("Silva")
                .role(Role.COLLATERAL)
                .enabled(true)
                .build();

        return user;
    }

    public UserEntity createUserEntityC() {
        UserEntity user = UserEntity.builder()
                .username("admin.user")
                .password("encoded_password_3")
                .email("pedro.santos@finance.pt")
                .firstName("Pedro")
                .lastName("Santos")
                .role(Role.ADMIN)
                .enabled(false)
                .build();

        return user;
    }

    public UserEntity createUserEntityD() {
        UserEntity user = UserEntity.builder()
                .username("regular.user")
                .password("encoded_password_4")
                .email("ana.ferreira@treasury.com")
                .firstName("Ana")
                .lastName("Ferreira")
                .role(Role.USER)
                .enabled(true)
                .build();

        return user;
    }




}
