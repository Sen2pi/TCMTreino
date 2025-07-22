package com.treasury.kpstreasury.models.entity;

import com.treasury.kpstreasury.enums.AccountStatus;
import com.treasury.kpstreasury.enums.AccountType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="treasurys")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreasuryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false,unique = true,length = 50)
    private  String accountNumber;

    @Column(nullable = false, length= 3)
    private  String currency;

    @Column(nullable = false, length= 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false, length= 19, scale = 2)
    private  BigDecimal availableBalance;

    @Column(nullable = false, length= 20)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(nullable = false, length= 20)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(nullable = false, length= 100)
    private  String bankName;

    @Column(nullable = false, length= 20)
    private  String branchCode;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime  createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
