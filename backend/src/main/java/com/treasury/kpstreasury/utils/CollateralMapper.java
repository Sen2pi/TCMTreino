package com.treasury.kpstreasury.utils;

import com.treasury.kpstreasury.models.dto.CollateralDto;
import com.treasury.kpstreasury.models.entity.CollateralEntity;
import org.springframework.stereotype.Component;

@Component
public class CollateralMapper {

    public CollateralDto toDto(CollateralEntity collateral) {
        if (collateral == null) {
            return null;
        }

        CollateralDto dto = new CollateralDto();
        dto.setId(collateral.getId());
        dto.setCollateralType(collateral.getCollateralType());
        dto.setDescription(collateral.getDescription());
        dto.setMarketValue(collateral.getMarketValue());
        dto.setHaircut(collateral.getHaircut());
        dto.setEligibleValue(collateral.getEligibleValue());
        dto.setCurrency(collateral.getCurrency());
        dto.setRating(collateral.getRating());
        dto.setMaturityDate(collateral.getMaturityDate());
        dto.setStatus(collateral.getStatus());
        dto.setCounterparty(collateral.getCounterparty());
        dto.setLocation(collateral.getLocation());
        dto.setCreatedAt(collateral.getCreatedAt());
        dto.setUpdatedAt(collateral.getUpdatedAt());
        return dto;
    }

    public CollateralEntity toEntity(CollateralDto dto) {
        if (dto == null) {
            return null;
        }

        CollateralEntity collateral = new CollateralEntity();
        collateral.setCollateralType(dto.getCollateralType());
        collateral.setDescription(dto.getDescription());
        collateral.setMarketValue(dto.getMarketValue());
        collateral.setHaircut(dto.getHaircut());
        collateral.setCurrency(dto.getCurrency());
        collateral.setRating(dto.getRating());
        collateral.setMaturityDate(dto.getMaturityDate());
        collateral.setStatus(dto.getStatus());
        collateral.setCounterparty(dto.getCounterparty());
        collateral.setLocation(dto.getLocation());
        return collateral;
    }

    public CollateralEntity updateEntity(CollateralEntity existingCollateral, CollateralDto dto) {
        if (dto == null) {
            return existingCollateral;
        }

        existingCollateral.setCollateralType(dto.getCollateralType());
        existingCollateral.setDescription(dto.getDescription());
        existingCollateral.setMarketValue(dto.getMarketValue());
        existingCollateral.setHaircut(dto.getHaircut());
        existingCollateral.setCurrency(dto.getCurrency());
        existingCollateral.setRating(dto.getRating());
        existingCollateral.setMaturityDate(dto.getMaturityDate());
        existingCollateral.setStatus(dto.getStatus());
        existingCollateral.setCounterparty(dto.getCounterparty());
        existingCollateral.setLocation(dto.getLocation());
        return existingCollateral;
    }
}