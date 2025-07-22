package com.treasury.kpstreasury.models.entity;


import com.treasury.kpstreasury.enums.CollateralStatus;
import com.treasury.kpstreasury.enums.CollateralType;
import com.treasury.kpstreasury.enums.Rating;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="collaterals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollateralEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private CollateralType collateralType;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal marketValue;

    @Column(nullable = false, precision = 5, scale = 4) // Ex: 0.1500 = 15%
    private BigDecimal haircut;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal eligibleValue; // Calculado: marketValue * (1 - haircut)

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Rating rating;

    @Column(nullable = true)
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

    @PrePersist
    @PreUpdate
    public void calculateEligibleValue() {
        if (marketValue != null && haircut != null) {
            this.eligibleValue = marketValue.multiply(BigDecimal.ONE.subtract(haircut));
        }
    }

}
