package com.treasury.kpstreasury.models.entity;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
public class UserTests {

    @Test
    void shouldCreateUserWithRequiredFields() {
        UserEntity user = new UserEntity();
        user.setUsername("John Doe");
        user.setPassword("uncoded_password");
        user.setEmail("johndoe@gmail.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(Role.TREASURY);

        assertThat(user.getUsername()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("johndoe@gmail.com");
        assertThat(user.getRole()).isEqualTo(Role.TREASURY);
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void ShouldImplementUserDetailsCorrectly() {
        UserEntity user = new UserEntity();
        user.setRole(Role.ADMIN);
        user.setEnabled(true);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isEnabled()).isTrue();
    }

}
