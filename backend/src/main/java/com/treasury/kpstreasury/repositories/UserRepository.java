package com.treasury.kpstreasury.repositories;

import com.treasury.kpstreasury.enums.Role;
import com.treasury.kpstreasury.models.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByRole(Role role);

    List<UserEntity> findByEnabledTrue();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);


    //Custumized query using JPS We use this when the method name is not enough to define the auto query from JPS , so we give JPA  an hint
    @Query("SELECT u FROM UserEntity u WHERE u.firstName LIKE %:name% OR u.lastName LIKE %:name%")
    List<UserEntity> findByFirstNameContaining(@Param("name") String name);


    //Pagination
    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.role = :role AND u.enabled = true")
    Page<UserEntity> findActiveUsersByRole(@Param("role") Role role, Pageable pageable);

    //Count by role
    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.role = :role")
    long countByRole(@Param("role") Role role);



}
