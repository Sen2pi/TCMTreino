package com.treasury.KPStreasury.services;

import com.treasury.KPStreasury.exceptions.BusinessException;
import com.treasury.KPStreasury.mappers.UserMapper;
import com.treasury.KPStreasury.models.dto.LoginRequest;
import com.treasury.KPStreasury.models.dto.AuthResponse;
import com.treasury.KPStreasury.models.dto.UserDto;
import com.treasury.KPStreasury.models.entity.User;
import com.treasury.KPStreasury.repository.UserRepository;
import com.treasury.KPStreasury.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    
    public AuthResponse authenticate(LoginRequest loginRequest) {
        log.debug("Authenticating user: {}", loginRequest.getUsername());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            
            User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new BusinessException("User not found"));
            
            UserDto userDto = userMapper.toDto(user);
            
            log.info("User authenticated successfully: {}", loginRequest.getUsername());
            
            return AuthResponse.builder()
                .token(token)
                .user(userDto)
                .build();
                
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", loginRequest.getUsername(), e);
            throw new BusinessException("Invalid credentials");
        }
    }
    
    public UserDto createUser(UserDto userDto) {
        log.debug("Creating new user: {}", userDto.getUsername());
        
        validateUserData(userDto);
        checkUsernameUniqueness(userDto.getUsername());
        checkEmailUniqueness(userDto.getEmail());
        
        User user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode("defaultPassword123")); // Should be changed on first login
        user.setEnabled(true);
        
        User saved = userRepository.save(user);
        
        log.info("User created successfully: {}", saved.getUsername());
        
        return userMapper.toDto(saved);
    }
    
    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        log.debug("Finding user by id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with id: " + id));
        
        return userMapper.toDto(user);
    }
    
    @Transactional(readOnly = true)
    public UserDto findByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found with username: " + username));
        
        return userMapper.toDto(user);
    }
    
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        log.debug("Finding all users");
        
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
    
    public UserDto updateUser(Long id, UserDto userDto) {
        log.debug("Updating user with id: {}", id);
        
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with id: " + id));
        
        validateUserData(userDto);
        
        if (!existing.getUsername().equals(userDto.getUsername())) {
            checkUsernameUniqueness(userDto.getUsername());
        }
        
        if (!existing.getEmail().equals(userDto.getEmail())) {
            checkEmailUniqueness(userDto.getEmail());
        }
        
        userMapper.updateEntityFromDto(userDto, existing);
        User updated = userRepository.save(existing);
        
        log.info("User updated successfully: {}", updated.getUsername());
        
        return userMapper.toDto(updated);
    }
    
    public void deleteUser(Long id) {
        log.debug("Deleting user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with id: " + id));
        
        userRepository.delete(user);
        
        log.info("User deleted successfully: {}", user.getUsername());
    }
    
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.debug("Changing password for user: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("Current password is incorrect");
        }
        
        validatePassword(newPassword);
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        log.info("Password changed successfully for user: {}", username);
    }
    
    @Transactional(readOnly = true)
    public List<UserDto> findByRole(String role) {
        log.debug("Finding users by role: {}", role);
        
        return userRepository.findByRole(role)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
    
    public void enableUser(Long id) {
        log.debug("Enabling user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with id: " + id));
        
        user.setEnabled(true);
        userRepository.save(user);
        
        log.info("User enabled: {}", user.getUsername());
    }
    
    public void disableUser(Long id) {
        log.debug("Disabling user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with id: " + id));
        
        user.setEnabled(false);
        userRepository.save(user);
        
        log.info("User disabled: {}", user.getUsername());
    }
    
    private void validateUserData(UserDto userDto) {
        if (userDto.getUsername() == null || userDto.getUsername().trim().isEmpty()) {
            throw new BusinessException("Username is required");
        }
        
        if (userDto.getUsername().length() < 3) {
            throw new BusinessException("Username must be at least 3 characters long");
        }
        
        if (userDto.getEmail() == null || userDto.getEmail().trim().isEmpty()) {
            throw new BusinessException("Email is required");
        }
        
        if (!userDto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new BusinessException("Invalid email format");
        }
        
        if (userDto.getFirstName() == null || userDto.getFirstName().trim().isEmpty()) {
            throw new BusinessException("First name is required");
        }
        
        if (userDto.getLastName() == null || userDto.getLastName().trim().isEmpty()) {
            throw new BusinessException("Last name is required");
        }
        
        if (userDto.getRole() == null || userDto.getRole().trim().isEmpty()) {
            throw new BusinessException("Role is required");
        }
        
        if (!isValidRole(userDto.getRole())) {
            throw new BusinessException("Invalid role. Allowed roles: USER, TREASURY, COLLATERAL, ADMIN");
        }
    }
    
    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new BusinessException("Password must be at least 8 characters long");
        }
        
        if (!password.matches(".*[A-Z].*")) {
            throw new BusinessException("Password must contain at least one uppercase letter");
        }
        
        if (!password.matches(".*[a-z].*")) {
            throw new BusinessException("Password must contain at least one lowercase letter");
        }
        
        if (!password.matches(".*[0-9].*")) {
            throw new BusinessException("Password must contain at least one digit");
        }
    }
    
    private boolean isValidRole(String role) {
        return List.of("USER", "TREASURY", "COLLATERAL", "ADMIN").contains(role.toUpperCase());
    }
    
    private void checkUsernameUniqueness(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException("Username already exists: " + username);
        }
    }
    
    private void checkEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Email already exists: " + email);
        }
    }
}
