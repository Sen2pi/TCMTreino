package com.treasury.kpstreasury.models.dto;

import com.treasury.kpstreasury.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private Role role;
    private Long expiresIn; // em segundos

    public AuthResponse(String token, String username, String email, Role role, Long expiresIn) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role;
        this.expiresIn = expiresIn;
    }
}