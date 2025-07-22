package com.treasury.kpstreasury.models.dto;

import com.treasury.kpstreasury.enums.Role;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserDtoTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidUserDto() {
        UserDto dto = createValidUserDto();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testUsernameRequired() {
        UserDto dto = createValidUserDto();
        dto.setUsername(null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Username is mandatory!", violations.iterator().next().getMessage());
    }

    @Test
    void testUsernameBlank() {
        UserDto dto = createValidUserDto();
        dto.setUsername("   ");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Username is mandatory!", violations.iterator().next().getMessage());
    }

    @Test
    void testUsernameTooShort() {
        UserDto dto = createValidUserDto();
        dto.setUsername("ab");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Username must have between 3 and 50 caracters", violations.iterator().next().getMessage());
    }

    @Test
    void testUsernameTooLong() {
        UserDto dto = createValidUserDto();
        dto.setUsername("a".repeat(51));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Username must have between 3 and 50 caracters", violations.iterator().next().getMessage());
    }

    @Test
    void testEmailRequired() {
        UserDto dto = createValidUserDto();
        dto.setEmail(null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Email is mandatory!", violations.iterator().next().getMessage());
    }

    @Test
    void testEmailInvalid() {
        UserDto dto = createValidUserDto();
        dto.setEmail("invalid-email");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Email should be a valid email adress example: test@test.com", violations.iterator().next().getMessage());
    }

    @Test
    void testFirstNameRequired() {
        UserDto dto = createValidUserDto();
        dto.setFirstName(null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("First Name is mandatory!", violations.iterator().next().getMessage());
    }

    @Test
    void testFirstNameTooLong() {
        UserDto dto = createValidUserDto();
        dto.setFirstName("a".repeat(51));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("First name shouldn't exeed the margin of 50 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testLastNameRequired() {
        UserDto dto = createValidUserDto();
        dto.setLastName(null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("lAST Name is mandatory!", violations.iterator().next().getMessage());
    }

    @Test
    void testLastNameTooLong() {
        UserDto dto = createValidUserDto();
        dto.setLastName("a".repeat(51));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Last name shouldn't exeed the margin of 50 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testRoleRequired() {
        UserDto dto = createValidUserDto();
        dto.setRole(null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        assertEquals("Role is mandatory", violations.iterator().next().getMessage());
    }

    private UserDto createValidUserDto() {
        UserDto dto = new UserDto();
        dto.setUsername("validuser");
        dto.setEmail("valid@email.com");
        dto.setFirstName("Valid");
        dto.setLastName("User");
        dto.setRole(Role.USER);
        dto.setEnabled(true);
        return dto;
    }
}