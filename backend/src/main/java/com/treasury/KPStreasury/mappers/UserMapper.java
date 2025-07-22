package com.treasury.kpstreasury.mappers;

import com.treasury.kpstreasury.models.dto.UserDto;
import com.treasury.kpstreasury.models.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    
    @Mapping(target = "password", ignore = true)
    UserDto toDto(User entity);
    
    @Mapping(target = "password", ignore = true)
    User toEntity(UserDto dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(UserDto dto, @MappingTarget User entity);
}