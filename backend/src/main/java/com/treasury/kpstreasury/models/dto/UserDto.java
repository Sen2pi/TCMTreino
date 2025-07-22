package com.treasury.kpstreasury.models.dto;

import com.treasury.kpstreasury.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.aspectj.bridge.IMessage;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(message = "Username is mandatory!")
    @Size(min=3, max=50, message = "Username must have between 3 and 50 caracters")
    private String username;

    @NotBlank(message= "Email is mandatory!")
    @Email(message = "Email should be a valid email adress example: test@test.com")
    private String email;

    @NotBlank(message = "First Name is mandatory!")
    @Size(max = 50, message = "First name shouldn't exeed the margin of 50 characters")
    private String firstName;

    @NotBlank(message = "lAST Name is mandatory!")
    @Size(max = 50, message = "Last name shouldn't exeed the margin of 50 characters")
    private String lastName;

    @NotNull(message= "Role is mandatory")
    private Role role;

    private boolean enabled;

    private LocalDateTime createdAt;
}
