package com.treasury.kpstreasury.controllers;

import com.treasury.kpstreasury.config.JwtUtils;
import com.treasury.kpstreasury.models.dto.AuthResponse;
import com.treasury.kpstreasury.models.dto.CreateUserDto;
import com.treasury.kpstreasury.models.dto.LoginRequest;
import com.treasury.kpstreasury.models.dto.UserDto;
import com.treasury.kpstreasury.models.entity.UserEntity;
import com.treasury.kpstreasury.services.UserService;
import com.treasury.kpstreasury.services.EventPublisher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final EventPublisher eventPublisher;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserEntity userDetails = (UserEntity) authentication.getPrincipal();

            String jwt = jwtUtils.generateJwtToken(authentication);

            AuthResponse authResponse = new AuthResponse(
                    jwt,
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    userDetails.getRole(),
                    jwtUtils.getExpirationTime()
            );

            // Publish login event
            eventPublisher.publishUserLogin(userDetails.getId(), userDetails.getUsername());

            return ResponseEntity.ok(authResponse);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Authentication failed");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody CreateUserDto createUserDto) {
        try {
            // Check if username exists
            if (!userService.isUsernameAvailable(createUserDto.getUsername())) {
                return ResponseEntity.badRequest()
                        .body("Username is already taken!");
            }

            // Check if email exists
            if (!userService.isEmailAvailable(createUserDto.getEmail())) {
                return ResponseEntity.badRequest()
                        .body("Email is already in use!");
            }

            // Create new user
            UserDto user = userService.createUser(createUserDto);

            return ResponseEntity.ok("User registered successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("User logged out successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not authenticated");
        }

        UserEntity userDetails = (UserEntity) authentication.getPrincipal();
        UserDto currentUser = userService.getUserByUsername(userDetails.getUsername()).orElse(null);

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }

        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token is invalid");
        }

        return ResponseEntity.ok("Token is valid");
    }
}