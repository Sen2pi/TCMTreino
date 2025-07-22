package com.treasury.kpstreasury.repositories;


import com.treasury.kpstreasury.enums.Role;
import com.treasury.kpstreasury.models.entity.UserEntity;
import com.treasury.kpstreasury.utils.TestsUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
public class UserRepositoryTests {

    private final TestsUtil testsUtil = new TestsUtil();

    @Autowired
    private TestEntityManager entityManager; // Para setup de dados de teste

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByUsername() {
        // Given - preparar dados de teste
        UserEntity user = testsUtil.createUserEntityA();
        user.setUsername("test.user");
        user.setPassword("encoded_password");
        user.setEmail("test.user@fujitsu.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(Role.TREASURY);
        user.setEnabled(true);

        // Persistir na base de dados de teste
        entityManager.persistAndFlush(user);

        // When - executar método a testar
        Optional<UserEntity> found = userRepository.findByUsername("test.user");

        // Then - verificar resultados
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test.user@fujitsu.com");
        assertThat(found.get().getRole()).isEqualTo(Role.TREASURY);
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        // When
        Optional<UserEntity> found = userRepository.findByUsername("nonexistent");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldCheckIfUsernameExists() {
        // Given
        UserEntity user = testsUtil.createUserEntityA();
        user.setUsername("existing.user");
        user.setPassword("password");
        user.setEmail("user@test.com");
        user.setFirstName("Existing");
        user.setLastName("User");
        user.setRole(Role.USER);
        entityManager.persistAndFlush(user);

        // When & Then
        assertThat(userRepository.existsByUsername("existing.user")).isTrue();
        assertThat(userRepository.existsByUsername("nonexistent.user")).isFalse();
    }

    @Test
    void shouldFindUsersByRole() {
        // Given
        UserEntity treasuryUser = testsUtil.createUserEntityA();
        userRepository.save(treasuryUser);

        UserEntity adminUser = testsUtil.createUserEntityC();
        userRepository.save(adminUser);

        UserEntity regularUser = testsUtil.createUserEntityD();
        userRepository.save(regularUser);

        entityManager.flush();
        entityManager.clear();

        // When
        Iterable<UserEntity> treasuryUsers = userRepository.findByRole(Role.TREASURY);
        Iterable<UserEntity> adminUsers = userRepository.findByRole(Role.ADMIN);

        // Then
        // Convert Iterable to List for easier assertions
        List<UserEntity> treasuryUsersList = StreamSupport.stream(treasuryUsers.spliterator(), false)
                .toList();
        List<UserEntity> adminUsersList = StreamSupport.stream(adminUsers.spliterator(), false)
                .toList();

        // Verify treasury users
        assertThat(treasuryUsersList.size()).isGreaterThan(0);
        assertThat(treasuryUsersList.get(0).getUsername()).isEqualTo("treasury.user");
        assertThat(treasuryUsersList.get(0).getRole()).isEqualTo(Role.TREASURY);

        // Verify admin users
        assertThat(adminUsersList.size()).isGreaterThan(0);
        assertThat(adminUsersList.get(0).getUsername()).isEqualTo("admin.user");
        assertThat(adminUsersList.get(0).getRole()).isEqualTo(Role.ADMIN);
    }

}
