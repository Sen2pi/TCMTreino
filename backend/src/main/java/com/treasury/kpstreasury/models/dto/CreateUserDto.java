package com.treasury.kpstreasury.models.dto;

import com.treasury.kpstreasury.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserDto {

    @NotBlank(message = "Username is Required")
    @Size(min = 3, max = 50, message = "Username should have between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is Required")
    @Size(min = 8,message = "Password should have at least 8 charcters, 1 Major and one Symbol")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email deve ser válido")
    private String email;

    @NotBlank(message = "First Name is Required")
    private String firstName;

    @NotBlank(message = "Last Name is Required")
    private String lastName;

    @NotNull(message = "Role is Required")
    private Role role;
}
