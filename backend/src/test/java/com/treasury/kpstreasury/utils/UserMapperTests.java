package com.treasury.kpstreasury.utils;

import com.treasury.kpstreasury.enums.Role;
import com.treasury.kpstreasury.models.dto.CreateUserDto;
import com.treasury.kpstreasury.models.dto.UserDto;
import com.treasury.kpstreasury.models.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTests {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void testToDtoWithValidEntity() {
        UserEntity entity = createValidUserEntity();
        
        UserDto dto = userMapper.toDto(entity);
        
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getUsername(), dto.getUsername());
        assertEquals(entity.getEmail(), dto.getEmail());
        assertEquals(entity.getFirstName(), dto.getFirstName());
        assertEquals(entity.getLastName(), dto.getLastName());
        assertEquals(entity.getRole(), dto.getRole());
        assertEquals(entity.isEnabled(), dto.isEnabled());
        assertEquals(entity.getCreatedAt(), dto.getCreatedAt());
    }

    @Test
    void testToDtoWithNullEntity() {
        UserDto dto = userMapper.toDto(null);
        assertNull(dto);
    }

    @Test
    void testToEntityWithValidCreateDto() {
        CreateUserDto createDto = createValidCreateUserDto();
        
        UserEntity entity = userMapper.toEntity(createDto);
        
        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals(createDto.getUsername(), entity.getUsername());
        assertEquals(createDto.getPassword(), entity.getPassword());
        assertEquals(createDto.getEmail(), entity.getEmail());
        assertEquals(createDto.getFirstName(), entity.getFirstName());
        assertEquals(createDto.getLastName(), entity.getLastName());
        assertEquals(createDto.getRole(), entity.getRole());
        assertTrue(entity.isEnabled());
        assertNull(entity.getCreatedAt());
    }

    @Test
    void testToEntityWithNullCreateDto() {
        UserEntity entity = userMapper.toEntity(null);
        assertNull(entity);
    }

    @Test
    void testUpdateEntityWithValidDto() {
        UserEntity existingEntity = createValidUserEntity();
        String originalUsername = existingEntity.getUsername();
        String originalPassword = existingEntity.getPassword();
        LocalDateTime originalCreatedAt = existingEntity.getCreatedAt();
        
        UserDto updateDto = new UserDto();
        updateDto.setEmail("updated@email.com");
        updateDto.setFirstName("UpdatedFirst");
        updateDto.setLastName("UpdatedLast");
        updateDto.setRole(Role.ADMIN);
        updateDto.setEnabled(false);
        
        UserEntity updatedEntity = userMapper.updateEntity(existingEntity, updateDto);
        
        assertSame(existingEntity, updatedEntity);
        assertEquals(originalUsername, updatedEntity.getUsername());
        assertEquals(originalPassword, updatedEntity.getPassword());
        assertEquals(originalCreatedAt, updatedEntity.getCreatedAt());
        assertEquals(updateDto.getEmail(), updatedEntity.getEmail());
        assertEquals(updateDto.getFirstName(), updatedEntity.getFirstName());
        assertEquals(updateDto.getLastName(), updatedEntity.getLastName());
        assertEquals(updateDto.getRole(), updatedEntity.getRole());
        assertEquals(updateDto.isEnabled(), updatedEntity.isEnabled());
    }

    @Test
    void testUpdateEntityWithNullDto() {
        UserEntity existingEntity = createValidUserEntity();
        UserEntity originalEntity = cloneUserEntity(existingEntity);
        
        UserEntity result = userMapper.updateEntity(existingEntity, null);
        
        assertSame(existingEntity, result);
        assertEquals(originalEntity.getUsername(), result.getUsername());
        assertEquals(originalEntity.getEmail(), result.getEmail());
        assertEquals(originalEntity.getFirstName(), result.getFirstName());
        assertEquals(originalEntity.getLastName(), result.getLastName());
        assertEquals(originalEntity.getRole(), result.getRole());
        assertEquals(originalEntity.isEnabled(), result.isEnabled());
    }

    private UserEntity createValidUserEntity() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setUsername("testuser");
        entity.setPassword("encodedPassword");
        entity.setEmail("test@email.com");
        entity.setFirstName("Test");
        entity.setLastName("User");
        entity.setRole(Role.USER);
        entity.setEnabled(true);
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    private CreateUserDto createValidCreateUserDto() {
        CreateUserDto dto = new CreateUserDto();
        dto.setUsername("newuser");
        dto.setPassword("plainPassword");
        dto.setEmail("new@email.com");
        dto.setFirstName("New");
        dto.setLastName("User");
        dto.setRole(Role.TREASURY);
        return dto;
    }

    private UserEntity cloneUserEntity(UserEntity original) {
        UserEntity clone = new UserEntity();
        clone.setId(original.getId());
        clone.setUsername(original.getUsername());
        clone.setPassword(original.getPassword());
        clone.setEmail(original.getEmail());
        clone.setFirstName(original.getFirstName());
        clone.setLastName(original.getLastName());
        clone.setRole(original.getRole());
        clone.setEnabled(original.isEnabled());
        clone.setCreatedAt(original.getCreatedAt());
        return clone;
    }
}