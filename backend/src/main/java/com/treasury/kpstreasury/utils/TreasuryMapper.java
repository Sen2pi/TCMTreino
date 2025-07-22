package com.treasury.kpstreasury.utils;

import com.treasury.kpstreasury.models.dto.TreasuryDto;
import com.treasury.kpstreasury.models.dto.TreasurySummaryDto;
import com.treasury.kpstreasury.models.entity.TreasuryEntity;
import org.springframework.stereotype.Component;

@Component
public class TreasuryMapper {

    public TreasuryDto toDto(TreasuryEntity treasury) {
        if (treasury == null) {
            return null;
        }

        TreasuryDto dto = new TreasuryDto();
        dto.setId(treasury.getId());
        dto.setAccountNumber(treasury.getAccountNumber());
        dto.setCurrency(treasury.getCurrency());
        dto.setBalance(treasury.getBalance());
        dto.setAvailableBalance(treasury.getAvailableBalance());
        dto.setAccountType(treasury.getAccountType());
        dto.setStatus(treasury.getStatus());
        dto.setBankName(treasury.getBankName());
        dto.setBranchCode(treasury.getBranchCode());
        dto.setCreatedAt(treasury.getCreatedAt());
        dto.setUpdatedAt(treasury.getUpdatedAt());
        return dto;
    }

    public TreasuryEntity toEntity(TreasuryDto dto) {
        if (dto == null) {
            return null;
        }

        TreasuryEntity treasury = new TreasuryEntity();
        treasury.setAccountNumber(dto.getAccountNumber());
        treasury.setCurrency(dto.getCurrency());
        treasury.setBalance(dto.getBalance());
        treasury.setAvailableBalance(dto.getAvailableBalance());
        treasury.setAccountType(dto.getAccountType());
        treasury.setStatus(dto.getStatus());
        treasury.setBankName(dto.getBankName());
        treasury.setBranchCode(dto.getBranchCode());
        return treasury;
    }

    public TreasuryEntity updateEntity(TreasuryEntity existingTreasury, TreasuryDto dto) {
        if (dto == null) {
            return existingTreasury;
        }

        existingTreasury.setBalance(dto.getBalance());
        existingTreasury.setAvailableBalance(dto.getAvailableBalance());
        existingTreasury.setStatus(dto.getStatus());
        existingTreasury.setBankName(dto.getBankName());
        existingTreasury.setBranchCode(dto.getBranchCode());
        return existingTreasury;
    }
}