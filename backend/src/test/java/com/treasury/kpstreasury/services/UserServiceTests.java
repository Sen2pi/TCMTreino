package com.treasury.kpstreasury.services;

import com.treasury.kpstreasury.enums.Role;
import com.treasury.kpstreasury.models.dto.CreateUserDto;
import com.treasury.kpstreasury.models.dto.UserDto;
import com.treasury.kpstreasury.models.entity.UserEntity;
import com.treasury.kpstreasury.repositories.UserRepository;
import com.treasury.kpstreasury.services.EventPublisher;
import com.treasury.kpstreasury.utils.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    private UserEntity userEntity;
    private UserDto userDto;
    private CreateUserDto createUserDto;

    @BeforeEach
    void setUp() {
        userEntity = UserEntity.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("encodedpassword")
                .role(Role.USER)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .role(Role.USER)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        createUserDto = CreateUserDto.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("password123")
                .role(Role.USER)
                .build();
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(userEntity));

        UserDetails result = userService.loadUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, 
                () -> userService.loadUserByUsername("nonexistent"));
        
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void createUser_ShouldCreateUser_WhenValidData() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userMapper.toEntity(createUserDto)).thenReturn(userEntity);
        when(passwordEncoder.encode("password123")).thenReturn("encodedpassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        UserDto result = userService.createUser(createUserDto);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).save(any(UserEntity.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void createUser_ShouldThrowException_WhenUsernameExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(createUserDto));

        assertTrue(exception.getMessage().contains("Username already exists"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(createUserDto));

        assertTrue(exception.getMessage().contains("Email already exists"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        Optional<UserDto> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_ShouldReturnEmpty_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<UserDto> result = userService.getUserById(1L);

        assertFalse(result.isPresent());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserByUsername_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        Optional<UserDto> result = userService.getUserByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        List<UserEntity> userEntities = Arrays.asList(userEntity);
        when(userRepository.findAll()).thenReturn(userEntities);
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        verify(userRepository).findAll();
    }

    @Test
    void getUsersByRole_ShouldReturnUsersByRole() {
        List<UserEntity> userEntities = Arrays.asList(userEntity);
        when(userRepository.findByRole(Role.USER)).thenReturn(userEntities);
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        List<UserDto> result = userService.getUsersByRole(Role.USER);

        assertEquals(1, result.size());
        assertEquals(Role.USER, result.get(0).getRole());
        verify(userRepository).findByRole(Role.USER);
    }

    @Test
    void getActiveUsers_ShouldReturnEnabledUsers() {
        List<UserEntity> userEntities = Arrays.asList(userEntity);
        when(userRepository.findByEnabledTrue()).thenReturn(userEntities);
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        List<UserDto> result = userService.getActiveUsers();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isEnabled());
        verify(userRepository).findByEnabledTrue();
    }

    @Test
    void searchUsersByName_ShouldReturnMatchingUsers() {
        List<UserEntity> userEntities = Arrays.asList(userEntity);
        when(userRepository.findByFirstNameContaining("Test")).thenReturn(userEntities);
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        List<UserDto> result = userService.searchUsersByName("Test");

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getFirstName());
        verify(userRepository).findByFirstNameContaining("Test");
    }

    @Test
    void getActiveUsersByRole_ShouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> userEntityPage = new PageImpl<>(Arrays.asList(userEntity));
        when(userRepository.findActiveUsersByRole(Role.USER, pageable)).thenReturn(userEntityPage);
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        Page<UserDto> result = userService.getActiveUsersByRole(Role.USER, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(Role.USER, result.getContent().get(0).getRole());
        verify(userRepository).findActiveUsersByRole(Role.USER, pageable);
    }

    @Test
    void countUsersByRole_ShouldReturnCount() {
        when(userRepository.countByRole(Role.USER)).thenReturn(5L);

        long result = userService.countUsersByRole(Role.USER);

        assertEquals(5L, result);
        verify(userRepository).countByRole(Role.USER);
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenValidData() {
        UserDto updateDto = UserDto.builder()
                .email("newemail@example.com")
                .firstName("Updated")
                .lastName("User")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userMapper.updateEntity(userEntity, updateDto)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(userDto);

        UserDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        verify(userRepository).save(userEntity);
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(1L, userDto));

        verify(userRepository, never()).save(any());
    }

    @Test
    void enableUser_ShouldEnableUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        userService.enableUser(1L);

        verify(userRepository).save(userEntity);
        assertTrue(userEntity.isEnabled());
    }

    @Test
    void disableUser_ShouldDisableUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        userService.disableUser(1L);

        verify(userRepository).save(userEntity);
        assertFalse(userEntity.isEnabled());
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> userService.deleteUser(1L));

        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void changePassword_ShouldChangePassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.encode("newpassword")).thenReturn("encodednewpassword");
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        userService.changePassword(1L, "newpassword");

        verify(passwordEncoder).encode("newpassword");
        verify(userRepository).save(userEntity);
        assertEquals("encodednewpassword", userEntity.getPassword());
    }

    @Test
    void isUsernameAvailable_ShouldReturnTrue_WhenUsernameNotExists() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);

        boolean result = userService.isUsernameAvailable("newuser");

        assertTrue(result);
        verify(userRepository).existsByUsername("newuser");
    }

    @Test
    void isEmailAvailable_ShouldReturnFalse_WhenEmailExists() {
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        boolean result = userService.isEmailAvailable("existing@example.com");

        assertFalse(result);
        verify(userRepository).existsByEmail("existing@example.com");
    }
}