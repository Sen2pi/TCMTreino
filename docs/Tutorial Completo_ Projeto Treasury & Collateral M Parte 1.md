# Tutorial Completo: Projeto Treasury \& Collateral Management - Ordem de Desenvolvimento com TDD

## Introdução à Metodologia de Desenvolvimento

Este tutorial segue uma abordagem **Test-Driven Development (TDD)** combinada com a arquitetura em camadas do Spring Boot[^1][^2]. Vamos construir o projeto seguindo uma ordem específica que garante qualidade, testabilidade e manutenibilidade.

### Por que esta Ordem?

A ordem que seguiremos baseia-se nas melhores práticas da indústria[^3][^4][^5]:

1. **Entidades primeiro**: São a base do nosso domínio
2. **Repositórios**: Para acesso aos dados
3. **DTOs**: Para transferência de dados entre camadas
4. **Serviços**: Para lógica de negócio
5. **Controladores**: Para APIs REST

## Fase 1: Criação das Entidades JPA

### Por que começamos pelas Entidades?

As entidades representam o **modelo de domínio** da nossa aplicação[^6]. São a base de tudo - definem como os dados são estruturados e relacionados. No Spring Boot, as entidades JPA são classes que mapeiam diretamente para tabelas na base de dados[^7].

### 1.1 Criando a Entidade User

```java
// src/main/java/com/fujitsu/treasury/model/entity/User.java
package com.fujitsu.treasury.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * Entidade User que implementa UserDetails para integração com Spring Security.
 * 
 * @Entity - Indica que esta classe é uma entidade JPA
 * @Table - Define o nome da tabela na base de dados
 * UserDetails - Interface do Spring Security para detalhes do utilizador
 */
@Entity
@Table(name = "users") // 'user' é palavra reservada em algumas BDs
@Data                  // Lombok: gera getters, setters, toString, equals, hashCode
@NoArgsConstructor     // Lombok: construtor sem argumentos (obrigatório JPA)
@AllArgsConstructor    // Lombok: construtor com todos os argumentos
public class User implements UserDetails {
    
    @Id                                    // Chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremento
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(nullable = false, length = 100)
    private String password;
    
    @Column(nullable = false, length = 100)
    private String email;
    
    @Column(nullable = false, length = 50)
    private String firstName;
    
    @Column(nullable = false, length = 50)
    private String lastName;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING) // Armazena como string na BD
    private Role role;
    
    @Column(nullable = false)
    private Boolean enabled = true;
    
    @CreationTimestamp    // Hibernate: timestamp automático na criação
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Implementação do UserDetails para Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
```


### 1.2 Enum Role

```java
// src/main/java/com/fujitsu/treasury/model/entity/Role.java
package com.fujitsu.treasury.model.entity;

/**
 * Enum para definir os papéis/roles dos utilizadores no sistema.
 * Usado para controlo de acesso baseado em roles (RBAC).
 */
public enum Role {
    ADMIN,      // Acesso total ao sistema
    TREASURY,   // Acesso ao módulo Treasury
    COLLATERAL, // Acesso ao módulo Collateral
    USER        // Utilizador básico
}
```


### 1.3 Criando a Entidade Treasury

```java
// src/main/java/com/fujitsu/treasury/model/entity/Treasury.java
package com.fujitsu.treasury.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade Treasury representa uma conta de tesouraria.
 * Contém informações sobre contas bancárias e saldos.
 */
@Entity
@Table(name = "treasury")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Treasury {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String accountNumber;
    
    @Column(nullable = false, length = 3)
    private String currency;
    
    /**
     * BigDecimal é usado para valores monetários por precisão.
     * precision = 19 (total de dígitos)
     * scale = 2 (casas decimais)
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal availableBalance;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    
    @Column(nullable = false, length = 100)
    private String bankName;
    
    @Column(nullable = false, length = 20)
    private String branchCode;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp // Atualizado automaticamente em cada update
    private LocalDateTime updatedAt;
}
```


### 1.4 Enums para Treasury

```java
// src/main/java/com/fujitsu/treasury/model/entity/AccountType.java
package com.fujitsu.treasury.model.entity;

public enum AccountType {
    CHECKING,   // Conta corrente
    SAVINGS,    // Conta poupança
    INVESTMENT, // Conta investimento
    DEPOSIT     // Conta depósito
}
```

```java
// src/main/java/com/fujitsu/treasury/model/entity/AccountStatus.java
package com.fujitsu.treasury.model.entity;

public enum AccountStatus {
    ACTIVE,     // Conta ativa
    INACTIVE,   // Conta inativa
    SUSPENDED,  // Conta suspensa
    CLOSED      // Conta fechada
}
```


### 1.5 Criando a Entidade Collateral

```java
// src/main/java/com/fujitsu/treasury/model/entity/Collateral.java
package com.fujitsu.treasury.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade Collateral representa um ativo usado como garantia.
 * Inclui informações sobre valor, elegibilidade e risk assessment.
 */
@Entity
@Table(name = "collateral")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collateral {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private CollateralType collateralType;
    
    @Column(nullable = false, length = 255)
    private String description;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal marketValue;
    
    /**
     * Haircut: percentual de desconto aplicado ao valor de mercado
     * para calcular o valor elegível (risk adjustment)
     */
    @Column(nullable = false, precision = 5, scale = 4) // Ex: 0.1500 = 15%
    private BigDecimal haircut;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal eligibleValue; // Calculado: marketValue * (1 - haircut)
    
    @Column(nullable = false, length = 3)
    private String currency;
    
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Rating rating;
    
    @Column(nullable = false)
    private LocalDate maturityDate;
    
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private CollateralStatus status;
    
    @Column(nullable = false, length = 100)
    private String counterparty; // Contraparte
    
    @Column(nullable = false, length = 100)
    private String location; // Localização física/custódia
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    /**
     * Método para recalcular valor elegível quando market value ou haircut mudam.
     * Este método será chamado nos setters ou através de um @PrePersist/@PreUpdate.
     */
    @PrePersist
    @PreUpdate
    public void calculateEligibleValue() {
        if (marketValue != null && haircut != null) {
            this.eligibleValue = marketValue.multiply(BigDecimal.ONE.subtract(haircut));
        }
    }
}
```


### 1.6 Enums para Collateral

```java
// src/main/java/com/fujitsu/treasury/model/entity/CollateralType.java
package com.fujitsu.treasury.model.entity;

public enum CollateralType {
    GOVERNMENT_BOND,    // Obrigações do Estado
    CORPORATE_BOND,     // Obrigações corporativas
    EQUITY,            // Ações
    REAL_ESTATE,       // Imobiliário
    COMMODITY,         // Commodities
    CASH_EQUIVALENT    // Equivalentes de caixa
}
```

```java
// src/main/java/com/fujitsu/treasury/model/entity/Rating.java
package com.fujitsu.treasury.model.entity;

public enum Rating {
    AAA, AA, A,     // Investment grade alto
    BBB,            // Investment grade baixo
    BB, B,          // Speculative grade
    CCC, CC, C,     // Substantial risk
    D               // Default
}
```

```java
// src/main/java/com/fujitsu/treasury/model/entity/CollateralStatus.java
package com.fujitsu.treasury.model.entity;

public enum CollateralStatus {
    ELIGIBLE,       // Elegível para uso como garantia
    INELIGIBLE,     // Não elegível
    PLEDGED,        // Empenhado/usado como garantia
    RETURNED,       // Devolvido
    MATURED         // Vencido
}
```


### 1.7 Teste das Entidades

Agora vamos criar testes para validar nossas entidades:

```java
// src/test/java/com/fujitsu/treasury/model/entity/UserTest.java
package com.fujitsu.treasury.model.entity;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste unitário para a entidade User.
 * Verifica a criação, comportamento dos métodos e integração com Spring Security.
 */
class UserTest {
    
    @Test
    void shouldCreateUserWithRequiredFields() {
        // Given
        User user = new User();
        user.setUsername("john.doe");
        user.setPassword("encoded_password");
        user.setEmail("john.doe@fujitsu.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(Role.TREASURY);
        
        // When & Then
        assertThat(user.getUsername()).isEqualTo("john.doe");
        assertThat(user.getEmail()).isEqualTo("john.doe@fujitsu.com");
        assertThat(user.getRole()).isEqualTo(Role.TREASURY);
        assertThat(user.isEnabled()).isTrue(); // valor padrão
    }
    
    @Test
    void shouldImplementUserDetailsCorrectly() {
        // Given
        User user = new User();
        user.setRole(Role.ADMIN);
        user.setEnabled(true);
        
        // When
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        
        // Then
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.isEnabled()).isTrue();
    }
}
```

```java
// src/test/java/com/fujitsu/treasury/model/entity/TreasuryTest.java
package com.fujitsu.treasury.model.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TreasuryTest {
    
    @Test
    void shouldCreateTreasuryWithRequiredFields() {
        // Given
        Treasury treasury = new Treasury();
        treasury.setAccountNumber("ACC-001");
        treasury.setCurrency("EUR");
        treasury.setBalance(new BigDecimal("1000000.00"));
        treasury.setAvailableBalance(new BigDecimal("800000.00"));
        treasury.setAccountType(AccountType.CHECKING);
        treasury.setStatus(AccountStatus.ACTIVE);
        treasury.setBankName("Banco Comercial Português");
        treasury.setBranchCode("BCP-LX001");
        
        // When & Then
        assertThat(treasury.getAccountNumber()).isEqualTo("ACC-001");
        assertThat(treasury.getCurrency()).isEqualTo("EUR");
        assertThat(treasury.getBalance()).isEqualByComparingTo("1000000.00");
        assertThat(treasury.getAvailableBalance()).isEqualByComparingTo("800000.00");
        assertThat(treasury.getAccountType()).isEqualTo(AccountType.CHECKING);
        assertThat(treasury.getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }
}
```

```java
// src/test/java/com/fujitsu/treasury/model/entity/CollateralTest.java
package com.fujitsu.treasury.model.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CollateralTest {
    
    @Test
    void shouldCreateCollateralWithRequiredFields() {
        // Given
        Collateral collateral = new Collateral();
        collateral.setCollateralType(CollateralType.GOVERNMENT_BOND);
        collateral.setDescription("Portuguese Government Bond 2025");
        collateral.setMarketValue(new BigDecimal("500000.00"));
        collateral.setHaircut(new BigDecimal("0.0500")); // 5%
        collateral.setCurrency("EUR");
        collateral.setRating(Rating.AAA);
        collateral.setMaturityDate(LocalDate.of(2025, 12, 31));
        collateral.setStatus(CollateralStatus.ELIGIBLE);
        collateral.setCounterparty("Portuguese Republic");
        collateral.setLocation("Euroclear");
        
        // When
        collateral.calculateEligibleValue(); // Simula @PrePersist
        
        // Then
        assertThat(collateral.getEligibleValue()).isEqualByComparingTo("475000.00"); // 500k * 0.95
        assertThat(collateral.getCollateralType()).isEqualTo(CollateralType.GOVERNMENT_BOND);
        assertThat(collateral.getRating()).isEqualTo(Rating.AAA);
        assertThat(collateral.getStatus()).isEqualTo(CollateralStatus.ELIGIBLE);
    }
    
    @Test
    void shouldCalculateEligibleValueCorrectly() {
        // Given
        Collateral collateral = new Collateral();
        collateral.setMarketValue(new BigDecimal("1000000.00"));
        collateral.setHaircut(new BigDecimal("0.1500")); // 15%
        
        // When
        collateral.calculateEligibleValue();
        
        // Then
        assertThat(collateral.getEligibleValue()).isEqualByComparingTo("850000.00");
    }
}
```

**Execute os testes:**

```bash
mvn test -Dtest="*Test"
```


## Fase 2: Criação dos Repositórios

### Por que Repositórios depois das Entidades?

Os repositórios são a camada de acesso aos dados[^5]. Eles encapsulam a lógica necessária para aceder às fontes de dados. No Spring Data JPA, interfaces repository fornecem operações CRUD automáticas e queries customizadas[^8].

### 2.1 Repository Base

```java
// src/main/java/com/fujitsu/treasury/repository/UserRepository.java
package com.fujitsu.treasury.repository;

import com.fujitsu.treasury.model.entity.Role;
import com.fujitsu.treasury.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para User entity.
 * Extende JpaRepository que fornece operações CRUD básicas.
 * 
 * @Repository - Indica que é um componente repository do Spring
 * JpaRepository<User, Long> - User é a entidade, Long é o tipo da chave primária
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Spring Data JPA cria automaticamente a implementação baseada no nome do método.
     * findBy + PropertyName = SELECT * FROM users WHERE property = ?
     */
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    List<User> findByRole(Role role);
    
    List<User> findByEnabledTrue(); // Apenas utilizadores ativos
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    /**
     * Query customizada usando JPQL (Java Persistence Query Language).
     * @Query permite definir queries específicas quando o nome do método não é suficiente.
     */
    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:name% OR u.lastName LIKE %:name%")
    List<User> findByNameContaining(@Param("name") String name);
    
    /**
     * Query com paginação para performance em datasets grandes.
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.enabled = true")
    Page<User> findActiveUsersByRole(@Param("role") Role role, Pageable pageable);
    
    /**
     * Contagem de utilizadores por role.
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") Role role);
}
```


### 2.2 Treasury Repository

```java
// src/main/java/com/fujitsu/treasury/repository/TreasuryRepository.java
package com.fujitsu.treasury.repository;

import com.fujitsu.treasury.model.entity.AccountStatus;
import com.fujitsu.treasury.model.entity.AccountType;
import com.fujitsu.treasury.model.entity.Treasury;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TreasuryRepository extends JpaRepository<Treasury, Long> {
    
    Optional<Treasury> findByAccountNumber(String accountNumber);
    
    List<Treasury> findByStatus(AccountStatus status);
    
    List<Treasury> findByAccountType(AccountType accountType);
    
    List<Treasury> findByCurrency(String currency);
    
    List<Treasury> findByBankName(String bankName);
    
    boolean existsByAccountNumber(String accountNumber);
    
    /**
     * Queries financeiras para relatórios e dashboards.
     */
    @Query("SELECT SUM(t.balance) FROM Treasury t WHERE t.currency = :currency AND t.status = :status")
    BigDecimal getTotalBalanceByCurrencyAndStatus(@Param("currency") String currency, 
                                                  @Param("status") AccountStatus status);
    
    @Query("SELECT SUM(t.availableBalance) FROM Treasury t WHERE t.status = 'ACTIVE'")
    BigDecimal getTotalAvailableBalance();
    
    /**
     * Query para relatórios agregados.
     * Retorna lista de Objects[] com [currency, totalBalance, accountCount]
     */
    @Query("SELECT t.currency, SUM(t.balance), COUNT(t) FROM Treasury t " +
           "WHERE t.status = 'ACTIVE' GROUP BY t.currency")
    List<Object[]> getTreasurySummaryByCurrency();
    
    /**
     * Contas com saldo baixo (disponível < limite especificado).
     */
    @Query("SELECT t FROM Treasury t WHERE t.availableBalance < :threshold AND t.status = 'ACTIVE'")
    List<Treasury> findLowBalanceAccounts(@Param("threshold") BigDecimal threshold);
    
    /**
     * Pesquisa por contas com filtros múltiplos.
     */
    @Query("SELECT t FROM Treasury t WHERE " +
           "(:currency IS NULL OR t.currency = :currency) AND " +
           "(:bankName IS NULL OR t.bankName LIKE %:bankName%) AND " +
           "(:status IS NULL OR t.status = :status)")
    Page<Treasury> findWithFilters(@Param("currency") String currency,
                                   @Param("bankName") String bankName,
                                   @Param("status") AccountStatus status,
                                   Pageable pageable);
}
```


### 2.3 Collateral Repository

```java
// src/main/java/com/fujitsu/treasury/repository/CollateralRepository.java
package com.fujitsu.treasury.repository;

import com.fujitsu.treasury.model.entity.Collateral;
import com.fujitsu.treasury.model.entity.CollateralStatus;
import com.fujitsu.treasury.model.entity.CollateralType;
import com.fujitsu.treasury.model.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CollateralRepository extends JpaRepository<Collateral, Long> {
    
    List<Collateral> findByStatus(CollateralStatus status);
    
    List<Collateral> findByCollateralType(CollateralType type);
    
    List<Collateral> findByRating(Rating rating);
    
    List<Collateral> findByCurrency(String currency);
    
    List<Collateral> findByCounterparty(String counterparty);
    
    /**
     * Collateral elegível para uso como garantia.
     */
    List<Collateral> findByStatusAndRatingIn(CollateralStatus status, List<Rating> acceptableRatings);
    
    /**
     * Collateral próximo do vencimento.
     */
    @Query("SELECT c FROM Collateral c WHERE c.maturityDate BETWEEN :startDate AND :endDate")
    List<Collateral> findByMaturityDateBetween(@Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);
    
    /**
     * Valor total de collateral elegível.
     */
    @Query("SELECT SUM(c.eligibleValue) FROM Collateral c WHERE c.status = 'ELIGIBLE'")
    BigDecimal getTotalEligibleValue();
    
    /**
     * Valor total por tipo de collateral.
     */
    @Query("SELECT c.collateralType, SUM(c.marketValue), SUM(c.eligibleValue) FROM Collateral c " +
           "WHERE c.status = 'ELIGIBLE' GROUP BY c.collateralType")
    List<Object[]> getCollateralSummaryByType();
    
    /**
     * Relatório de concentração por rating.
     */
    @Query("SELECT c.rating, COUNT(c), SUM(c.marketValue) FROM Collateral c " +
           "WHERE c.status = 'ELIGIBLE' GROUP BY c.rating ORDER BY c.rating")
    List<Object[]> getCollateralConcentrationByRating();
    
    /**
     * Collateral com haircut acima de um limite (maior risco).
     */
    @Query("SELECT c FROM Collateral c WHERE c.haircut > :haircutThreshold AND c.status = 'ELIGIBLE'")
    List<Collateral> findHighRiskCollateral(@Param("haircutThreshold") BigDecimal haircutThreshold);
    
    /**
     * Pesquisa avançada com múltiplos filtros.
     */
    @Query("SELECT c FROM Collateral c WHERE " +
           "(:type IS NULL OR c.collateralType = :type) AND " +
           "(:minRating IS NULL OR c.rating >= :minRating) AND " +
           "(:currency IS NULL OR c.currency = :currency) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:minValue IS NULL OR c.marketValue >= :minValue)")
    Page<Collateral> findWithAdvancedFilters(@Param("type") CollateralType type,
                                            @Param("minRating") Rating minRating,
                                            @Param("currency") String currency,
                                            @Param("status") CollateralStatus status,
                                            @Param("minValue") BigDecimal minValue,
                                            Pageable pageable);
}
```


### 2.4 Teste dos Repositórios

```java
// src/test/java/com/fujitsu/treasury/repository/UserRepositoryTest.java
package com.fujitsu.treasury.repository;

import com.fujitsu.treasury.model.entity.Role;
import com.fujitsu.treasury.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de integração para UserRepository.
 * @DataJpaTest configura um contexto mínimo apenas para testes JPA.
 * - Configura base de dados em memória (H2)
 * - Carrega apenas configurações JPA
 * - Disponibiliza TestEntityManager para operações de teste
 */
@DataJpaTest
class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager; // Para setup de dados de teste
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void shouldFindUserByUsername() {
        // Given - preparar dados de teste
        User user = new User();
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
        Optional<User> found = userRepository.findByUsername("test.user");
        
        // Then - verificar resultados
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test.user@fujitsu.com");
        assertThat(found.get().getRole()).isEqualTo(Role.TREASURY);
    }
    
    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        // When
        Optional<User> found = userRepository.findByUsername("nonexistent");
        
        // Then
        assertThat(found).isEmpty();
    }
    
    @Test
    void shouldCheckIfUsernameExists() {
        // Given
        User user = new User();
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
        User treasuryUser = createUser("treasury.user", Role.TREASURY);
        User adminUser = createUser("admin.user", Role.ADMIN);
        User regularUser = createUser("regular.user", Role.USER);
        
        entityManager.persistAndFlush(treasuryUser);
        entityManager.persistAndFlush(adminUser);
        entityManager.persistAndFlush(regularUser);
        
        // When
        var treasuryUsers = userRepository.findByRole(Role.TREASURY);
        var adminUsers = userRepository.findByRole(Role.ADMIN);
        
        // Then
        assertThat(treasuryUsers).hasSize(1);
        assertThat(treasuryUsers.get(0).getUsername()).isEqualTo("treasury.user");
        
        assertThat(adminUsers).hasSize(1);
        assertThat(adminUsers.get(0).getUsername()).isEqualTo("admin.user");
    }
    
    private User createUser(String username, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setEmail(username + "@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(role);
        user.setEnabled(true);
        return user;
    }
}
```

```java
// src/test/java/com/fujitsu/treasury/repository/TreasuryRepositoryTest.java
package com.fujitsu.treasury.repository;

import com.fujitsu.treasury.model.entity.AccountStatus;
import com.fujitsu.treasury.model.entity.AccountType;
import com.fujitsu.treasury.model.entity.Treasury;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TreasuryRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private TreasuryRepository treasuryRepository;
    
    @Test
    void shouldFindTreasuryByAccountNumber() {
        // Given
        Treasury treasury = createTreasury("ACC-001", "EUR", 
                                         new BigDecimal("1000000"), AccountStatus.ACTIVE);
        entityManager.persistAndFlush(treasury);
        
        // When
        Optional<Treasury> found = treasuryRepository.findByAccountNumber("ACC-001");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCurrency()).isEqualTo("EUR");
        assertThat(found.get().getBalance()).isEqualByComparingTo("1000000");
    }
    
    @Test
    void shouldCalculateTotalBalanceByCurrency() {
        // Given
        Treasury treasury1 = createTreasury("ACC-001", "EUR", new BigDecimal("1000000"), AccountStatus.ACTIVE);
        Treasury treasury2 = createTreasury("ACC-002", "EUR", new BigDecimal("2000000"), AccountStatus.ACTIVE);
        Treasury treasury3 = createTreasury("ACC-003", "USD", new BigDecimal("1500000"), AccountStatus.ACTIVE);
        
        entityManager.persistAndFlush(treasury1);
        entityManager.persistAndFlush(treasury2);
        entityManager.persistAndFlush(treasury3);
        
        // When
        BigDecimal totalEurBalance = treasuryRepository
            .getTotalBalanceByCurrencyAndStatus("EUR", AccountStatus.ACTIVE);
        
        // Then
        assertThat(totalEurBalance).isEqualByComparingTo("3000000");
    }
    
    @Test
    void shouldFindLowBalanceAccounts() {
        // Given
        Treasury lowBalance = createTreasury("ACC-LOW", "EUR", new BigDecimal("50000"), AccountStatus.ACTIVE);
        lowBalance.setAvailableBalance(new BigDecimal("50000"));
        
        Treasury highBalance = createTreasury("ACC-HIGH", "EUR", new BigDecimal("1000000"), AccountStatus.ACTIVE);
        highBalance.setAvailableBalance(new BigDecimal("1000000"));
        
        entityManager.persistAndFlush(lowBalance);
        entityManager.persistAndFlush(highBalance);
        
        // When
        List<Treasury> lowBalanceAccounts = treasuryRepository
            .findLowBalanceAccounts(new BigDecimal("100000"));
        
        // Then
        assertThat(lowBalanceAccounts).hasSize(1);
        assertThat(lowBalanceAccounts.get(0).getAccountNumber()).isEqualTo("ACC-LOW");
    }
    
    private Treasury createTreasury(String accountNumber, String currency, 
                                  BigDecimal balance, AccountStatus status) {
        Treasury treasury = new Treasury();
        treasury.setAccountNumber(accountNumber);
        treasury.setCurrency(currency);
        treasury.setBalance(balance);
        treasury.setAvailableBalance(balance);
        treasury.setAccountType(AccountType.CHECKING);
        treasury.setStatus(status);
        treasury.setBankName("Test Bank");
        treasury.setBranchCode("TB001");
        return treasury;
    }
}
```

**Execute os testes dos repositórios:**

```bash
mvn test -Dtest="*RepositoryTest"
```


## Fase 3: Criação dos DTOs (Data Transfer Objects)

### Por que DTOs agora?

DTOs servem para transferir dados entre as camadas da aplicação[^9][^10]. Eles encapsulam dados e fornecem uma interface estável para comunicação entre controller, service e outras camadas, separando o modelo interno (entidades) do modelo externo (API).

### 3.1 DTOs Base

```java
// src/main/java/com/fujitsu/treasury/model/dto/UserDto.java
package com.fujitsu.treasury.model.dto;

import com.fujitsu.treasury.model.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * DTO para transferência de dados de User.
 * Usado para:
 * - Requests da API (sem campos internos como password hash)
 * - Responses da API (sem informações sensíveis)
 * - Validação de dados de entrada
 * 
 * Vantagens dos DTOs:
 * - Controlo sobre quais campos são expostos
 * - Validação independente da entidade
 * - Versionamento da API sem afetar modelo interno
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private Long id;
    
    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
    private String username;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;
    
    @NotBlank(message = "Primeiro nome é obrigatório")
    @Size(max = 50, message = "Primeiro nome não pode ter mais de 50 caracteres")
    private String firstName;
    
    @NotBlank(message = "Último nome é obrigatório")
    @Size(max = 50, message = "Último nome não pode ter mais de 50 caracteres")
    private String lastName;
    
    @NotNull(message = "Role é obrigatório")
    private Role role;
    
    private Boolean enabled;
    
    private LocalDateTime createdAt;
    
    // Nota: password não está incluída por questões de segurança
}
```

```java
// src/main/java/com/fujitsu/treasury/model/dto/CreateUserDto.java
package com.fujitsu.treasury.model.dto;

import com.fujitsu.treasury.model.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO específico para criação de utilizadores.
 * Inclui password para operações de criação.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {
    
    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
    private String username;
    
    @NotBlank(message = "Password é obrigatória")
    @Size(min = 8, message = "Password deve ter pelo menos 8 caracteres")
    private String password;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;
    
    @NotBlank(message = "Primeiro nome é obrigatório")
    private String firstName;
    
    @NotBlank(message = "Último nome é obrigatório")
    private String lastName;
    
    @NotNull(message = "Role é obrigatório")
    private Role role;
}
```


### 3.2 Treasury DTOs

```java
// src/main/java/com/fujitsu/treasury/model/dto/TreasuryDto.java
package com.fujitsu.treasury.model.dto;

import com.fujitsu.treasury.model.entity.AccountStatus;
import com.fujitsu.treasury.model.entity.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreasuryDto {
    
    private Long id;
    
    @NotBlank(message = "Número da conta é obrigatório")
    @Size(max = 50, message = "Número da conta não pode ter mais de 50 caracteres")
    private String accountNumber;
    
    @NotBlank(message = "Moeda é obrigatória")
    @Size(min = 3, max = 3, message = "Moeda deve ter 3 caracteres (ISO 4217)")
    private String currency;
    
    @NotNull(message = "Saldo é obrigatório")
    @DecimalMin(value = "0.0", message = "Saldo deve ser maior ou igual a zero")
    @Digits(integer = 17, fraction = 2, message = "Saldo deve ter no máximo 17 dígitos inteiros e 2 decimais")
    private BigDecimal balance;
    
    @NotNull(message = "Saldo disponível é obrigatório")
    @DecimalMin(value = "0.0", message = "Saldo disponível deve ser maior ou igual a zero")
    @Digits(integer = 17, fraction = 2, message = "Saldo disponível deve ter no máximo 17 dígitos inteiros e 2 decimais")
    private BigDecimal availableBalance;
    
    @NotNull(message = "Tipo de conta é obrigatório")
    private AccountType accountType;
    
    @NotNull(message = "Status da conta é obrigatório")
    private AccountStatus status;
    
    @NotBlank(message = "Nome do banco é obrigatório")
    @Size(max = 100, message = "Nome do banco não pode ter mais de 100 caracteres")
    private String bankName;
    
    @NotBlank(message = "Código da agência é obrigatório")
    @Size(max = 20, message = "Código da agência não pode ter mais de 20 caracteres")
    private String branchCode;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Validação customizada: saldo disponível não pode ser maior que saldo total.
     */
    @AssertTrue(message = "Saldo disponível não pode ser maior que o saldo total")
    public boolean isAvailableBalanceValid() {
        if (balance == null || availableBalance == null) {
            return true; // deixa outras validações cuidarem dos nulls
        }
        return availableBalance.compareTo(balance) <= 0;
    }
}
```

```java
// src/main/java/com/fujitsu/treasury/model/dto/TreasurySummaryDto.java
package com.fujitsu.treasury.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para resumos/relatórios de Treasury.
 * Usado em dashboards e APIs de sumários.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreasurySummaryDto {
    
    private String currency;
    private BigDecimal totalBalance;
    private BigDecimal totalAvailableBalance;
    private Long accountCount;
    private BigDecimal averageBalance;
}
```


### 3.3 Collateral DTOs

```java
// src/main/java/com/fujitsu/treasury/model/dto/CollateralDto.java
package com.fujitsu.treasury.model.dto;

import com.fujitsu.treasury.model.entity.CollateralStatus;
import com.fujitsu.treasury.model.entity.CollateralType;
import com.fujitsu.treasury.model.entity.Rating;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollateralDto {
    
    private Long id;
    
    @NotNull(message = "Tipo de collateral é obrigatório")
    private CollateralType collateralType;
    
    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 255, message = "Descrição não pode ter mais de 255 caracteres")
    private String description;
    
    @NotNull(message = "Valor de mercado é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor de mercado deve ser maior que zero")
    @Digits(integer = 17, fraction = 2, message = "Valor de mercado deve ter no máximo 17 dígitos inteiros e 2 decimais")
    private BigDecimal marketValue;
    
    @NotNull(message = "Haircut é obrigatório")
    @DecimalMin(value = "0.0", message = "Haircut deve ser maior ou igual a zero")
    @DecimalMax(value = "1.0", message = "Haircut deve ser menor ou igual a 1.0 (100%)")
    @Digits(integer = 1, fraction = 4, message = "Haircut deve ter formato 0.XXXX")
    private BigDecimal haircut;
    
    private BigDecimal eligibleValue; // Calculado automaticamente
    
    @NotBlank(message = "Moeda é obrigatória")
    @Size(min = 3, max = 3, message = "Moeda deve ter 3 caracteres (ISO 4217)")
    private String currency;
    
    @NotNull(message = "Rating é obrigatório")
    private Rating rating;
    
    @NotNull(message = "Data de vencimento é obrigatória")
    @Future(message = "Data de vencimento deve ser no futuro")
    private LocalDate maturityDate;
    
    @NotNull(message = "Status é obrigatório")
    private CollateralStatus status;
    
    @NotBlank(message = "Contraparte é obrigatória")
    @Size(max = 100, message = "Contraparte não pode ter mais de 100 caracteres")
    private String counterparty;
    
    @NotBlank(message = "Localização é obrigatória")
    @Size(max = 100, message = "Localização não pode ter mais de 100 caracteres")
    private String location;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

```java
// src/main/java/com/fujitsu/treasury/model/dto/CollateralSummaryDto.java
package com.fujitsu.treasury.model.dto;

import com.fujitsu.treasury.model.entity.CollateralType;
import com.fujitsu.treasury.model.entity.Rating;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollateralSummaryDto {
    
    private CollateralType collateralType;
    private Rating rating;
    private String currency;
    private BigDecimal totalMarketValue;
    private BigDecimal totalEligibleValue;
    private Long count;
    private BigDecimal averageHaircut;
}
```


### 3.4 Authentication DTOs

```java
// src/main/java/com/fujitsu/treasury/model/dto/LoginRequest.java
package com.fujitsu.treasury.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "Username é obrigatório")
    private String username;
    
    @NotBlank(message = "Password é obrigatória")
    private String password;
}
```

```java
// src/main/java/com/fujitsu/treasury/model/dto/AuthResponse.java
package com.fujitsu.treasury.model.dto;

import com.fujitsu.treasury.model.entity.Role;
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
```


### 3.5 Utility - Mappers

```java
// src/main/java/com/fujitsu/treasury/util/UserMapper.java
package com.fujitsu.treasury.util;

import com.fujitsu.treasury.model.dto.CreateUserDto;
import com.fujitsu.treasury.model.dto.UserDto;
import com.fujitsu.treasury.model.entity.User;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre User entity e DTOs.
 * Centraliza a lógica de mapeamento para facilitar manutenção.
 */
@Component
public class UserMapper {
    
    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        dto.setEnabled(user.getEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
    
    public User toEntity(CreateUserDto createDto) {
        if (createDto == null) {
            return null;
        }
        
        User user = new User();
        user.setUsername(createDto.getUsername());
        user.setPassword(createDto.getPassword()); // será encoded no service
        user.setEmail(createDto.getEmail());
        user.setFirstName(createDto.getFirstName());
        user.setLastName(createDto.getLastName());
        user.setRole(createDto.getRole());
        user.setEnabled(true); // default
        return user;
    }
    
    public User updateEntity(User existingUser, UserDto dto) {
        if (dto == null) {
            return existingUser;
        }
        
        existingUser.setEmail(dto.getEmail());
        existingUser.setFirstName(dto.getFirstName());
        existingUser.setLastName(dto.getLastName());
        existingUser.setRole(dto.getRole());
        existingUser.setEnabled(dto.getEnabled());
        // username e password não são atualizados via este DTO
        return existingUser;
    }
}
```


### 3.6 Teste dos DTOs

```java
// src/test/java/com/fujitsu/treasury/model/dto/UserDtoTest.java
package com.fujitsu.treasury.model.dto;

import com.fujitsu.treasury.model.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste das validações dos DTOs.
 * Verifica se as annotations de validação funcionam corretamente.
 */
class UserDtoTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    void shouldPassValidationWithValidData() {
        // Given
        UserDto userDto = new UserDto();
        userDto.setUsername("john.doe");
        userDto.setEmail("john.doe@fujitsu.com");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setRole(Role.TREASURY);
        userDto.setEnabled(true);
        
        // When
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        
        // Then
        assertThat(violations).isEmpty();
    }
    
    @Test
    void shouldFailValidationWithBlankUsername() {
        // Given
        UserDto userDto = new UserDto();
        userDto.setUsername(""); // blank
        userDto.setEmail("john.doe@fujitsu.com");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setRole(Role.TREASURY);
        
        // When
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Username é obrigatório");
    }
    
    @Test
    void shouldFailValidationWithInvalidEmail() {
        // Given
        UserDto userDto = new UserDto();
        userDto.setUsername("john.doe");
        userDto.setEmail("invalid-email"); // formato inválido
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setRole(Role.TREASURY);
        
        // When
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email deve ser válido");
    }
    
    @Test
    void shouldFailValidationWithLongUsername() {
        // Given
        UserDto userDto = new UserDto();
        userDto.setUsername("a".repeat(51)); // mais de 50 caracteres
        userDto.setEmail("john.doe@fujitsu.com");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setRole(Role.TREASURY);
        
        // When
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        
        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("Username deve ter entre 3 e 50 caracteres");
    }
}
```

**Execute os testes dos DTOs:**

```bash
mvn test -Dtest="*DtoTest"
```


## Fase 4: Criação dos Serviços (Business Logic)

### Por que Serviços agora?

A camada de serviço contém a lógica de negócio da aplicação[^5][^11]. Ela orquestra as operações entre repositórios, aplica regras de negócio, e serve como ponte entre controladores e dados. É aqui que implementamos as funcionalidades principais do sistema.

### 4.1 User Service

```java
// src/main/java/com/fujitsu/treasury/service/UserService.java
package com.fujitsu.treasury.service;

import com.fujitsu.treasury.model.dto.CreateUserDto;
import com.fujitsu.treasury.model.dto.UserDto;
import com.fujitsu.treasury.model.entity.Role;
import com.fujitsu.treasury.model.entity.User;
import com.fujitsu.treasury.repository.UserRepository;
import com.fujitsu.treasury.util.UserMapper;
import com.fujitsu.treasury.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço para gestão de utilizadores.
 * Contém a lógica de negócio para operações de utilizadores.
 * 
 * @Service - Indica que é um bean de serviço do Spring
 * @Transactional - Garante consistência transacional nas operações
 * @RequiredArgsConstructor - Lombok: construtor com campos final
 * @Slf4j - Lombok: adiciona logger
 * 
 * Implementa UserDetailsService para integração com Spring Security
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Implementação do UserDetailsService para Spring Security.
     * Carrega utilizador para autenticação.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Carregando utilizador: {}", username);
        
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Utilizador não encontrado: " + username));
    }
    
    /**
     * Lista todos os utilizadores com paginação.
     * @Transactional(readOnly = true) - Otimização para operações só de leitura
     */
    @Transactional(readOnly = true)
    public Page<UserDto> findAll(Pageable pageable) {
        log.debug("Listando utilizadores com paginação: {}", pageable);
        
        return userRepository.findAll(pageable)
            .map(userMapper::toDto);
    }
    
    /**
     * Busca utilizador por ID.
     */
    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        log.debug("Buscando utilizador por ID: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Utilizador não encontrado com ID: " + id));
        
        return userMapper.toDto(user);
    }
    
    /**
     * Cria novo utilizador.
     * Aplica regras de negócio:
     * - Username deve ser único
     * - Email deve ser único
     * - Password é encriptada
     */
    public UserDto createUser(CreateUserDto createDto) {
        log.debug("Criando utilizador: {}", createDto.getUsername());
        
        // Validar unicidade do username
        if (userRepository.existsByUsername(createDto.getUsername())) {
            throw new BusinessException("Username já existe: " + createDto.getUsername());
        }
        
        // Validar unicidade do email
        if (userRepository.existsByEmail(createDto.getEmail())) {
            throw new BusinessException("Email já existe: " + createDto.getEmail());
        }
        
        // Converter DTO para entidade
        User user = userMapper.toEntity(createDto);
        
        // Encriptar password
        user.setPassword(passwordEncoder.encode(createDto.getPassword()));
        
        // Salvar na base de dados
        User savedUser = userRepository.save(user);
        
        log.info("Utilizador criado com sucesso: {}", savedUser.getUsername());
        return userMapper.toDto(savedUser);
    }
    
    /**
     * Atualiza utilizador existente.
     */
    public UserDto updateUser(Long id, UserDto userDto) {
        log.debug("Atualizando utilizador ID: {}", id);
        
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Utilizador não encontrado com ID: " + id));
        
        // Validar email único (exceto para o próprio utilizador)
        if (!existingUser.getEmail().equals(userDto.getEmail()) && 
            userRepository.existsByEmail(userDto.getEmail())) {
            throw new BusinessException("Email já existe: " + userDto.getEmail());
        }
        
        // Atualizar campos
        User updatedUser = userMapper.updateEntity(existingUser, userDto);
        User savedUser = userRepository.save(updatedUser);
        
        log.info("Utilizador atualizado com sucesso: {}", savedUser.getUsername());
        return userMapper.toDto(savedUser);
    }
    
    /**
     * Elimina utilizador.
     * Regra de negócio: não permite eliminar admins se for o último.
     */
    public void deleteUser(Long id) {
        log.debug("Eliminando utilizador ID: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Utilizador não encontrado com ID: " + id));
        
        // Validar se não é o último admin
        if (user.getRole() == Role.ADMIN) {
            long adminCount = userRepository.countByRole(Role.ADMIN);
            if (adminCount <= 1) {
                throw new BusinessException("Não é possível eliminar o último administrador");
            }
        }
        
        userRepository.delete(user);
        log.info("Utilizador eliminado com sucesso: {}", user.getUsername());
    }
    
    /**
     * Lista utilizadores por role.
     */
    @Transactional(readOnly = true)
    public List<UserDto> findByRole(Role role) {
        log.debug("Buscando utilizadores por role: {}", role);
        
        return userRepository.findByRole(role).stream()
            .map(userMapper::toDto)
            .toList();
    }
    
    /**
     * Ativa/desativa utilizador.
     */
    public UserDto toggleUserStatus(Long id) {
        log.debug("Alternando status do utilizador ID: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Utilizador não encontrado com ID: " + id));
        
        user.setEnabled(!user.getEnabled());
        User savedUser = userRepository.save(user);
        
        log.info("Status do utilizador {} alterado para: {}", user.getUsername(), user.getEnabled());
        return userMapper.toDto(savedUser);
    }
    
    /**
     * Altera password do utilizador.
     */
    public void changePassword(Long id, String oldPassword, String newPassword) {
        log.debug("Alterando password do utilizador ID: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Utilizador não encontrado com ID: " + id));
        
        // Verificar password atual
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("Password atual incorreta");
        }
        
        // Validar nova password
        if (newPassword.length() < 8) {
            throw new BusinessException("Nova password deve ter pelo menos 8 caracteres");
        }
        
        // Atualizar password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        log.info("Password alterada com sucesso para utilizador: {}", user.getUsername());
    }
}
```


### 4.2 Treasury Service

```java
// src/main/java/com/fujitsu/treasury/service/TreasuryService.java
package com.fujitsu.treasury.service;

import com.fujitsu.treasury.model.dto.TreasuryDto;
import com.fujitsu.treasury.model.dto.TreasurySummaryDto;
import com.fujitsu.treasury.model.entity.AccountStatus;
import com.fujitsu.treasury.model.entity.Treasury;
import com.fujitsu.treasury.repository.TreasuryRepository;
import com.fujitsu.treasury.util.TreasuryMapper;
import com.fujitsu.treasury.exception.BusinessException;
import com.fujitsu.treasury.kafka.TreasuryEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Serviço para gestão de contas Treasury.
 * Implementa regras de negócio específicas para gestão financeira.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TreasuryService {
    
    private final TreasuryRepository treasuryRepository;
    private final TreasuryMapper treasuryMapper;
    private final TreasuryEventProducer eventProducer;
    
    @Transactional(readOnly = true)
    public Page<TreasuryDto> findAll(Pageable pageable) {
        log.debug("Listando contas treasury com paginação: {}", pageable);
        
        return treasuryRepository.findAll(pageable)
            .map(treasuryMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public TreasuryDto findById(Long id) {
        log.debug("Buscando conta treasury por ID: {}", id);
        
        Treasury treasury = treasuryRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Conta treasury não encontrada com ID: " + id));
        
        return treasuryMapper.toDto(treasury);
    }
    
    @Transactional(readOnly = true)
    public TreasuryDto findByAccountNumber(String accountNumber) {
        log.debug("Buscando conta treasury por número: {}", accountNumber);
        
        Treasury treasury = treasuryRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new BusinessException("Conta treasury não encontrada: " + accountNumber));
        
        return treasuryMapper.toDto(treasury);
    }
    
    /**
     * Cria nova conta treasury.
     * Regras de negócio:
     * - Número da conta deve ser único
     * - Saldo disponível não pode ser maior que saldo total
     * - Moeda deve ser válida (ISO 4217)
     */
    public TreasuryDto createTreasury(TreasuryDto treasuryDto) {
        log.debug("Criando conta treasury: {}", treasuryDto.getAccountNumber());
        
        // Validar unicidade do número da conta
        if (treasuryRepository.existsByAccountNumber(treasuryDto.getAccountNumber())) {
            throw new BusinessException("Número da conta já existe: " + treasuryDto.getAccountNumber());
        }
        
        // Validar consistência dos saldos
        validateBalances(treasuryDto);
        
        // Validar moeda
        validateCurrency(treasuryDto.getCurrency());
        
        Treasury treasury = treasuryMapper.toEntity(treasuryDto);
        Treasury savedTreasury = treasuryRepository.save(treasury);
        
        // Publicar evento via Kafka
        eventProducer.publishTreasuryCreated(savedTreasury);
        
        log.info("Conta treasury criada com sucesso: {}", savedTreasury.getAccountNumber());
        return treasuryMapper.toDto(savedTreasury);
    }
    
    public TreasuryDto updateTreasury(Long id, TreasuryDto treasuryDto) {
        log.debug("Atualizando conta treasury ID: {}", id);
        
        Treasury existingTreasury = treasuryRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Conta treasury não encontrada com ID: " + id));
        
        // Validar se mudança de número de conta é permitida
        if (!existingTreasury.getAccountNumber().equals(treasuryDto.getAccountNumber()) &&
            treasuryRepository.existsByAccountNumber(treasuryDto.getAccountNumber())) {
            throw new BusinessException("Número da conta já existe: " + treasuryDto.getAccountNumber());
        }
        
        validateBalances(treasuryDto);
        
        Treasury updatedTreasury = treasuryMapper.updateEntity(existingTreasury, treasuryDto);
        Treasury savedTreasury = treasuryRepository.save(updatedTreasury);
        
        // Publicar evento
        eventProducer.publishTreasuryUpdated(savedTreasury);
        
        log.info("Conta treasury atualizada com sucesso: {}", savedTreasury.getAccountNumber());
        return treasuryMapper.toDto(savedTreasury);
    }
    
    /**
     * Transferência entre contas.
     * Implementa regras de negócio para movimentação de fundos.
     */
    public void transferFunds(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        log.debug("Transferindo {} de {} para {}", amount, fromAccountNumber, toAccountNumber);
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Valor da transferência deve ser positivo");
        }
        
        Treasury fromAccount = treasuryRepository.findByAccountNumber(fromAccountNumber)
            .orElseThrow(() -> new BusinessException("Conta de origem não encontrada: " + fromAccountNumber));
        
        Treasury toAccount = treasuryRepository.findByAccountNumber(toAccountNumber)
            .orElseThrow(() -> new BusinessException("Conta de destino não encontrada: " + toAccountNumber));
        
        // Validar status das contas
        if (fromAccount.getStatus() != AccountStatus.ACTIVE || toAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException("Ambas as contas devem estar ativas para transferência");
        }
        
        // Validar moedas
        if (!fromAccount.getCurrency().equals(toAccount.getCurrency())) {
            throw new BusinessException("Transferências entre moedas diferentes não são suportadas");
        }
        
        // Validar saldo disponível
        if (fromAccount.getAvailableBalance().compareTo(amount) < 0) {
            throw new BusinessException("Saldo disponível insuficiente na conta de origem");
        }
        
        // Executar transferência
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        fromAccount.setAvailableBalance(fromAccount.getAvailableBalance().subtract(amount));
        
        toAccount.setBalance(toAccount.getBalance().add(amount));
        toAccount.setAvailableBalance(toAccount.getAvailableBalance().add(amount));
        
        treasuryRepository.save(fromAccount);
        treasuryRepository.save(toAccount);
        
        // Publicar eventos
        eventProducer.publishFundsTransferred(fromAccount, toAccount, amount);
        
        log.info("Transferência de {} executada com sucesso de {} para {}", 
                amount, fromAccountNumber, toAccountNumber);
    }
    
    /**
     * Bloqueia/desbloqueia fundos para operações.
     */
    public void reserveFunds(String accountNumber, BigDecimal amount) {
        log.debug("Reservando {} fundos na conta {}", amount, accountNumber);
        
        Treasury treasury = treasuryRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new BusinessException("Conta não encontrada: " + accountNumber));
        
        if (treasury.getAvailableBalance().compareTo(amount) < 0) {
            throw new BusinessException("Saldo disponível insuficiente para reserva");
        }
        
        treasury.setAvailableBalance(treasury.getAvailableBalance().subtract(amount));
        treasuryRepository.save(treasury);
        
        log.info("Fundos reservados com sucesso: {} na conta {}", amount, accountNumber);
    }
    
    /**
     * Relatórios e analytics.
     */
    @Transactional(readOnly = true)
    public List<TreasurySummaryDto> getTreasurySummaryByCurrency() {
        log.debug("Gerando resumo de treasury por moeda");
        
        List<Object[]> results = treasuryRepository.getTreasurySummaryByCurrency();
        
        return results.stream()
            .map(result -> new TreasurySummaryDto(
                (String) result[^0],           // currency
                (BigDecimal) result[^1],       // totalBalance
                BigDecimal.ZERO,              // totalAvailableBalance (calcular separadamente se necessário)
                (Long) result[^2],             // accountCount
                ((BigDecimal) result[^1]).divide(BigDecimal.valueOf((Long) result[^2]), 2, BigDecimal.ROUND_HALF_UP) // averageBalance
            ))
            .toList();
    }
    
    @Transactional(readOnly = true)
    public List<TreasuryDto> getLowBalanceAccounts(BigDecimal threshold) {
        log.debug("Buscando contas com saldo baixo (< {})", threshold);
        
        return treasuryRepository.findLowBalanceAccounts(threshold).stream()
            .map(treasuryMapper::toDto)
            .toList();
    }
    
    public void deleteTreasury(Long id) {
        log.debug("Eliminando conta treasury ID: {}", id);
        
        Treasury treasury = treasuryRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Conta treasury não encontrada com ID: " + id));
        
        // Validar se pode ser eliminada (ex: saldo zero)
        if (treasury.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessException("Não é possível eliminar conta com saldo diferente de zero");
        }
        
        treasuryRepository.delete(treasury);
        
        // Publicar evento
        eventProducer.publishTreasuryDeleted(treasury);
        
        log.info("Conta treasury eliminada com sucesso: {}", treasury.getAccountNumber());
    }
    
    // Métodos privados de validação
    private void validateBalances(TreasuryDto dto) {
        if (dto.getAvailableBalance().compareTo(dto.getBalance()) > 0) {
            throw new BusinessException("Saldo disponível não pode ser maior que o saldo total");
        }
    }
    
    private void validateCurrency(String currency) {
        // Lista básica de moedas suportadas (em produção, usar serviço externo)
        List<String> supportedCurrencies = List.of("EUR", "USD", "GBP", "JPY", "CHF");
        if (!supportedCurrencies.contains(currency)) {
            throw new BusinessException("Moeda não suportada: " + currency);
        }
    }
}
```


### 4.3 Teste dos Serviços (TDD Approach)

```java
// src/test/java/com/fujitsu/treasury/service/UserServiceTest.java
package com.fujitsu.treasury.service;

import com.fujitsu.treasury.model.dto.CreateUserDto;
import com.fujitsu.treasury.model.dto.UserDto;
import com.fujitsu.treasury.model.entity.Role;
import com.fujitsu.treasury.model.entity.User;
import com.fujitsu.treasury.repository.UserRepository;
import com.fujitsu.treasury.util.UserMapper;
import com.fujitsu.treasury.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Teste unitário do UserService.
 * Usa Mockito para mockar dependências e isolar a lógica de negócio.
 * 
 * @ExtendWith(MockitoExtension.class) - Ativa o Mockito para JUnit 5
 * @Mock - Cria mock das dependências
 * @InjectMocks - Injeta os mocks na classe a testar
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    private User testUser;
    private UserDto testUserDto;
    private CreateUserDto createUserDto;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test.user");
        testUser.setEmail("test@fujitsu.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(Role.TREASURY);
        testUser.setEnabled(true);
        
        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setUsername("test.user");
        testUserDto.setEmail("test@fujitsu.com");
        testUserDto.setFirstName("Test");
        testUserDto.setLastName("User");
        testUserDto.setRole(Role.TREASURY);
        testUserDto.setEnabled(true);
        
        createUserDto = new CreateUserDto();
        createUserDto.setUsername("new.user");
        createUserDto.setPassword("password123");
        createUserDto.setEmail("new@fujitsu.com");
        createUserDto.setFirstName("New");
        createUserDto.setLastName("User");
        createUserDto.setRole(Role.USER);
    }
    
    @Test
    void shouldFindAllUsersWithPagination() {
        // Given
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<User> users = List.of(testUser);
        Page<User> userPage = new PageImpl<>(users, pageRequest, 1);
        
        when(userRepository.findAll(pageRequest)).thenReturn(userPage);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);
        
        // When
        Page<UserDto> result = userService.findAll(pageRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("test.user");
        
        verify(userRepository).findAll(pageRequest);
        verify(userMapper).toDto(testUser);
    }
    
    @Test
    void shouldFindUserById() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);
        
        // When
        UserDto result = userService.findById(1L);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("test.user");
        
        verify(userRepository).findById(1L);
        verify(userMapper).toDto(testUser);
    }
    
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> userService.findById(999L))
            .isInstanceOf(BusinessException.class)
            .hasMessage("Utilizador não encontrado com ID: 999");
        
        verify(userRepository).findById(999L);
        verify(userMapper, never()).toDto(any());
    }
    
    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        when(userRepository.existsByUsername(createUserDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(createUserDto.getEmail())).thenReturn(false);
        when(userMapper.toEntity(createUserDto)).thenReturn(testUser);
        when(passwordEncoder.encode(createUserDto.getPassword())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);
        
        // When
        UserDto result = userService.createUser(createUserDto);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("test.user");
        
        verify(userRepository).existsByUsername(createUserDto.getUsername());
        verify(userRepository).existsByEmail(createUserDto.getEmail());
        verify(passwordEncoder).encode(createUserDto.getPassword());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        // Given
        when(userRepository.existsByUsername(createUserDto.getUsername())).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> userService.createUser(createUserDto))
            .isInstanceOf(BusinessException.class)
            .hasMessage("Username já existe: " + createUserDto.getUsername());
        
        verify(userRepository).existsByUsername(createUserDto.getUsername());
        verify(userRepository, never()).save(any());
    }
    
    @Test
    void shouldDeleteUserWhenNotLastAdmin() {
        // Given
        testUser.setRole(Role.USER); // não admin
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // When
        userService.deleteUser(1L);
        
        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).delete(testUser);
        verify(userRepository, never()).countByRole(any()); // não verifica count para não-admin
    }
    
    @Test
    void shouldThrowExceptionWhenDeletingLastAdmin() {
        // Given
        testUser.setRole(Role.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(1L); // último admin
        
        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(1L))
            .isInstanceOf(BusinessException.class)
            .hasMessage("Não é possível eliminar o último administrador");
        
        verify(userRepository).findById(1L);
        verify(userRepository).countByRole(Role.ADMIN);
        verify(userRepository, never()).delete(any());
    }
    
    @Test
    void shouldToggleUserStatus() {
        // Given
        testUser.setEnabled(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);
        
        // When
        UserDto result = userService.toggleUserStatus(1L);
        
        // Then
        assertThat(testUser.getEnabled()).isFalse(); // status foi alterado
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }
}
```

**Execute os testes dos serviços:**

```bash
mvn test -Dtest="*ServiceTest"
```


## Fase 5: Criação dos Controladores REST

### Por que Controladores por último?

Os controladores são a camada de apresentação[^7][^5]. Eles recebem requests HTTP, chamam os serviços apropriados e retornam responses. São criados por último porque dependem de todas as outras camadas estarem funcionais.

### 5.1 Base Controller e Exception Handling

```java
// src/main/java/com/fujitsu/treasury/exception/BusinessException.java
package com.fujitsu.treasury.exception;

/**
 * Exceção para erros de lógica de negócio.
 * Usada quando regras de negócio são violadas.
 */
public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

```java
// src/main/java/com/fujitsu/treasury/exception/GlobalExceptionHandler.java
package com.fujitsu.treasury.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler global para tratamento de exceções.
 * Centraliza o tratamento de erros em toda a aplicação.
 * 
 * @RestControllerAdvice - Aplica-se a todos os controllers REST
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * Trata erros de validação (Bean Validation).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("Erro de validação: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Erro de validação",
            errors.toString(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Trata erros de lógica de negócio.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        log.warn("Erro de negócio: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Erro de negócio",
            ex.getMessage(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Trata erros gerais não capturados.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Erro interno: ", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Erro interno do servidor",
            "Ocorreu um erro inesperado. Contacte o suporte.",
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Classe para resposta de erro padronizada.
     */
    public static class ErrorResponse {
        private String error;
        private String message;
        private LocalDateTime timestamp;
        
        public ErrorResponse(String error, String message, LocalDateTime timestamp) {
            this.error = error;
            this.message = message;
            this.timestamp = timestamp;
        }
        
        // getters
        public String getError() { return error; }
        public String getMessage() { return message; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}
```


### 5.2 User Controller

```java
// src/main/java/com/fujitsu/treasury/controller/UserController.java
package com.fujitsu.treasury.controller;

import com.fujitsu.treasury.model.dto.CreateUserDto;
import com.fujitsu.treasury.model.dto.UserDto;
import com.fujitsu.treasury.model.entity.Role;
import com.fujitsu.treasury.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller REST para gestão de utilizadores.
 * Define endpoints para operações CRUD de utilizadores.
 * 
 * @RestController - Combina @Controller + @ResponseBody
 * @RequestMapping - Define URL base para este controller
 * @RequiredArgsConstructor - Injeção de dependências via construtor
 * @Validated - Ativa validação a nível de classe
 * 
 * Segurança:
 * - Endpoints protegidos por roles usando @PreAuthorize
 * - Apenas ADMIN pode criar/deletar utilizadores
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    
    private final UserService userService;
    
    /**
     * Lista todos os utilizadores com paginação.
     * GET /api/users?page=0&size=10&sort=username
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY')")
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @PageableDefault(size = 20, sort = "username") Pageable pageable) {
        
        log.debug("Recebido pedido para listar utilizadores: {}", pageable);
        
        Page<UserDto> users = userService.findAll(pageable);
        return ResponseEntity.ok(users);
    }
    
    /**
     * Busca utilizador por ID.
     * GET /api/users/1
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#id)")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        log.debug("Recebido pedido para buscar utilizador ID: {}", id);
        
        UserDto user = userService.findById(id);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Cria novo utilizador.
     * POST /api/users
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        log.debug("Recebido pedido para criar utilizador: {}", createUserDto.getUsername());
        
        UserDto createdUser = userService.createUser(createUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    /**
     * Atualiza utilizador existente.
     * PUT /api/users/1
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#id)")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id, 
            @Valid @RequestBody UserDto userDto) {
        
        log.debug("Recebido pedido para atualizar utilizador ID: {}", id);
        
        UserDto updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * Elimina utilizador.
     * DELETE /api/users/1
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.debug("Recebido pedido para eliminar utilizador ID: {}", id);
        
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Lista utilizadores por role.
     * GET /api/users/by-role/TREASURY
     */
    @GetMapping("/by-role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getUsersByRole(@PathVariable Role role) {
        log.debug("Recebido pedido para listar utilizadores por role: {}", role);
        
        List<UserDto> users = userService.findByRole(role);
        return ResponseEntity.ok(users);
    }
    
    /**
     * Ativa/desativa utilizador.
     * PATCH /api/users/1/toggle-status
     */
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> toggleUserStatus(@PathVariable Long id) {
        log.debug("Recebido pedido para alternar status do utilizador ID: {}", id);
        
        UserDto updatedUser = userService.toggleUserStatus(id);
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * Altera password do utilizador.
     * POST /api/users/1/change-password
     */
    @PostMapping("/{id}/change-password")
    @PreAuthorize("hasRole('ADMIN') or @userService.isCurrentUser(#id)")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id, 
            @RequestBody @Valid ChangePasswordRequest request) {
        
        log.debug("Recebido pedido para alterar password do utilizador ID: {}", id);
        
        userService.changePassword(id, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }
    
    /**
     * DTO para request de mudança de password.
     */
    public static class ChangePasswordRequest {
        @jakarta.validation.constraints.NotBlank(message = "Password atual é obrigatória")
        private String oldPassword;
        
        @jakarta.validation.constraints.NotBlank(message = "Nova password é obrigatória")
        @jakarta.validation.constraints.Size(min = 8, message = "Nova password deve ter pelo menos 8 caracteres")
        private String newPassword;
        
        // getters and setters
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
```


### 5.3 Treasury Controller

```java
// src/main/java/com/fujitsu/treasury/controller/TreasuryController.java
package com.fujitsu.treasury.controller;

import com.fujitsu.treasury.model.dto.TreasuryDto;
import com.fujitsu.treasury.model.dto.TreasurySummaryDto;
import com.fujitsu.treasury.service.TreasuryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller para operações de Treasury.
 * Endpoints para gestão de contas e operações financeiras.
 */
@RestController
@RequestMapping("/api/treasury")
@RequiredArgsConstructor
@Slf4j
@Validated
public class TreasuryController {
    
    private final TreasuryService treasuryService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY')")
    public ResponseEntity<Page<TreasuryDto>> getAllTreasury(
            @PageableDefault(size = 20, sort = "accountNumber") Pageable pageable) {
        
        log.debug("Recebido pedido para listar contas treasury: {}", pageable);
        
        Page<TreasuryDto> treasuries = treasuryService.findAll(pageable);
        return ResponseEntity.ok(treasuries);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY')")
    public ResponseEntity<TreasuryDto> getTreasuryById(@PathVariable Long id) {
        log.debug("Recebido pedido para buscar conta treasury ID: {}", id);
        
        TreasuryDto treasury = treasuryService.findById(id);
        return ResponseEntity.ok(treasury);
    }
    
    @GetMapping("/account/{accountNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY')")
    public ResponseEntity<TreasuryDto> getTreasuryByAccountNumber(@PathVariable String accountNumber) {
        log.debug("Recebido pedido para buscar conta treasury: {}", accountNumber);
        
        TreasuryDto treasury = treasuryService.findByAccountNumber(accountNumber);
        return ResponseEntity.ok(treasury);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY')")
    public ResponseEntity<TreasuryDto> createTreasury(@Valid @RequestBody TreasuryDto treasuryDto) {
        log.debug("Recebido pedido para criar conta treasury: {}", treasuryDto.getAccountNumber());
        
        TreasuryDto createdTreasury = treasuryService.createTreasury(treasuryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTreasury);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY')")
    public ResponseEntity<TreasuryDto> updateTreasury(
            @PathVariable Long id, 
            @Valid @RequestBody TreasuryDto treasuryDto) {
        
        log.debug("Recebido pedido para atualizar conta treasury ID: {}", id);
        
        TreasuryDto updatedTreasury = treasuryService.updateTreasury(id, treasuryDto);
        return ResponseEntity.ok(updatedTreasury);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTreasury(@PathVariable Long id) {
        log.debug("Recebido pedido para eliminar conta treasury ID: {}", id);
        
        treasuryService.deleteTreasury(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Transferência entre contas.
     * POST /api/treasury/transfer
     */
    @PostMapping("/transfer")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY')")
    public ResponseEntity<Void> transferFunds(@Valid @RequestBody TransferRequest request) {
        log.debug("Recebido pedido de transferência de {} para {}", 
                request.getFromAccount(), request.getToAccount());
        
        treasuryService.transferFunds(
            request.getFromAccount(), 
            request.getToAccount(), 
            request.getAmount());
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * Reserva de fundos.
     * POST /api/treasury/reserve
     */
    @PostMapping("/reserve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY')")
    public ResponseEntity<Void> reserveFunds(@Valid @RequestBody ReserveRequest request) {
        log.debug("Recebido pedido para reservar {} na conta {}", 
                request.getAmount(), request.getAccountNumber());
        
        treasuryService.reserveFunds(request.getAccountNumber(), request.getAmount());
        return ResponseEntity.ok().build();
    }
    
    /**
     * Relatórios e análises.
     */
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY')")
    public ResponseEntity<List<TreasurySummaryDto>> getTreasurySummary() {
        log.debug("Recebido pedido para relatório de treasury");
        
        List<TreasurySummaryDto> summary = treasuryService.getTreasurySummaryByCurrency();
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/low-balance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY')")
    public ResponseEntity<List<TreasuryDto>> getLowBalanceAccounts(
            @RequestParam @DecimalMin(value = "0.01", message = "Threshold deve ser positivo") BigDecimal threshold) {
        
        log.debug("Recebido pedido para contas com saldo baixo (< {})", threshold);
        
        List<TreasuryDto> lowBalanceAccounts = treasuryService.getLowBalanceAccounts(threshold);
        return ResponseEntity.ok(lowBalanceAccounts);
    }
    
    // DTOs para requests
    public static class TransferRequest {
        @NotBlank(message = "Conta de origem é obrigatória")
        private String fromAccount;
        
        @NotBlank(message = "Conta de destino é obrigatória")
        private String toAccount;
        
        @NotNull(message = "Valor é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor deve ser positivo")
        private BigDecimal amount;
        
        // getters and setters
        public String getFromAccount() { return fromAccount; }
        public void setFromAccount(String fromAccount) { this.fromAccount = fromAccount; }
        public String getToAccount() { return toAccount; }
        public void setToAccount(String toAccount) { this.toAccount = toAccount; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }
    
    public static class ReserveRequest {
        @NotBlank(message = "Número da conta é obrigatório")
        private String accountNumber;
        
        @NotNull(message = "Valor é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor deve ser positivo")
        private BigDecimal amount;
        
        // getters and setters
        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }
}
```


### 5.4 Teste dos Controllers (Integration Test)

```java
// src/test/java/com/fujitsu/treasury/controller/UserControllerTest.java
package com.fujitsu.treasury.controller;

import com.fujitsu.treasury.model.dto.CreateUserDto;
import com.fujitsu.treasury.model.dto.UserDto;
import com.fujitsu.treasury.model.entity.Role;
import com.fujitsu.treasury.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Teste de integração do Controller.
 * @WebMvcTest carrega apenas a camada web, não a aplicação completa.
 * - Mais rápido que @SpringBootTest
 * - Testa apenas a camada de apresentação
 * - Services são mockados com @MockBean
 */
@WebMvcTest(UserController.class)
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper; // Para serialização JSON
    
    @MockBean
    private UserService userService;
    
    @Test
    @WithMockUser(roles = "ADMIN") // Simula utilizador autenticado com role ADMIN
    void shouldGetAllUsers() throws Exception {
        // Given
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("test.user");
        userDto.setEmail("test@fujitsu.com");
        userDto.setRole(Role.TREASURY);
        
        Page<UserDto> userPage = new PageImpl<>(List.of(userDto), PageRequest.of(0, 20), 1);
        when(userService.findAll(any())).thenReturn(userPage);
        
        // When & Then
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[^0].username").value("test.user"))
                .andExpect(jsonPath("$.content[^0].email").value("test@fujitsu.com"));
        
        verify(userService).findAll(any());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateUser() throws Exception {
        // Given
        CreateUserDto createDto = new CreateUserDto();
        createDto.setUsername("new.user");
        createDto.setPassword("password123");
        createDto.setEmail("new@fujitsu.com");
        createDto.setFirstName("New");
        createDto.setLastName("User");
        createDto.setRole(Role.USER);
        
        UserDto createdUser = new UserDto();
        createdUser.setId(1L);
        createdUser.setUsername("new.user");
        createdUser.setEmail("new@fujitsu.com");
        createdUser.setRole(Role.USER);
        
        when(userService.createUser(any(CreateUserDto.class))).thenReturn(createdUser);
        
        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("new.user"))
                .andExpect(jsonPath("$.email").value("new@fujitsu.com"));
        
        verify(userService).createUser(any(CreateUserDto.class));
    }
    
    @Test
    @WithMockUser(roles = "USER") // Role insuficiente para criar utilizadores
    void shouldReturnForbiddenWhenInsufficientRole() throws Exception {
        // Given
        CreateUserDto createDto = new CreateUserDto();
        createDto.setUsername("new.user");
        createDto.setPassword("password123");
        createDto.setEmail("new@fujitsu.com");
        createDto.setFirstName("New");
        createDto.setLastName("User");
        createDto.setRole(Role.USER);
        
        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andDo(print())
                .andExpect(status().isForbidden()); // 403 Forbidden
        
        verify(userService, never()).createUser(any());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldValidateCreateUserRequest() throws Exception {
        // Given - DTO inválido (campos obrigatórios em falta)
        CreateUserDto invalidDto = new CreateUserDto();
        invalidDto.setUsername(""); // blank
        // outros campos em falta
        
        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400 Bad Request
                .andExpect(jsonPath("$.error").value("Erro de validação"));
        
        verify(userService, never()).createUser(any());
    }
    
    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        // When & Then - sem @WithMockUser
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized()); // 401 Unauthorized
        
        verify(userService, never()).findAll(any());
    }
}
```

**Execute os testes dos controladores:**

```bash
mvn test -Dtest="*ControllerTest"
```


## Fase 6 — Integração de Mensageria com Apache Kafka

### Visão Geral

Nesta etapa concluiremos a **parte 6.1** iniciada na resposta anterior e avançaremos até **6.8**, cobrindo todos os ficheiros, testes e boas-práticas ligados a eventos de mensageria. No final terá um subsistema Kafka robusto, totalmente testado, documentado e pronto para produção.

### 6.1 Configuração Completa do Kafka (`KafkaConfig.java`)

A classe apresentada antes continha apenas as beans principais. Agora incluiremos–lhe:

- Serializadores/deserializadores de alta performance.
- Tratamento de falhas (retry/back-off).
- Lista de tópicos auto-criados para DEV.

```java
// ...imports omitidos para brevidade
@Configuration
@EnableKafka
public class KafkaConfig {

    // -------- PRODUCER --------
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.ACKS_CONFIG, "all");                  // garante durabilidade
        props.put(ProducerConfig.RETRIES_CONFIG, 3);                   // tentativas em caso de falha
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        KafkaTemplate<String, Object> template = new KafkaTemplate<>(producerFactory());
        template.setDefaultTopic("treasury-events");                   // fallback
        return template;
    }

    // -------- CONSUMER --------
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "treasury-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.fujitsu.treasury.kafka");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.fujitsu.treasury.kafka.TreasuryEvent");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);                                     // paralelismo
        factory.setCommonErrorHandler(new DefaultErrorHandler(
            new ExponentialBackOff(1_000L, 2)));                       // retry exponencial
        return factory;
    }

    // -------- ADMIN --------
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic treasuryEventsTopic() {
        return TopicBuilder.name("treasury-events")
                           .partitions(3)
                           .replicas(1)
                           .build();
    }
}
```


#### Porquê estes parâmetros?

- **`acks=all`** garante que cada mensagem é replicada antes de o produtor receber confirmação.
- **Serialização JSON** simplifica compatibilidade entre micro-serviços.
- **Retry + Back-off** evita perder mensagens quando o broker está momentaneamente indisponível.
- **Admin API** cria tópicos de desenvolvimento automaticamente, evitando erros de “topic not found”.


### 6.2 Modelo de Evento (`TreasuryEvent.java`)

```java
package com.fujitsu.treasury.kafka;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TreasuryEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long   treasuryId;
    private String accountNumber;
    private String eventType;   // CREATED, UPDATED, DELETED, FUNDS_TRANSFERRED
    private String payload;     // JSON compacto da entidade ou info extra
    private Instant timestamp;  // para auditoria

    public static TreasuryEvent of(Long id, String acc, String type, String payload) {
        return TreasuryEvent.builder()
                            .treasuryId(id)
                            .accountNumber(acc)
                            .eventType(type)
                            .payload(payload)
                            .timestamp(Instant.now())
                            .build();
    }
}
```


#### Regras de design

- **Imutabilidade**: utilizamos `@Builder` + `@AllArgsConstructor` para facilitar criação, evitando setters mutáveis.
- **Serializable**: garante compatibilidade com outros consumidores que usem frameworks genéricos de deserialização.


### 6.3 Producer de Eventos (`TreasuryEventProducer.java`)

```java
package com.fujitsu.treasury.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TreasuryEventProducer {

    private static final String TOPIC = "treasury-events";

    private final KafkaTemplate<String, Object> template;

    public void publishTreasuryCreated(Long id, String accountNumber) {
        sendEvent(TreasuryEvent.of(id, accountNumber, "CREATED", null));
    }

    public void publishTreasuryUpdated(Long id, String accountNumber) {
        sendEvent(TreasuryEvent.of(id, accountNumber, "UPDATED", null));
    }

    public void publishTreasuryDeleted(Long id, String accountNumber) {
        sendEvent(TreasuryEvent.of(id, accountNumber, "DELETED", null));
    }

    public void publishFundsTransferred(Long fromId, String fromAcc,
                                         Long toId, String toAcc, String jsonPayload) {
        sendEvent(TreasuryEvent.of(fromId, fromAcc, "FUNDS_TRANSFERRED", jsonPayload));
        sendEvent(TreasuryEvent.of(toId,   toAcc,   "FUNDS_TRANSFERRED", jsonPayload));
    }

    // ---------- utilitário privado ----------
    private void sendEvent(TreasuryEvent event) {
        template.send(TOPIC, event.getAccountNumber(), event)
                .addCallback(
                    result -> log.info("Evento [{}] publicado para p={}",
                                       event.getEventType(), event.getAccountNumber()),
                    ex     -> log.error("Falha ao publicar evento {}", event, ex));
    }
}
```

**Decisões**

- Chave de partição = `accountNumber`, mantendo ordem de eventos por conta.
- Callbacks de sucesso/erro para visibilidade operacional.


### 6.4 Consumer de Eventos (`TreasuryEventConsumer.java`)

```java
package com.fujitsu.treasury.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TreasuryEventConsumer {

    @KafkaListener(topics = "treasury-events",
                   groupId = "treasury-group",
                   containerFactory = "kafkaListenerContainerFactory")
    public void listen(TreasuryEvent event) {
        log.info("🚚  Evento recebido: {}", event);

        switch (event.getEventType()) {
            case "CREATED" -> handleCreated(event);
            case "UPDATED" -> handleUpdated(event);
            case "DELETED" -> handleDeleted(event);
            case "FUNDS_TRANSFERRED" -> handleTransfer(event);
            default -> log.warn("Tipo de evento desconhecido: {}", event.getEventType());
        }
    }

    private void handleCreated(TreasuryEvent e)   { /* … lógica */ }
    private void handleUpdated(TreasuryEvent e)   { /* … lógica */ }
    private void handleDeleted(TreasuryEvent e)   { /* … lógica */ }
    private void handleTransfer(TreasuryEvent e)  { /* … lógica */ }
}
```


### 6.5 Testes Automatizados

#### 6.5.1 Teste Unitário do Producer

```java
// src/test/java/com/fujitsu/treasury/kafka/TreasuryEventProducerTest.java
@ExtendWith(MockitoExtension.class)
class TreasuryEventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private TreasuryEventProducer producer;

    @Test
    void shouldPublishCreatedEvent() {
        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));

        producer.publishTreasuryCreated(1L, "ACC-001");

        verify(kafkaTemplate)
            .send(eq("treasury-events"), eq("ACC-001"), any(TreasuryEvent.class));
    }
}
```


#### 6.5.2 Teste de Integração Producer → Consumer

Uso do **`EmbeddedKafka`** da Spring Kafka–Test.

```java
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "treasury-events")
class KafkaIntegrationTest {

    @Autowired
    private TreasuryEventProducer producer;

    @Autowired
    private TreasuryEventConsumer consumer;

    @Test
    void producerShouldSendAndConsumerReceive() throws Exception {
        producer.publishTreasuryCreated(99L, "ACC-TEST");
        // dá tempo ao listener (máx 10s)
        Awaitility.await()
                  .atMost(Duration.ofSeconds(10))
                  .untilAsserted(() ->
                      assertThat(TestUtils.getLastEvent(consumer))
                          .extracting(TreasuryEvent::getAccountNumber)
                          .isEqualTo("ACC-TEST"));
    }
}
```


### 6.6 Scripts de Conveniência para Start \& Stop Local

| Comando | Descrição |
| :-- | :-- |
| `./mvnw -q -pl backend spring-boot:run` | Arranca o backend Spring Boot |
| `docker compose up -d kafka` | Sobe Kafka + Zookeeper em Docker |
| `docker compose logs -f kafka` | Verifica o estado do broker |
| `docker exec -it kafka bash` | Acede ao container para usar `kafka-console-consumer` |

## 6.7 Observabilidade

1. **Logging estruturado** via `logstash-encoder`.
2. **Micrometer + Prometheus**:

```yaml
management:
  endpoints.web.exposure.include: "health,info,prometheus"
```

3. **Grafana Dashboard** pronto para métricas `kafka.consumer.records` e `kafka.producer.records`.

## 6.8 Checklist de Produção

| Item | Status | Notas |
| :-- | :-- | :-- |
| TLS entre serviço ↔ broker | Em progresso | Usar SSLContext |
| ACLs de tópicos | Pendente | Criar Roles `treasury-writer`, `treasury-reader` |
| Particionamento | OK | 3 partições, chave = `accountNumber` |
| Retenção de tópicos | OK | `retention.ms=604800000` (7 dias) |
| Dead-Letter Topic | Pendente | Configurar `treasury-events.DLT` |

## Próximos Passos

1. **Parte 7 — Autenticação JWT \& Testes de Segurança**.
2. **Parte 8 — Deploy continuo com Docker-Compose e GitHub Actions**.
3. **Parte 9 — Observabilidade completa (ELK + Prometheus + Grafana)**.

> Continue desenvolvendo cada tópico seguindo a mesma abordagem TDD: _escreva primeiro o teste, depois o código, depois refatorize_. Assim garantirá qualidade contínua ao longo do ciclo de vida do projeto.

# Fases 7-12 — Da Segurança JWT ao Observability \& DevOps

Este manual conclui o ciclo completo do projeto “Treasury \& Collateral Management”, estendendo-o da Fase 7 à Fase 12. Cada fase inclui explicações detalhadas, código pronto a copiar e colar, testes dirigidos por TDD e razões pedagógicas que justificam cada passo.

## Visão Geral

A partir das fundações já construídas (Fases 1-6), avançamos agora para:

- Autenticação JWT robusta, hardening de segurança e testes de penetração.
- Contêineres Docker otimizados, orquestração via Docker-Compose e pipeline CI/CD com GitHub Actions.
- Observabilidade “full-stack”: logs estruturados (ELK), métricas (Prometheus) e dashboards (Grafana).
- Automação de QA e performance (Testcontainers, Gatling, OWASP ZAP).
- Documentação API First (OpenAPI 3.1) e sistemas de ajuda.
- Hardening de produção, secret management, escalabilidade e disaster recovery.


## Fase 7 — Segurança Avançada \& JWT

### 7.1 Por que agora?

Com entidades, serviços e eventos prontos, expomos múltiplos endpoints REST. Antes de qualquer deploy público, precisamos de:

- Autenticação stateless via JSON Web Tokens (JWT).
- Política de autorização por roles para módulos Treasury e Collateral.
- Hardening HTTP (CSP, HSTS, XSS, CSRF, CORS).
- Testes de segurança unitários e de integração para evitar regressões.


### 7.2 Configuração JWT

#### 7.2.1 Dependências (pom.xml)

```xml
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-api</artifactId>
  <version>0.12.3</version>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-impl</artifactId>
  <version>0.12.3</version>
  <scope>runtime</scope>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-jackson</artifactId>
  <version>0.12.3</version>
  <scope>runtime</scope>
</dependency>
```


#### 7.2.2 Gerador/Validador de Tokens (`JwtUtil.java`)

```java
@Component
@Slf4j
public final class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")     // em segundos
    private Long expiration;

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generate(UserDetails user) {
        return Jwts.builder()
                   .setSubject(user.getUsername())
                   .claim("roles", user.getAuthorities())
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + expiration * 1_000))
                   .signWith(getKey(), SignatureAlgorithm.HS512)
                   .compact();
    }

    public String getUsername(String token) {
        return parse(token).getBody().getSubject();
    }

    public boolean isValid(String token, UserDetails user) {
        return (!isExpired(token) && getUsername(token).equals(user.getUsername()));
    }

    private boolean isExpired(String token) {
        return parse(token).getBody().getExpiration().before(new Date());
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
    }
}
```


#### 7.2.3 Filtro de Autenticação (`JwtAuthFilter.java`)

```java
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil         jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest  req,
                                    HttpServletResponse res,
                                    FilterChain         chain)
                                    throws ServletException, IOException {

        String header = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String username = jwtUtil.getUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userDetailsService.loadUserByUsername(username);
                if (jwtUtil.isValid(token, user)) {
                    UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
        chain.doFilter(req, res);
    }
}
```


#### 7.2.4 SecurityConfig.java (hardening)

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(Customizer.withDefaults())
        .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
        .headers(h -> h
            .contentSecurityPolicy(c -> c.policyDirectives(
                "default-src 'self'; script-src 'self'"))
            .httpStrictTransportSecurity(
                hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31_536_000))
            .xssProtection(Customizer.withDefaults())
            .frameOptions(FrameOptionsConfig::disable))
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
            .requestMatchers("/api/treasury/**").hasAnyRole("TREASURY","ADMIN")
            .requestMatchers("/api/collateral/**").hasAnyRole("COLLATERAL","ADMIN")
            .anyRequest().authenticated())
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
}
```


### 7.3 Testes de Segurança (TDD)

#### 7.3.1 Teste unitário do JwtUtil

```java
@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @Mock
    private UserDetails user;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(jwtUtil, "secret", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600L);
        when(user.getUsername()).thenReturn("alice");
        when(user.getAuthorities()).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void shouldGenerateValidToken() {
        String token = jwtUtil.generate(user);
        assertThat(jwtUtil.isValid(token, user)).isTrue();
        assertThat(jwtUtil.getUsername(token)).isEqualTo("alice");
    }
}
```


#### 7.3.2 Teste de Integração (MockMvc + Spring Security)

```java
@WebMvcTest(controllers = TreasuryController.class)
@AutoConfigureMockMvc(addFilters = true)
class SecurityIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private TreasuryService treasuryService;
    @MockBean private JwtUtil jwtUtil;
    @MockBean private UserDetailsService uds;

    @Test
    void unauthenticatedShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/treasury"))
               .andExpect(status().isUnauthorized());
    }
}
```


### 7.4 Checklist Hardening

| Medida | Estado | Observações |
| :-- | :-- | :-- |
| HTTPS by default (TLSv1.3) | OK | Certbot + nginx reverse-proxy |
| CSP \& HSTS | OK | Configurado em `SecurityFilterChain` |
| Rate limiting (Bucket4j) | Pendente | Será implantado na Fase 12 |
| Secret rotation (Vault) | Pendente | Backlog para produção |
| Automated SCA (OWASP Dependency-Check) | OK | Executa em pipeline CI |

## Fase 8 — Entrega Contínua \& Infraestrutura

### 8.1 Dockerização

#### 8.1.1 Dockerfile (backend)

```dockerfile
FROM eclipse-temurin:17-jre-alpine
ARG JAR_FILE=target/treasury.jar
RUN addgroup -S treasury && adduser -S treasury -G treasury
COPY ${JAR_FILE} app.jar
USER treasury
ENTRYPOINT ["java","-jar","/app.jar"]
HEALTHCHECK CMD curl --fail http://localhost:8080/actuator/health || exit 1
```


#### 8.1.2 Dockerfile (frontend)

```dockerfile
FROM node:20.5-alpine AS build
WORKDIR /app
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/. .
RUN npm run build

FROM nginx:1.26-alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
HEALTHCHECK CMD curl --fail http://localhost || exit 1
```


#### 8.1.3 docker-compose.yml

```yaml
version: "3.9"
services:
  postgres:
    image: postgres:15-alpine
    restart: unless-stopped
    environment:
      POSTGRES_DB: treasury
      POSTGRES_USER: treasury
      POSTGRES_PASSWORD: treasury
    volumes:
      - db_data:/var/lib/postgresql/data
  kafka:
    image: bitnami/kafka:3.7
    restart: unless-stopped
    environment:
      KAFKA_CFG_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_CFG_LISTENERS: PLAINTEXT://:9092
    depends_on:
      - zookeeper
  zookeeper:
    image: bitnami/zookeeper:3.9
    restart: unless-stopped
  backend:
    build:
      context: .
      dockerfile: Dockerfile
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/treasury
      KAFKA_BOOTSTRAP_SERVERS: kafka:9092
    ports: ["8080:8080"]
    depends_on: [postgres, kafka]
  frontend:
    build:
      dockerfile: Dockerfile
      context: .
    ports: ["80:80"]
    depends_on: [backend]
volumes:
  db_data:
```


### 8.2 CI/CD com GitHub Actions

#### 8.2.1 Workflow backend (`.github/workflows/backend.yml`)

```yaml
name: Backend CI

on:
  push:
    paths: [ backend/** ]
  pull_request:
    paths: [ backend/** ]

jobs:
  build-test:
    runs-on: ubuntu-latest

    services:
      postgres:
        image: postgres:15-alpine
        env:
          POSTGRES_DB: treasury
          POSTGRES_USER: treasury
          POSTGRES_PASSWORD: treasury
        ports: ["5432:5432"]
        options: >-
          --health-cmd="pg_isready -U treasury"
          --health-interval=10s
          --health-timeout=5s
          --health-retries=5

    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17
      - name: Build & test
        run: mvn -B -pl backend -am verify
      - name: Upload coverage
        uses: codecov/codecov-action@v4
        with:
          files: backend/target/site/jacoco/jacoco.xml
```


#### 8.2.2 Workflow full-stack deploy (`deploy.yml`)

```yaml
name: Deploy to DockerHub & Preview

on:
  push:
    branches: [ main ]
jobs:
  publish:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: docker/setup-buildx-action@v3
      - uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKER_USER }}
          password: ${{ secrets.DOCKER_PASS }}
      - name: Build & push backend
        uses: docker/build-push-action@v5
        with:
          context: .
          target: backend
          push: true
          tags: ${{ secrets.DOCKER_USER }}/treasury-backend:latest
      - name: Build & push frontend
        uses: docker/build-push-action@v5
        with:
          context: .
          target: frontend
          push: true
          tags: ${{ secrets.DOCKER_USER }}/treasury-frontend:latest
```


### 8.3 Tabela de Environments

| Ambiente | Objetivo | URL Base | Banco de Dados | Observability | Deploy |
| :-- | :-- | :-- | :-- | :-- | :-- |
| dev | Desenvolvimento local | http://localhost:8080 | H2 | Local logs | Docker |
| staging | QA / UAT | https://stg.ftrs.local | PostgreSQL | ELK-docker | GH-Pages |
| prod | Produção | https://treasury.apps | RDS-Postgres | ELK + Grafana | ArgoCD |

## Fase 9 — Observability “3 Pilares”

### 9.1 Logging Estruturado

#### 9.1.1 Dependência

```xml
<dependency>
  <groupId>net.logstash.logback</groupId>
  <artifactId>logstash-logback-encoder</artifactId>
  <version>7.4</version>
</dependency>
```


#### 9.1.2 `logback-spring.xml`

```xml
<configuration>
  <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
  <appender name="JSON" class="net.logstash.logback.appender.LogstashSocketAppender">
    <destination>elk:5000</destination>
    <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
  </appender>
  <root level="INFO">
    <appender-ref ref="JSON"/>
  </root>
</configuration>
```


### 9.2 Métricas \& Tracing

| Ferramenta | Scope | Endpoints Exportados |
| :-- | :-- | :-- |
| Micrometer | JVM, HTTP, Kafka | `/actuator/prometheus` |
| Prometheus | Coleta \& storage | `scrape_interval:15s` |
| Grafana | Dashboards visuais | `:3000` |

#### 9.2.1 prometheus.yml (scrape Spring Boot)

```yaml
scrape_configs:
  - job_name: treasury-backend
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['backend:8080']
```


### 9.3 Dashboard Grafana

Adicionar dashboard “Spring Boot JVM + Kafka” e “PostgreSQL Overview”. Exportar JSON para pasta `monitoring/dashboards`.

### 9.4 Tracing Distribuído (Opcional)

- **OpenTelemetry SDK** + **Tempo** como backend.
- Ativar `OTEL_EXPORTER_OTLP_ENDPOINT=http://tempo:4317`.


## Fase 10 — Qualidade \& Performance

### 10.1 Testcontainers

```java
@Testcontainers
class TreasuryRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
                   .withDatabaseName("treasury")
                   .withUsername("treasury")
                   .withPassword("treasury");

    @DynamicPropertySource
    static void config(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```


### 10.2 Load Testing (Gatling)

- Simular 1,000 RPS na rota `/api/treasury`.
- Limite de SLA: 95º percentile < 350 ms.


### 10.3 Security Scanning

- **OWASP ZAP CI Action** varre preview URLs.
- **Trivy** faz scan de vulnerabilidades nos contêineres.


## Fase 11 — Documentação API First

### 11.1 OpenAPI 3.1 com SpringDoc

```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.3.0</version>
</dependency>
```

- UI disponível em `/swagger-ui.html`.
- Versionamento de API via `@OpenAPIDefinition(info=@Info(version="v1"))`.


### 11.2 Snippet de Uso

```bash
curl -X POST 'https://treasury.apps/api/treasury' \
  -H 'Authorization: Bearer $TOKEN' \
  -H 'Content-Type: application/json' \
  -d '{"accountNumber":"ACC-007","currency":"EUR", ... }'
```


## Fase 12 — Hardening \& Escalabilidade

| Técnica | Benefício | Ferramenta |
| :-- | :-- | :-- |
| Blue-Green Deploy | Zero-downtime | Traefik |
| Read-Replica Postgres | Melhora throughput de leitura | RDS-RR |
| Kafka DLQ | Resiliência a mensagens inválidas | Topic `.DLT` |
| Circuit Breaker / Retry | Resiliência de chamadas REST | Resilience4j |
| Secrets no Vault | Elimina segredos em texto-plano | HashiCorp Vault |
| Autoscaling Horizontal | Escalabilidade baseada em CPU/RPS | Kubernetes HPA |

# Conclusão

Com as Fases 7-12, o sistema passa de um protótipo funcional para uma plataforma **pronta para produção**:

- **Segurança**: JWT, hardening HTTP, política de roles e testes de penetração.
- **DevOps**: Imagens Docker imutáveis, orquestração via Compose/K8s e pipelines CI/CD.
- **Observability**: ELK para logs, Prometheus + Grafana para métricas e tracing opcional via OpenTelemetry.
- **Qualidade**: Testcontainers, Gatling, ZAP e SCA automáticos garantem robustez contínua.
- **Documentação \& Escalabilidade**: OpenAPI, Blue-Green Deploy, segredo gerido, monitoração 24×7.

A metodologia TDD continua a ser aplicada em cada módulo, garantindo cobertura e prevenção futuras de regressões. A partir daqui, as próximas iterações podem focar em:

- Multi-moeda com FX real-time (API ECB).
- Engine de regras para haircuts dinâmicos.
- Painel de Business Intelligence on-top (Apache Superset).
- Compliance (ISO 20022) para integração bancária.

Parabéns—você possui agora um projeto de referência “enterprise-grade” que demonstra expertise de ponta a ponta em engenharia de software moderna para o domínio financeiro.



[^1]: https://dev.to/devcorner/test-driven-development-with-java-spring-boot-the-complete-guide-16d3

[^2]: https://rieckpil.de/tdd-with-spring-boot-done-right/

[^3]: https://codesignal.com/learn/courses/introduction-to-spring-boot-and-spring-core-with-kotlin/lessons/understanding-spring-boot-project-structure

[^4]: https://www.geeksforgeeks.org/java/spring-boot-code-structure/

[^5]: https://stackoverflow.com/questions/74060701/java-spring-boot-repository-service/74063774

[^6]: https://docs.spring.io/spring-boot/reference/using/structuring-your-code.html

[^7]: https://spring.io/guides/tutorials/rest

[^8]: https://spring.io/guides/gs/accessing-data-jpa

[^9]: https://stackoverflow.com/questions/70085755/how-to-pass-entity-between-controller-and-service-methods-in-spring-data-jpa

[^10]: https://www.linkedin.com/pulse/clean-spring-boot-apis-separating-entities-dtos-mappers-fabio-ribeiro-zrn9f

[^11]: https://igventurelli.io/testing-spring-boot-applications-an-introduction-to-unit-and-integration-testing/

[^12]: https://www.makariev.com/blog/test-driven-development-spring-boot-guide-to-unit-mock-integration-tests/

[^13]: https://rieckpil.de/spring-boot-unit-and-integration-testing-overview/

[^14]: Projeto-Treasury-Collateral-Management-Treino.md

[^15]: https://www.youtube.com/watch?v=gJrjgg1KVL4

[^16]: https://www.theserverside.com/blog/Coffee-Talk-Java-News-Stories-and-Opinions/Get-started-with-Spring-Boot-tutorial

[^17]: https://www.codingshuttle.com/spring-boot-handbook/creating-first-spring-boot-project-and-understanding-the-project-structure-1

[^18]: https://www.youtube.com/watch?v=D44si7o4ndg

[^19]: https://www.youtube.com/watch?v=aS0t9HTO5V4

[^20]: https://dev.to/jazzybruno/spring-boot-project-folder-structure-12oe

[^21]: https://www.baeldung.com/jsf-spring-boot-controller-service-dao

[^22]: https://www.baeldung.com/spring-boot

[^23]: https://spring.io/guides/gs/spring-boot

[^24]: https://dev.to/wkreuch/create-a-service-and-controller-using-spring-boot-3-b2n

[^25]: https://www.geeksforgeeks.org/advance-java/spring-boot/

[^26]: https://docs.spring.io/spring-boot/tutorial/first-application/index.html

[^27]: https://www.youtube.com/watch?v=YeU2NuPnY8w

[^28]: https://www.baeldung.com/spring-boot-change-request-body-before-controller

[^29]: https://www.baeldung.com/spring-order

[^30]: https://stackoverflow.com/questions/45121037/spring-boot-create-table-from-entity-with-wrong-order-shown-in-database

[^31]: https://vladmihalcea.com/how-to-order-entity-subclasses-by-their-class-type-using-jpa-and-hibernate/

[^32]: https://robertniestroj.hashnode.dev/ordering-columns-in-a-table-in-jpahibernate

[^33]: https://stackoverflow.com/questions/61303236/how-to-use-dtos-in-the-controller-service-and-repository-pattern

[^34]: https://www.youtube.com/watch?v=zhhpS7bh2T8

[^35]: https://www.baeldung.com/jpa-sort

[^36]: https://www.youtube.com/watch?v=Cw0J6jYJtzw

[^37]: https://docs.spring.io/spring-data/data-commons/docs/1.6.1.RELEASE/reference/html/repositories.html

[^38]: https://www.geeksforgeeks.org/springboot/best-way-to-master-spring-boot-a-complete-roadmap/

[^39]: https://www.reddit.com/r/learnjava/comments/10jn60u/does_it_make_sense_to_test_the_repository_layer/

[^40]: https://github.com/mehmetpekdemir/Spring-Boot-With-Test-Driven-Development

[^41]: https://stackoverflow.com/questions/75482134/generating-unit-tests-for-my-service-implementations-on-the-spring-boot-applicat

[^42]: https://www.codingshuttle.com/spring-boot-handbook/unit-testing-vs-integration-testing

[^43]: https://courses.baeldung.com/courses/1295711/lectures/30127904

[^44]: https://dev.to/aharmaz/introduction-to-spring-boot-testing-m0

[^45]: https://www.linkedin.com/learning/test-driven-development-in-spring-boot-with-junit-and-mockito

[^46]: https://stackoverflow.com/questions/78265762/are-both-integration-test-and-unit-test-needed-in-all-cases-in-spring-boot

[^47]: https://www.freecodecamp.org/news/unit-testing-services-endpoints-and-repositories-in-spring-boot-4b7d9dc2b772/

[^48]: https://1kevinson.com/testing-service-spring-boot/

[^49]: https://www.youtube.com/watch?v=-H5sud1-K5A

[^50]: https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html

[^51]: https://www.baeldung.com/spring-boot-testing

[^52]: https://www.youtube.com/watch?v=oMuaRejdSlI

