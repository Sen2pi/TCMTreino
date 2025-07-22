package com.treasury.kpstreasury.models.entity;

import com.treasury.kpstreasury.enums.AccountStatus;
import com.treasury.kpstreasury.enums.AccountType;
import com.treasury.kpstreasury.utils.TestsUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode =  DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TresuryEntityTests {

    private final TestsUtil testsUtil = new TestsUtil();


    @Test
    void shouldCreateTreasuryEntityWithRequiredFields() {
        TreasuryEntity treasury = testsUtil.createTreasuryEntityA();

        assertThat(treasury.getAccountNumber()).isEqualTo("ACC-001");
        assertThat(treasury.getCurrency()).isEqualTo("EUR");
        assertThat(treasury.getBalance()).isEqualByComparingTo("1000000.00");
        assertThat(treasury.getAvailableBalance()).isEqualByComparingTo("800000.00");
        assertThat(treasury.getAccountType()).isEqualTo(AccountType.CHECKING);
        assertThat(treasury.getStatus()).isEqualTo(AccountStatus.ACTIVE);

    }

}
