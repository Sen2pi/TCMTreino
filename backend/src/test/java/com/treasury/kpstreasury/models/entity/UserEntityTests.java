package com.treasury.kpstreasury.models.entity;


import com.treasury.kpstreasury.utils.TestsUtil;
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
public class UserEntityTests {

    private final TestsUtil testsUtil = new TestsUtil();

    @Test
    void shouldCreateUserWithRequiredFields() {
        UserEntity user = testsUtil.createUserEntityA();

        assertThat(user.getUsername()).isEqualTo("johnDoe");
        assertThat(user.getEmail()).isEqualTo("johndoe@gmail.com");
        assertThat(user.getRole()).isEqualTo(Role.TREASURY);
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void ShouldImplementUserDetailsCorrectly() {
        UserEntity user = testsUtil.createUserEntityA();
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
