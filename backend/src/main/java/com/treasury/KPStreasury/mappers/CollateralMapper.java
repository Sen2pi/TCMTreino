package com.treasury.kpstreasury.mappers;

import com.treasury.kpstreasury.models.dto.CollateralDto;
import com.treasury.kpstreasury.models.entity.Collateral;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CollateralMapper {
    
    CollateralDto toDto(Collateral entity);
    
    Collateral toEntity(CollateralDto dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(CollateralDto dto, @MappingTarget Collateral entity);
}