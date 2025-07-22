package com.treasury.kpstreasury.repositories;
import com.treasury.kpstreasury.enums.AccountStatus;
import com.treasury.kpstreasury.enums.AccountType;
import com.treasury.kpstreasury.models.entity.TreasuryEntity;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

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
        TreasuryEntity treasury = createTreasury("ACC-001", "EUR",
                new BigDecimal("1000000"), AccountStatus.ACTIVE);
        entityManager.persistAndFlush(treasury);

        // When
        Optional<TreasuryEntity> found = treasuryRepository.findByAccountNumber("ACC-001");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCurrency()).isEqualTo("EUR");
        assertThat(found.get().getBalance()).isEqualByComparingTo("1000000");
    }

    @Test
    void shouldCalculateTotalBalanceByCurrency() {
        // Given
        TreasuryEntity treasury1 = createTreasury("ACC-001", "EUR", new BigDecimal("1000000"), AccountStatus.ACTIVE);
        TreasuryEntity treasury2 = createTreasury("ACC-002", "EUR", new BigDecimal("2000000"), AccountStatus.ACTIVE);
        TreasuryEntity treasury3 = createTreasury("ACC-003", "USD", new BigDecimal("1500000"), AccountStatus.ACTIVE);

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
        TreasuryEntity lowBalance = createTreasury("ACC-LOW", "EUR", new BigDecimal("50000"), AccountStatus.ACTIVE);
        lowBalance.setAvailableBalance(new BigDecimal("50000"));

        TreasuryEntity highBalance = createTreasury("ACC-HIGH", "EUR", new BigDecimal("1000000"), AccountStatus.ACTIVE);
        highBalance.setAvailableBalance(new BigDecimal("1000000"));

        entityManager.persistAndFlush(lowBalance);
        entityManager.persistAndFlush(highBalance);

        // When
        List<TreasuryEntity> lowBalanceAccounts = treasuryRepository
                .findLowBalanceAccounts(new BigDecimal("100000"));

        // Then
        assertThat(lowBalanceAccounts).hasSize(1);
        assertThat(lowBalanceAccounts.get(0).getAccountNumber()).isEqualTo("ACC-LOW");
    }

    private TreasuryEntity createTreasury(String accountNumber, String currency,
                                    BigDecimal balance, AccountStatus status) {
        TreasuryEntity treasury = new TreasuryEntity();
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