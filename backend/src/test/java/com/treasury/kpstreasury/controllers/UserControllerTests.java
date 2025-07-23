package com.treasury.kpstreasury.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.treasury.kpstreasury.config.TestSecurityConfig;
import com.treasury.kpstreasury.enums.Role;
import com.treasury.kpstreasury.models.dto.CreateUserDto;
import com.treasury.kpstreasury.models.dto.UserDto;
import com.treasury.kpstreasury.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;
    private CreateUserDto createUserDto;

    @BeforeEach
    void setUp() {
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
    void createUser_ShouldReturnCreated_WhenValidData() throws Exception {
        when(userService.createUser(any(CreateUserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenUserAlreadyExists() throws Exception {
        when(userService.createUser(any(CreateUserDto.class)))
                .thenThrow(new IllegalArgumentException("Username already exists"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(userDto));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void getUserById_ShouldReturnNotFound_WhenUserNotExists() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        List<UserDto> users = Arrays.asList(userDto);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    void getUsersByRole_ShouldReturnUsersByRole() throws Exception {
        List<UserDto> users = Arrays.asList(userDto);
        when(userService.getUsersByRole(Role.USER)).thenReturn(users);

        mockMvc.perform(get("/api/users/role/USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].role").value("USER"));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenValidData() throws Exception {
        when(userService.updateUser(anyLong(), any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void updateUser_ShouldReturnNotFound_WhenUserNotExists() throws Exception {
        when(userService.updateUser(anyLong(), any(UserDto.class)))
                .thenThrow(new IllegalArgumentException("User not found"));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void enableUser_ShouldReturnOk_WhenUserExists() throws Exception {
        doNothing().when(userService).enableUser(1L);

        mockMvc.perform(put("/api/users/1/enable")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void disableUser_ShouldReturnOk_WhenUserExists() throws Exception {
        doNothing().when(userService).disableUser(1L);

        mockMvc.perform(put("/api/users/1/disable")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_ShouldReturnNoContent_WhenUserExists() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_ShouldReturnNotFound_WhenUserNotExists() throws Exception {
        doThrow(new IllegalArgumentException("User not found")).when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void changePassword_ShouldReturnOk_WhenValidData() throws Exception {
        doNothing().when(userService).changePassword(1L, "newpassword");

        mockMvc.perform(put("/api/users/1/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"newpassword\""))
                .andExpect(status().isOk());
    }

    @Test
    void isUsernameAvailable_ShouldReturnBoolean() throws Exception {
        when(userService.isUsernameAvailable("testuser")).thenReturn(true);

        mockMvc.perform(get("/api/users/check-username")
                        .param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void isEmailAvailable_ShouldReturnBoolean() throws Exception {
        when(userService.isEmailAvailable("test@example.com")).thenReturn(false);

        mockMvc.perform(get("/api/users/check-email")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void createUser_ShouldWork_WhenSecurityDisabled() throws Exception {
        when(userService.createUser(any(CreateUserDto.class))).thenReturn(userDto);
        
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isCreated());
    }
}