package com.treasury.kpstreasury.utils;

import com.treasury.kpstreasury.models.dto.CreateUserDto;
import com.treasury.kpstreasury.models.dto.UserDto;
import com.treasury.kpstreasury.models.entity.UserEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre User entity e DTOs.
 * Centraliza a lógica de mapeamento para facilitar manutenção.
 */
@Component
public class UserMapper {

    public UserDto toDto(UserEntity user) {
        if (user == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        dto.setEnabled(user.isEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    public UserEntity toEntity(CreateUserDto createDto) {
        if (createDto == null) {
            return null;
        }

        UserEntity user = new UserEntity();
        user.setUsername(createDto.getUsername());
        user.setPassword(createDto.getPassword()); // será encoded no service
        user.setEmail(createDto.getEmail());
        user.setFirstName(createDto.getFirstName());
        user.setLastName(createDto.getLastName());
        user.setRole(createDto.getRole());
        user.setEnabled(true); // default
        return user;
    }

    public UserEntity updateEntity(UserEntity existingUser, UserDto dto) {
        if (dto == null) {
            return existingUser;
        }

        existingUser.setEmail(dto.getEmail());
        existingUser.setFirstName(dto.getFirstName());
        existingUser.setLastName(dto.getLastName());
        existingUser.setRole(dto.getRole());
        existingUser.setEnabled(dto.isEnabled());
        // username e password não são atualizados via este DTO
        return existingUser;
    }
}