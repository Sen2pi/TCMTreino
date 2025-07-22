package com.treasury.kpstreasury.utils;

import com.treasury.kpstreasury.enums.CollateralStatus;
import com.treasury.kpstreasury.enums.CollateralType;
import com.treasury.kpstreasury.enums.Rating;
import com.treasury.kpstreasury.models.dto.CollateralDto;
import com.treasury.kpstreasury.models.entity.CollateralEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CollateralMapperTests {

    private CollateralMapper collateralMapper;

    @BeforeEach
    void setUp() {
        collateralMapper = new CollateralMapper();
    }

    @Test
    void testToDtoWithValidEntity() {
        CollateralEntity entity = createValidCollateralEntity();
        
        CollateralDto dto = collateralMapper.toDto(entity);
        
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getCollateralType(), dto.getCollateralType());
        assertEquals(entity.getDescription(), dto.getDescription());
        assertEquals(entity.getMarketValue(), dto.getMarketValue());
        assertEquals(entity.getHaircut(), dto.getHaircut());
        assertEquals(entity.getEligibleValue(), dto.getEligibleValue());
        assertEquals(entity.getCurrency(), dto.getCurrency());
        assertEquals(entity.getRating(), dto.getRating());
        assertEquals(entity.getMaturityDate(), dto.getMaturityDate());
        assertEquals(entity.getStatus(), dto.getStatus());
        assertEquals(entity.getCounterparty(), dto.getCounterparty());
        assertEquals(entity.getLocation(), dto.getLocation());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), dto.getUpdatedAt());
    }

    @Test
    void testToDtoWithNullEntity() {
        CollateralDto dto = collateralMapper.toDto(null);
        assertNull(dto);
    }

    @Test
    void testToEntityWithValidDto() {
        CollateralDto dto = createValidCollateralDto();
        
        CollateralEntity entity = collateralMapper.toEntity(dto);
        
        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals(dto.getCollateralType(), entity.getCollateralType());
        assertEquals(dto.getDescription(), entity.getDescription());
        assertEquals(dto.getMarketValue(), entity.getMarketValue());
        assertEquals(dto.getHaircut(), entity.getHaircut());
        assertEquals(dto.getCurrency(), entity.getCurrency());
        assertEquals(dto.getRating(), entity.getRating());
        assertEquals(dto.getMaturityDate(), entity.getMaturityDate());
        assertEquals(dto.getStatus(), entity.getStatus());
        assertEquals(dto.getCounterparty(), entity.getCounterparty());
        assertEquals(dto.getLocation(), entity.getLocation());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getUpdatedAt());
    }

    @Test
    void testToEntityWithNullDto() {
        CollateralEntity entity = collateralMapper.toEntity(null);
        assertNull(entity);
    }

    @Test
    void testUpdateEntityWithValidDto() {
        CollateralEntity existingEntity = createValidCollateralEntity();
        Long originalId = existingEntity.getId();
        LocalDateTime originalCreatedAt = existingEntity.getCreatedAt();
        LocalDateTime originalUpdatedAt = existingEntity.getUpdatedAt();
        
        CollateralDto updateDto = new CollateralDto();
        updateDto.setCollateralType(CollateralType.CORPORATE_BOND);
        updateDto.setDescription("Updated Description");
        updateDto.setMarketValue(new BigDecimal("200000.00"));
        updateDto.setHaircut(new BigDecimal("0.1500"));
        updateDto.setCurrency("USD");
        updateDto.setRating(Rating.BBB);
        updateDto.setMaturityDate(LocalDate.of(2026, 6, 15));
        updateDto.setStatus(CollateralStatus.PLEDGED);
        updateDto.setCounterparty("Updated Counterparty");
        updateDto.setLocation("Updated Location");
        
        CollateralEntity updatedEntity = collateralMapper.updateEntity(existingEntity, updateDto);
        
        assertSame(existingEntity, updatedEntity);
        assertEquals(originalId, updatedEntity.getId());
        assertEquals(originalCreatedAt, updatedEntity.getCreatedAt());
        assertEquals(originalUpdatedAt, updatedEntity.getUpdatedAt());
        assertEquals(updateDto.getCollateralType(), updatedEntity.getCollateralType());
        assertEquals(updateDto.getDescription(), updatedEntity.getDescription());
        assertEquals(updateDto.getMarketValue(), updatedEntity.getMarketValue());
        assertEquals(updateDto.getHaircut(), updatedEntity.getHaircut());
        assertEquals(updateDto.getCurrency(), updatedEntity.getCurrency());
        assertEquals(updateDto.getRating(), updatedEntity.getRating());
        assertEquals(updateDto.getMaturityDate(), updatedEntity.getMaturityDate());
        assertEquals(updateDto.getStatus(), updatedEntity.getStatus());
        assertEquals(updateDto.getCounterparty(), updatedEntity.getCounterparty());
        assertEquals(updateDto.getLocation(), updatedEntity.getLocation());
    }

    @Test
    void testUpdateEntityWithNullDto() {
        CollateralEntity existingEntity = createValidCollateralEntity();
        CollateralEntity originalEntity = cloneCollateralEntity(existingEntity);
        
        CollateralEntity result = collateralMapper.updateEntity(existingEntity, null);
        
        assertSame(existingEntity, result);
        assertEquals(originalEntity.getCollateralType(), result.getCollateralType());
        assertEquals(originalEntity.getDescription(), result.getDescription());
        assertEquals(originalEntity.getMarketValue(), result.getMarketValue());
        assertEquals(originalEntity.getHaircut(), result.getHaircut());
        assertEquals(originalEntity.getCurrency(), result.getCurrency());
        assertEquals(originalEntity.getRating(), result.getRating());
        assertEquals(originalEntity.getMaturityDate(), result.getMaturityDate());
        assertEquals(originalEntity.getStatus(), result.getStatus());
        assertEquals(originalEntity.getCounterparty(), result.getCounterparty());
        assertEquals(originalEntity.getLocation(), result.getLocation());
    }

    private CollateralEntity createValidCollateralEntity() {
        CollateralEntity entity = new CollateralEntity();
        entity.setId(1L);
        entity.setCollateralType(CollateralType.GOVERNMENT_BOND);
        entity.setDescription("Test Government Bond");
        entity.setMarketValue(new BigDecimal("100000.00"));
        entity.setHaircut(new BigDecimal("0.0500"));
        entity.setEligibleValue(new BigDecimal("95000.00"));
        entity.setCurrency("EUR");
        entity.setRating(Rating.AAA);
        entity.setMaturityDate(LocalDate.of(2025, 12, 31));
        entity.setStatus(CollateralStatus.ELIGIBLE);
        entity.setCounterparty("Test Counterparty");
        entity.setLocation("Test Location");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    private CollateralDto createValidCollateralDto() {
        CollateralDto dto = new CollateralDto();
        dto.setCollateralType(CollateralType.GOVERNMENT_BOND);
        dto.setDescription("Test Government Bond");
        dto.setMarketValue(new BigDecimal("100000.00"));
        dto.setHaircut(new BigDecimal("0.0500"));
        dto.setCurrency("EUR");
        dto.setRating(Rating.AAA);
        dto.setMaturityDate(LocalDate.of(2025, 12, 31));
        dto.setStatus(CollateralStatus.ELIGIBLE);
        dto.setCounterparty("Test Counterparty");
        dto.setLocation("Test Location");
        return dto;
    }

    private CollateralEntity cloneCollateralEntity(CollateralEntity original) {
        CollateralEntity clone = new CollateralEntity();
        clone.setId(original.getId());
        clone.setCollateralType(original.getCollateralType());
        clone.setDescription(original.getDescription());
        clone.setMarketValue(original.getMarketValue());
        clone.setHaircut(original.getHaircut());
        clone.setEligibleValue(original.getEligibleValue());
        clone.setCurrency(original.getCurrency());
        clone.setRating(original.getRating());
        clone.setMaturityDate(original.getMaturityDate());
        clone.setStatus(original.getStatus());
        clone.setCounterparty(original.getCounterparty());
        clone.setLocation(original.getLocation());
        clone.setCreatedAt(original.getCreatedAt());
        clone.setUpdatedAt(original.getUpdatedAt());
        return clone;
    }
}