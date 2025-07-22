package com.treasury.kpstreasury.models.dto;

import com.treasury.kpstreasury.enums.Role;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CreateUserDtoTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidCreateUserDto() {
        CreateUserDto dto = createValidCreateUserDto();

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testUsernameRequired() {
        CreateUserDto dto = createValidCreateUserDto();
        dto.setUsername(null);

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Username is Required", violations.iterator().next().getMessage());
    }

    @Test
    void testUsernameBlank() {
        CreateUserDto dto = createValidCreateUserDto();
        dto.setUsername("   ");

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Username is Required", violations.iterator().next().getMessage());
    }

    @Test
    void testUsernameTooShort() {
        CreateUserDto dto = createValidCreateUserDto();
        dto.setUsername("ab");

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Username should have between 3 and 50 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testPasswordRequired() {
        CreateUserDto dto = createValidCreateUserDto();
        dto.setPassword(null);

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Password is Required", violations.iterator().next().getMessage());
    }

    @Test
    void testPasswordTooShort() {
        CreateUserDto dto = createValidCreateUserDto();
        dto.setPassword("1234567");

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Password should have at least 8 charcters, 1 Major and one Symbol", violations.iterator().next().getMessage());
    }

    @Test
    void testEmailRequired() {
        CreateUserDto dto = createValidCreateUserDto();
        dto.setEmail(null);

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Email is required", violations.iterator().next().getMessage());
    }

    @Test
    void testEmailInvalid() {
        CreateUserDto dto = createValidCreateUserDto();
        dto.setEmail("invalid-email");

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Email deve ser válido", violations.iterator().next().getMessage());
    }

    @Test
    void testFirstNameRequired() {
        CreateUserDto dto = createValidCreateUserDto();
        dto.setFirstName(null);

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("First Name is Required", violations.iterator().next().getMessage());
    }

    @Test
    void testLastNameRequired() {
        CreateUserDto dto = createValidCreateUserDto();
        dto.setLastName(null);

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Last Name is Required", violations.iterator().next().getMessage());
    }

    @Test
    void testRoleRequired() {
        CreateUserDto dto = createValidCreateUserDto();
        dto.setRole(null);

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Role is Required", violations.iterator().next().getMessage());
    }

    private CreateUserDto createValidCreateUserDto() {
        CreateUserDto dto = new CreateUserDto();
        dto.setUsername("validuser");
        dto.setPassword("validPassword123!");
        dto.setEmail("valid@email.com");
        dto.setFirstName("Valid");
        dto.setLastName("User");
        dto.setRole(Role.USER);
        return dto;
    }
}