package com.treasury.kpstreasury.repositories;

import com.treasury.kpstreasury.enums.AccountStatus;
import com.treasury.kpstreasury.enums.AccountType;
import com.treasury.kpstreasury.models.entity.TreasuryEntity;
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
public interface TreasuryRepository extends JpaRepository<TreasuryEntity, Long> {

    Optional<TreasuryEntity> findByAccountNumber(String accountNumber);

    List<TreasuryEntity> findByStatus(AccountStatus status);

    List<TreasuryEntity> findByAccountType(AccountType accountType);

    List<TreasuryEntity> findByCurrency(String currency);

    List<TreasuryEntity> findByBankName(String bankName);

    boolean existsByAccountNumber(String accountNumber);

    //Finance Dashboard
    @Query("SELECT SUM(t.balance) FROM TreasuryEntity t WHERE t.currency = :currency AND t.status = :status")
    BigDecimal getTotalBalanceByCurrencyAndStatus(@Param("currency") String currency, @Param("status") AccountStatus status);

    @Query("SELECT SUM(t.availableBalance) FROM TreasuryEntity  t WHERE t.status = 'ACTIVE'")
    BigDecimal getTotalAvailableBalance();

    //Agregated Reports Queries
    @Query("SELECT t.currency, SUM(t.balance), COUNT(t) FROM TreasuryEntity t WHERE t.status = 'ACTIVE' GROUP BY t.currency ")
    List<Object[]> getTreasurySummaryByCurrency();

    @Query("SELECT t FROM TreasuryEntity t WHERE t.availableBalance < :threshold AND t.status = 'ACTIVE' ")
    List<TreasuryEntity> findLowBalanceAccounts(@Param("threshold") BigDecimal threshold);

    //Search Accounts with the multi filter option
    @Query("SELECT t FROM TreasuryEntity t WHERE " +
            "(:currency IS NULL OR t.currency = :currency) AND " +
            "(:bankName IS NULL OR t.bankName LIKE %:bankName%) AND " +
            "(:status IS NULL OR t.status = :status)")
    Page<TreasuryEntity> findWithFilters(@Param("currency") String currency,
                                         @Param("bankName") String bankName,
                                         @Param("status") AccountStatus status,
                                         Pageable pageable);
}

