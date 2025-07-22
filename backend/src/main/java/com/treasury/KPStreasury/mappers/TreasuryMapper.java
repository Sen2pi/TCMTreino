package com.treasury.kpstreasury.mappers;

import com.treasury.kpstreasury.models.dto.TreasuryDto;
import com.treasury.kpstreasury.models.entity.TreasuryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TreasuryMapper {
    
    TreasuryDto toDto(TreasuryEntity entity);
    
    TreasuryEntity toEntity(TreasuryDto dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(TreasuryDto dto, @MappingTarget TreasuryEntity entity);
}