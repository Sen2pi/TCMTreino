package com.treasury.kpstreasury.models.dto;

import com.treasury.kpstreasury.enums.CollateralStatus;
import com.treasury.kpstreasury.enums.CollateralType;
import com.treasury.kpstreasury.enums.Rating;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CollateralDtoTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidCollateralDto() {
        CollateralDto dto = new CollateralDto();
        dto.setCollateralType(CollateralType.GOVERNMENT_BOND);
        dto.setDescription("Valid Government Bond");
        dto.setMarketValue(new BigDecimal("100000.00"));
        dto.setHaircut(new BigDecimal("0.0500"));
        dto.setCurrency("EUR");
        dto.setRating(Rating.AAA);
        dto.setMaturityDate(LocalDate.now().plusYears(1));
        dto.setStatus(CollateralStatus.ELIGIBLE);
        dto.setCounterparty("Valid Counterparty");
        dto.setLocation("Valid Location");

        Set<ConstraintViolation<CollateralDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testCollateralTypeRequired() {
        CollateralDto dto = createValidCollateralDto();
        dto.setCollateralType(null);

        Set<ConstraintViolation<CollateralDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Collateral type is required", violations.iterator().next().getMessage());
    }

    @Test
    void testDescriptionRequired() {
        CollateralDto dto = createValidCollateralDto();
        dto.setDescription(null);

        Set<ConstraintViolation<CollateralDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Description is required", violations.iterator().next().getMessage());
    }

    @Test
    void testDescriptionBlank() {
        CollateralDto dto = createValidCollateralDto();
        dto.setDescription("   ");

        Set<ConstraintViolation<CollateralDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Description is required", violations.iterator().next().getMessage());
    }

    @Test
    void testDescriptionTooLong() {
        CollateralDto dto = createValidCollateralDto();
        dto.setDescription("a".repeat(256));

        Set<ConstraintViolation<CollateralDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Description cannot have more than 255 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testMarketValueRequired() {
        CollateralDto dto = createValidCollateralDto();
        dto.setMarketValue(null);

        Set<ConstraintViolation<CollateralDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Market value is required", violations.iterator().next().getMessage());
    }

    @Test
    void testMarketValueZero() {
        CollateralDto dto = createValidCollateralDto();
        dto.setMarketValue(BigDecimal.ZERO);

        Set<ConstraintViolation<CollateralDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Market value must be greater than zero", violations.iterator().next().getMessage());
    }

    @Test
    void testHaircutRequired() {
        CollateralDto dto = createValidCollateralDto();
        dto.setHaircut(null);

        Set<ConstraintViolation<CollateralDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Haircut is required", violations.iterator().next().getMessage());
    }

    @Test
    void testHaircutNegative() {
        CollateralDto dto = createValidCollateralDto();
        dto.setHaircut(new BigDecimal("-0.1"));

        Set<ConstraintViolation<CollateralDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Haircut must be greater than or equal to zero", violations.iterator().next().getMessage());
    }

    @Test
    void testHaircutTooHigh() {
        CollateralDto dto = createValidCollateralDto();
        dto.setHaircut(new BigDecimal("1.1"));

        Set<ConstraintViolation<CollateralDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Haircut must be less than or equal to 1.0 (100%)", violations.iterator().next().getMessage());
    }

    @Test
    void testCurrencyRequired() {
        CollateralDto dto = createValidCollateralDto();
        dto.setCurrency(null);

        Set<ConstraintViolation<CollateralDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Currency is required", violations.iterator().next().getMessage());
    }

    @Test
    void testCurrencyInvalidLength() {
        CollateralDto dto = createValidCollateralDto();
        dto.setCurrency("EU");

        Set<ConstraintViolation<CollateralDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Currency must have 3 characters (ISO 4217)", violations.iterator().next().getMessage());
    }

    @Test
    void testRatingRequired() {
        CollateralDto dto = createValidCollateralDto();
        dto.setRating(null);

        Set<ConstraintViolation<CollateralDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Rating is required", violations.iterator().next().getMessage());
    }

    @Test
    void testMaturityDateInPast() {
        CollateralDto dto = createValidCollateralDto();
        dto.setMaturityDate(LocalDate.now().minusDays(1));

        Set<ConstraintViolation<CollateralDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Maturity date must be in the future", violations.iterator().next().getMessage());
    }

    @Test
    void testStatusRequired() {
        CollateralDto dto = createValidCollateralDto();
        dto.setStatus(null);

        Set<ConstraintViolation<CollateralDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Status is required", violations.iterator().next().getMessage());
    }

    @Test
    void testCounterpartyRequired() {
        CollateralDto dto = createValidCollateralDto();
        dto.setCounterparty(null);

        Set<ConstraintViolation<CollateralDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Counterparty is required", violations.iterator().next().getMessage());
    }

    @Test
    void testLocationRequired() {
        CollateralDto dto = createValidCollateralDto();
        dto.setLocation(null);

        Set<ConstraintViolation<CollateralDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Location is required", violations.iterator().next().getMessage());
    }

    private CollateralDto createValidCollateralDto() {
        CollateralDto dto = new CollateralDto();
        dto.setCollateralType(CollateralType.GOVERNMENT_BOND);
        dto.setDescription("Valid Description");
        dto.setMarketValue(new BigDecimal("100000.00"));
        dto.setHaircut(new BigDecimal("0.0500"));
        dto.setCurrency("EUR");
        dto.setRating(Rating.AAA);
        dto.setMaturityDate(LocalDate.now().plusYears(1));
        dto.setStatus(CollateralStatus.ELIGIBLE);
        dto.setCounterparty("Valid Counterparty");
        dto.setLocation("Valid Location");
        return dto;
    }
}