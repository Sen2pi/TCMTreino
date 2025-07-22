package com.treasury.kpstreasury.repositories;

import com.treasury.kpstreasury.enums.CollateralStatus;
import com.treasury.kpstreasury.enums.CollateralType;
import com.treasury.kpstreasury.enums.Rating;
import com.treasury.kpstreasury.models.entity.CollateralEntity;
import com.treasury.kpstreasury.utils.TestsUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CollateralRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CollateralRepository collateralRepository;

    private TestsUtil testsUtil;

    private CollateralEntity collateralA;
    private CollateralEntity collateralB;
    private CollateralEntity collateralC;
    private CollateralEntity collateralD;

    @BeforeEach
    void setUp() {
        testsUtil = new TestsUtil();

        // Create and persist test data
        collateralA = testsUtil.createCollateralEntityA(); // GOVERNMENT_BOND, EUR, AAA, ELIGIBLE
        collateralB = testsUtil.createCollateralEntityB(); // CORPORATE_BOND, EUR, BBB, ELIGIBLE
        collateralC = testsUtil.createCollateralEntityC(); // EQUITY, EUR, A, PENDING
        collateralD = testsUtil.createCollateralEntityD(); // REAL_ESTATE, EUR, AA, INELIGIBLE

        entityManager.persistAndFlush(collateralA);
        entityManager.persistAndFlush(collateralB);
        entityManager.persistAndFlush(collateralC);
        entityManager.persistAndFlush(collateralD);
    }

    @Test
    void shouldFindCollateralByStatus() {
        // When
        List<CollateralEntity> eligibleCollaterals = collateralRepository.findByStatus(CollateralStatus.ELIGIBLE);
        List<CollateralEntity> pendingCollaterals = collateralRepository.findByStatus(CollateralStatus.PLEDGED);
        List<CollateralEntity> ineligibleCollaterals = collateralRepository.findByStatus(CollateralStatus.INELIGIBLE);

        // Then
        assertThat(eligibleCollaterals).hasSize(2);
        assertThat(eligibleCollaterals).extracting("description")
                .containsExactlyInAnyOrder("Portuguese Government Bond 2025", "EDP Corporate Bond 2026");

        assertThat(pendingCollaterals).hasSize(1);
        assertThat(pendingCollaterals.get(0).getDescription()).isEqualTo("Jerónimo Martins Shares");

        assertThat(ineligibleCollaterals).hasSize(1);
        assertThat(ineligibleCollaterals.get(0).getDescription()).isEqualTo("Commercial Property Porto");
    }

    @Test
    void shouldFindCollateralByType() {
        // When
        List<CollateralEntity> governmentBonds = collateralRepository.findByCollateralType(CollateralType.GOVERNMENT_BOND);
        List<CollateralEntity> corporateBonds = collateralRepository.findByCollateralType(CollateralType.CORPORATE_BOND);
        List<CollateralEntity> equities = collateralRepository.findByCollateralType(CollateralType.EQUITY);
        List<CollateralEntity> realEstate = collateralRepository.findByCollateralType(CollateralType.REAL_STATE);

        // Then
        assertThat(governmentBonds).hasSize(1);
        assertThat(governmentBonds.get(0).getDescription()).isEqualTo("Portuguese Government Bond 2025");

        assertThat(corporateBonds).hasSize(1);
        assertThat(corporateBonds.get(0).getDescription()).isEqualTo("EDP Corporate Bond 2026");

        assertThat(equities).hasSize(1);
        assertThat(equities.get(0).getDescription()).isEqualTo("Jerónimo Martins Shares");

        assertThat(realEstate).hasSize(1);
        assertThat(realEstate.get(0).getDescription()).isEqualTo("Commercial Property Porto");
    }

    @Test
    void shouldFindCollateralByRating() {
        // When
        List<CollateralEntity> aaaRated = collateralRepository.findByRating(Rating.AAA);
        List<CollateralEntity> aaRated = collateralRepository.findByRating(Rating.AA);
        List<CollateralEntity> aRated = collateralRepository.findByRating(Rating.A);
        List<CollateralEntity> bbbRated = collateralRepository.findByRating(Rating.BBB);

        // Then
        assertThat(aaaRated).hasSize(1);
        assertThat(aaaRated.get(0).getDescription()).isEqualTo("Portuguese Government Bond 2025");

        assertThat(aaRated).hasSize(1);
        assertThat(aaRated.get(0).getDescription()).isEqualTo("Commercial Property Porto");

        assertThat(aRated).hasSize(1);
        assertThat(aRated.get(0).getDescription()).isEqualTo("Jerónimo Martins Shares");

        assertThat(bbbRated).hasSize(1);
        assertThat(bbbRated.get(0).getDescription()).isEqualTo("EDP Corporate Bond 2026");
    }

    @Test
    void shouldFindCollateralByCurrency() {
        // When
        List<CollateralEntity> eurCollaterals = collateralRepository.findByCurrency("EUR");
        List<CollateralEntity> usdCollaterals = collateralRepository.findByCurrency("USD");

        // Then
        assertThat(eurCollaterals).hasSize(4); // All test collaterals are EUR
        assertThat(usdCollaterals).isEmpty();
    }

    @Test
    void shouldFindCollateralByCounterparty() {
        // When
        List<CollateralEntity> republicCollaterals = collateralRepository.findByCounterparty("Portuguese Republic");
        List<CollateralEntity> edpCollaterals = collateralRepository.findByCounterparty("EDP - Energias de Portugal");

        // Then
        assertThat(republicCollaterals).hasSize(1);
        assertThat(republicCollaterals.get(0).getDescription()).isEqualTo("Portuguese Government Bond 2025");

        assertThat(edpCollaterals).hasSize(1);
        assertThat(edpCollaterals.get(0).getDescription()).isEqualTo("EDP Corporate Bond 2026");
    }

    @Test
    void shouldFindCollateralByStatusAndRatingIn() {
        // When
        List<Rating> acceptableRatings = Arrays.asList(Rating.AAA, Rating.AA, Rating.A);
        List<CollateralEntity> eligibleHighRated = collateralRepository
                .findByStatusAndRatingIn(CollateralStatus.ELIGIBLE, acceptableRatings);

        // Then
        assertThat(eligibleHighRated).hasSize(1); // Only collateralA (AAA, ELIGIBLE)
        assertThat(eligibleHighRated.get(0).getDescription()).isEqualTo("Portuguese Government Bond 2025");
        assertThat(eligibleHighRated.get(0).getRating()).isEqualTo(Rating.AAA);
    }

    @Test
    void shouldFindCollateralByMaturityDateBetween() {
        // When
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        List<CollateralEntity> expiringCollaterals = collateralRepository
                .findByMaturityDateBetween(startDate, endDate);

        // Then
        assertThat(expiringCollaterals).hasSize(1);
        assertThat(expiringCollaterals.get(0).getDescription()).isEqualTo("Portuguese Government Bond 2025");
        assertThat(expiringCollaterals.get(0).getMaturityDate()).isEqualTo(LocalDate.of(2025, 12, 31));
    }

    @Test
    void shouldGetTotalEligibleValue() {
        // When
        BigDecimal totalEligibleValue = collateralRepository.getTotalEligibleValue();

        // Then
        // Calculate expected: collateralA (500000 * (1 - 0.05)) + collateralB (300000 * (1 - 0.10))
        BigDecimal expectedA = new BigDecimal("500000.00").multiply(BigDecimal.ONE.subtract(new BigDecimal("0.0500")));
        BigDecimal expectedB = new BigDecimal("300000.00").multiply(BigDecimal.ONE.subtract(new BigDecimal("0.1000")));
        BigDecimal expected = expectedA.add(expectedB);

        assertThat(totalEligibleValue).isEqualByComparingTo(expected);
    }

    @Test
    void shouldGetCollateralSummaryByType() {
        // When
        List<Object[]> summary = collateralRepository.getCollateralSummaryByType();

        // Then
        assertThat(summary).hasSize(2); // GOVERNMENT_BOND and CORPORATE_BOND (only ELIGIBLE ones)

        // Find government bond summary
        Object[] govBondSummary = summary.stream()
                .filter(row -> row[0] == CollateralType.GOVERNMENT_BOND)
                .findFirst()
                .orElse(null);

        assertThat(govBondSummary).isNotNull();
        assertThat(govBondSummary[1]).isEqualTo(new BigDecimal("500000.00")); // Market value
    }

    @Test
    void shouldGetCollateralConcentrationByRating() {
        // When
        List<Object[]> concentration = collateralRepository.getCollateralConcentrationByRating();

        // Then
        assertThat(concentration).hasSize(2); // AAA and BBB (only ELIGIBLE ones)

        // Find AAA concentration
        Object[] aaaConcentration = concentration.stream()
                .filter(row -> row[0] == Rating.AAA)
                .findFirst()
                .orElse(null);

        assertThat(aaaConcentration).isNotNull();
        assertThat(aaaConcentration[1]).isEqualTo(1L); // Count
        assertThat(aaaConcentration[2]).isEqualTo(new BigDecimal("500000.00")); // Market value
    }

    @Test
    void shouldFindHighRiskCollateral() {
        // When
        BigDecimal threshold = new BigDecimal("0.0800");
        List<CollateralEntity> highRiskCollaterals = collateralRepository.findHighRiskCollateral(threshold);

        // Then
        assertThat(highRiskCollaterals).hasSize(1);
        assertThat(highRiskCollaterals.get(0).getDescription()).isEqualTo("EDP Corporate Bond 2026");
        assertThat(highRiskCollaterals.get(0).getHaircut()).isEqualByComparingTo(new BigDecimal("0.1000"));
    }

    @Test
    void shouldFindWithAdvancedFilters() {
        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<CollateralEntity> result = collateralRepository.findWithAdvancedFilters(
                CollateralType.GOVERNMENT_BOND,
                Rating.AAA,
                "EUR",
                CollateralStatus.ELIGIBLE,
                new BigDecimal("400000.00"),
                pageable
        );

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDescription()).isEqualTo("Portuguese Government Bond 2025");
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void shouldFindWithAdvancedFiltersNoResults() {
        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<CollateralEntity> result = collateralRepository.findWithAdvancedFilters(
                CollateralType.GOVERNMENT_BOND,
                Rating.AAA,
                "USD", // No USD collaterals
                CollateralStatus.ELIGIBLE,
                null,
                pageable
        );

        // Then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    void shouldFindWithAdvancedFiltersAllNull() {
        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<CollateralEntity> result = collateralRepository.findWithAdvancedFilters(
                null, null, null, null, null, pageable
        );

        // Then
        assertThat(result.getContent()).hasSize(4); // All collaterals
        assertThat(result.getTotalElements()).isEqualTo(4);
    }
}
