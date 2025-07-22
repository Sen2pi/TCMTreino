package com.treasury.kpstreasury.models.entity;

import com.treasury.kpstreasury.utils.TestsUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith({SpringExtension.class})
public class CollateralEntityTests {

    private final TestsUtil testsUtil = new TestsUtil();

    @Test
    void shouldCreateCollateralWithRequiredFields() {
        CollateralEntity collateral = testsUtil.createCollateralEntityA();

        collateral.calculateEligibleValue();

        assertThat(collateral.getEligibleValue()).isEqualByComparingTo("475000.00");
        assertThat(collateral.getCollateralType()).isEqualTo(CollateralType.GOVERNMENT_BOND);
        assertThat(collateral.getRating()).isEqualTo(Rating.AAA);
        assertThat(collateral.getStatus()).isEqualTo(CollateralStatus.ELIGIBLE);

    }

    @Test
    void shouldCalculateEligibleValueCorrectly() {
        CollateralEntity collateral = testsUtil.createCollateralEntityA();
        collateral.setMarketValue(new BigDecimal("1000000.00"));
        collateral.setHaircut(new BigDecimal("0.1500")); // 15%
        collateral.calculateEligibleValue();

        assertThat(collateral.getEligibleValue()).isEqualByComparingTo("850000.00");

    }

}
