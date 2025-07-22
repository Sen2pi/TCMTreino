package com.treasury.kpstreasury.models.dto;

import com.treasury.kpstreasury.enums.AccountStatus;
import com.treasury.kpstreasury.enums.AccountType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TreasuryDtoTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidTreasuryDto() {
        TreasuryDto dto = createValidTreasuryDto();

        Set<ConstraintViolation<TreasuryDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testAccountNumberRequired() {
        TreasuryDto dto = createValidTreasuryDto();
        dto.setAccountNumber(null);

        Set<ConstraintViolation<TreasuryDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Account Number is Required", violations.iterator().next().getMessage());
    }

    @Test
    void testAccountNumberBlank() {
        TreasuryDto dto = createValidTreasuryDto();
        dto.setAccountNumber("   ");

        Set<ConstraintViolation<TreasuryDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Account Number is Required", violations.iterator().next().getMessage());
    }

    @Test
    void testAccountNumberTooLong() {
        TreasuryDto dto = createValidTreasuryDto();
        dto.setAccountNumber("a".repeat(51));

        Set<ConstraintViolation<TreasuryDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Account Number should have at max 50 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testCurrencyRequired() {
        TreasuryDto dto = createValidTreasuryDto();
        dto.setCurrency(null);

        Set<ConstraintViolation<TreasuryDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Currency is Required", violations.iterator().next().getMessage());
    }

    @Test
    void testCurrencyInvalidLength() {
        TreasuryDto dto = createValidTreasuryDto();
        dto.setCurrency("EU");

        Set<ConstraintViolation<TreasuryDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Currency must be composed of 3 charcters only (ISO 4217)", violations.iterator().next().getMessage());
    }

    @Test
    void testBalanceRequired() {
        TreasuryDto dto = createValidTreasuryDto();
        dto.setBalance(null);

        Set<ConstraintViolation<TreasuryDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("balance is required", violations.iterator().next().getMessage());
    }

    @Test
    void testBalanceNegative() {
        TreasuryDto dto = new TreasuryDto();
        dto.setAccountNumber("ACC-001");
        dto.setCurrency("EUR");
        dto.setBalance(new BigDecimal("-100.00"));
        dto.setAvailableBalance(new BigDecimal("-50.00")); // Keep valid relationship
        dto.setAccountType(AccountType.CHECKING);
        dto.setStatus(AccountStatus.ACTIVE);
        dto.setBankName("Valid Bank");
        dto.setBranchCode("BR001");

        Set<ConstraintViolation<TreasuryDto>> violations = validator.validate(dto);
        assertEquals(3, violations.size()); // Both balance and availableBalance negative
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Balance must be greater orequal to 0")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Available balance must be greater or equal to 0")));
    }

    @Test
    void testAvailableBalanceRequired() {
        TreasuryDto dto = createValidTreasuryDto();
        dto.setAvailableBalance(null);

        Set<ConstraintViolation<TreasuryDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("available balance is required", violations.iterator().next().getMessage());
    }

    @Test
    void testAvailableBalanceGreaterThanBalance() {
        TreasuryDto dto = createValidTreasuryDto();
        dto.setBalance(new BigDecimal("1000.00"));
        dto.setAvailableBalance(new BigDecimal("1500.00"));

        Set<ConstraintViolation<TreasuryDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Available balance cannot be grater than total balance", violations.iterator().next().getMessage());
    }

    @Test
    void testAccountTypeRequired() {
        TreasuryDto dto = createValidTreasuryDto();
        dto.setAccountType(null);

        Set<ConstraintViolation<TreasuryDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Account type  is required", violations.iterator().next().getMessage());
    }

    @Test
    void testStatusRequired() {
        TreasuryDto dto = createValidTreasuryDto();
        dto.setStatus(null);

        Set<ConstraintViolation<TreasuryDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Account Status is required", violations.iterator().next().getMessage());
    }

    @Test
    void testBankNameRequired() {
        TreasuryDto dto = createValidTreasuryDto();
        dto.setBankName(null);

        Set<ConstraintViolation<TreasuryDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Bank Name  is required", violations.iterator().next().getMessage());
    }

    @Test
    void testBankNameTooLong() {
        TreasuryDto dto = createValidTreasuryDto();
        dto.setBankName("a".repeat(101));

        Set<ConstraintViolation<TreasuryDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Bank name cannot exceed 100 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testBranchCodeRequired() {
        TreasuryDto dto = createValidTreasuryDto();
        dto.setBranchCode(null);

        Set<ConstraintViolation<TreasuryDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Agency code is required", violations.iterator().next().getMessage());
    }

    @Test
    void testBranchCodeTooLong() {
        TreasuryDto dto = createValidTreasuryDto();
        dto.setBranchCode("a".repeat(21));

        Set<ConstraintViolation<TreasuryDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Agency code must not exceed 20 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testAvailableBalanceValidationPasses() {
        TreasuryDto dto = createValidTreasuryDto();
        dto.setBalance(new BigDecimal("1000.00"));
        dto.setAvailableBalance(new BigDecimal("800.00"));

        Set<ConstraintViolation<TreasuryDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    private TreasuryDto createValidTreasuryDto() {
        TreasuryDto dto = new TreasuryDto();
        dto.setAccountNumber("ACC-001");
        dto.setCurrency("EUR");
        dto.setBalance(new BigDecimal("1000000.00"));
        dto.setAvailableBalance(new BigDecimal("800000.00"));
        dto.setAccountType(AccountType.CHECKING);
        dto.setStatus(AccountStatus.ACTIVE);
        dto.setBankName("Valid Bank");
        dto.setBranchCode("BR001");
        return dto;
    }
}