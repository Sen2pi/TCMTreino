package com.treasury.kpstreasury.utils;

import com.treasury.kpstreasury.enums.AccountStatus;
import com.treasury.kpstreasury.enums.AccountType;
import com.treasury.kpstreasury.models.dto.TreasuryDto;
import com.treasury.kpstreasury.models.entity.TreasuryEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TreasuryMapperTests {

    private TreasuryMapper treasuryMapper;

    @BeforeEach
    void setUp() {
        treasuryMapper = new TreasuryMapper();
    }

    @Test
    void testToDtoWithValidEntity() {
        TreasuryEntity entity = createValidTreasuryEntity();
        
        TreasuryDto dto = treasuryMapper.toDto(entity);
        
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getAccountNumber(), dto.getAccountNumber());
        assertEquals(entity.getCurrency(), dto.getCurrency());
        assertEquals(entity.getBalance(), dto.getBalance());
        assertEquals(entity.getAvailableBalance(), dto.getAvailableBalance());
        assertEquals(entity.getAccountType(), dto.getAccountType());
        assertEquals(entity.getStatus(), dto.getStatus());
        assertEquals(entity.getBankName(), dto.getBankName());
        assertEquals(entity.getBranchCode(), dto.getBranchCode());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), dto.getUpdatedAt());
    }

    @Test
    void testToDtoWithNullEntity() {
        TreasuryDto dto = treasuryMapper.toDto(null);
        assertNull(dto);
    }

    @Test
    void testToEntityWithValidDto() {
        TreasuryDto dto = createValidTreasuryDto();
        
        TreasuryEntity entity = treasuryMapper.toEntity(dto);
        
        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals(dto.getAccountNumber(), entity.getAccountNumber());
        assertEquals(dto.getCurrency(), entity.getCurrency());
        assertEquals(dto.getBalance(), entity.getBalance());
        assertEquals(dto.getAvailableBalance(), entity.getAvailableBalance());
        assertEquals(dto.getAccountType(), entity.getAccountType());
        assertEquals(dto.getStatus(), entity.getStatus());
        assertEquals(dto.getBankName(), entity.getBankName());
        assertEquals(dto.getBranchCode(), entity.getBranchCode());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getUpdatedAt());
    }

    @Test
    void testToEntityWithNullDto() {
        TreasuryEntity entity = treasuryMapper.toEntity(null);
        assertNull(entity);
    }

    @Test
    void testUpdateEntityWithValidDto() {
        TreasuryEntity existingEntity = createValidTreasuryEntity();
        Long originalId = existingEntity.getId();
        String originalAccountNumber = existingEntity.getAccountNumber();
        String originalCurrency = existingEntity.getCurrency();
        AccountType originalAccountType = existingEntity.getAccountType();
        LocalDateTime originalCreatedAt = existingEntity.getCreatedAt();
        LocalDateTime originalUpdatedAt = existingEntity.getUpdatedAt();
        
        TreasuryDto updateDto = new TreasuryDto();
        updateDto.setBalance(new BigDecimal("150000.00"));
        updateDto.setAvailableBalance(new BigDecimal("120000.00"));
        updateDto.setStatus(AccountStatus.SUSPENDED);
        updateDto.setBankName("Updated Bank");
        updateDto.setBranchCode("UPD001");
        
        TreasuryEntity updatedEntity = treasuryMapper.updateEntity(existingEntity, updateDto);
        
        assertSame(existingEntity, updatedEntity);
        assertEquals(originalId, updatedEntity.getId());
        assertEquals(originalAccountNumber, updatedEntity.getAccountNumber());
        assertEquals(originalCurrency, updatedEntity.getCurrency());
        assertEquals(originalAccountType, updatedEntity.getAccountType());
        assertEquals(originalCreatedAt, updatedEntity.getCreatedAt());
        assertEquals(originalUpdatedAt, updatedEntity.getUpdatedAt());
        assertEquals(updateDto.getBalance(), updatedEntity.getBalance());
        assertEquals(updateDto.getAvailableBalance(), updatedEntity.getAvailableBalance());
        assertEquals(updateDto.getStatus(), updatedEntity.getStatus());
        assertEquals(updateDto.getBankName(), updatedEntity.getBankName());
        assertEquals(updateDto.getBranchCode(), updatedEntity.getBranchCode());
    }

    @Test
    void testUpdateEntityWithNullDto() {
        TreasuryEntity existingEntity = createValidTreasuryEntity();
        TreasuryEntity originalEntity = cloneTreasuryEntity(existingEntity);
        
        TreasuryEntity result = treasuryMapper.updateEntity(existingEntity, null);
        
        assertSame(existingEntity, result);
        assertEquals(originalEntity.getAccountNumber(), result.getAccountNumber());
        assertEquals(originalEntity.getCurrency(), result.getCurrency());
        assertEquals(originalEntity.getBalance(), result.getBalance());
        assertEquals(originalEntity.getAvailableBalance(), result.getAvailableBalance());
        assertEquals(originalEntity.getAccountType(), result.getAccountType());
        assertEquals(originalEntity.getStatus(), result.getStatus());
        assertEquals(originalEntity.getBankName(), result.getBankName());
        assertEquals(originalEntity.getBranchCode(), result.getBranchCode());
    }

    private TreasuryEntity createValidTreasuryEntity() {
        TreasuryEntity entity = new TreasuryEntity();
        entity.setId(1L);
        entity.setAccountNumber("ACC-001");
        entity.setCurrency("EUR");
        entity.setBalance(new BigDecimal("100000.00"));
        entity.setAvailableBalance(new BigDecimal("80000.00"));
        entity.setAccountType(AccountType.CHECKING);
        entity.setStatus(AccountStatus.ACTIVE);
        entity.setBankName("Test Bank");
        entity.setBranchCode("TEST001");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    private TreasuryDto createValidTreasuryDto() {
        TreasuryDto dto = new TreasuryDto();
        dto.setAccountNumber("ACC-001");
        dto.setCurrency("EUR");
        dto.setBalance(new BigDecimal("100000.00"));
        dto.setAvailableBalance(new BigDecimal("80000.00"));
        dto.setAccountType(AccountType.CHECKING);
        dto.setStatus(AccountStatus.ACTIVE);
        dto.setBankName("Test Bank");
        dto.setBranchCode("TEST001");
        return dto;
    }

    private TreasuryEntity cloneTreasuryEntity(TreasuryEntity original) {
        TreasuryEntity clone = new TreasuryEntity();
        clone.setId(original.getId());
        clone.setAccountNumber(original.getAccountNumber());
        clone.setCurrency(original.getCurrency());
        clone.setBalance(original.getBalance());
        clone.setAvailableBalance(original.getAvailableBalance());
        clone.setAccountType(original.getAccountType());
        clone.setStatus(original.getStatus());
        clone.setBankName(original.getBankName());
        clone.setBranchCode(original.getBranchCode());
        clone.setCreatedAt(original.getCreatedAt());
        clone.setUpdatedAt(original.getUpdatedAt());
        return clone;
    }
}