package com.treasury.kpstreasury.services;

import com.treasury.kpstreasury.enums.AccountStatus;
import com.treasury.kpstreasury.enums.AccountType;
import com.treasury.kpstreasury.models.dto.TreasuryDto;
import com.treasury.kpstreasury.models.dto.TreasurySummaryDto;
import com.treasury.kpstreasury.models.entity.TreasuryEntity;
import com.treasury.kpstreasury.repositories.TreasuryRepository;
import com.treasury.kpstreasury.utils.TreasuryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TreasuryService {

    private final TreasuryRepository treasuryRepository;
    private final TreasuryMapper treasuryMapper;

    public TreasuryDto createTreasuryAccount(TreasuryDto treasuryDto) {
        if (treasuryRepository.existsByAccountNumber(treasuryDto.getAccountNumber())) {
            throw new IllegalArgumentException("Account number already exists: " + treasuryDto.getAccountNumber());
        }

        TreasuryEntity treasuryEntity = treasuryMapper.toEntity(treasuryDto);
        TreasuryEntity savedTreasury = treasuryRepository.save(treasuryEntity);
        return treasuryMapper.toDto(savedTreasury);
    }

    @Transactional(readOnly = true)
    public Optional<TreasuryDto> getTreasuryById(Long id) {
        return treasuryRepository.findById(id)
                .map(treasuryMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<TreasuryDto> getTreasuryByAccountNumber(String accountNumber) {
        return treasuryRepository.findByAccountNumber(accountNumber)
                .map(treasuryMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<TreasuryDto> getAllTreasuryAccounts() {
        return treasuryRepository.findAll()
                .stream()
                .map(treasuryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TreasuryDto> getTreasuryAccountsByStatus(AccountStatus status) {
        return treasuryRepository.findByStatus(status)
                .stream()
                .map(treasuryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TreasuryDto> getTreasuryAccountsByType(AccountType accountType) {
        return treasuryRepository.findByAccountType(accountType)
                .stream()
                .map(treasuryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TreasuryDto> getTreasuryAccountsByCurrency(String currency) {
        return treasuryRepository.findByCurrency(currency)
                .stream()
                .map(treasuryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TreasuryDto> getTreasuryAccountsByBank(String bankName) {
        return treasuryRepository.findByBankName(bankName)
                .stream()
                .map(treasuryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TreasuryDto> getTreasuryAccountsWithFilters(String currency, String bankName, 
                                                           AccountStatus status, Pageable pageable) {
        return treasuryRepository.findWithFilters(currency, bankName, status, pageable)
                .map(treasuryMapper::toDto);
    }

    public TreasuryDto updateTreasuryAccount(Long id, TreasuryDto treasuryDto) {
        TreasuryEntity existingTreasury = treasuryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Treasury account not found with id: " + id));

        TreasuryEntity updatedTreasury = treasuryMapper.updateEntity(existingTreasury, treasuryDto);
        TreasuryEntity savedTreasury = treasuryRepository.save(updatedTreasury);
        return treasuryMapper.toDto(savedTreasury);
    }

    public void deleteTreasuryAccount(Long id) {
        if (!treasuryRepository.existsById(id)) {
            throw new IllegalArgumentException("Treasury account not found with id: " + id);
        }
        treasuryRepository.deleteById(id);
    }

    public TreasuryDto updateBalance(Long id, BigDecimal newBalance) {
        TreasuryEntity treasury = treasuryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Treasury account not found with id: " + id));

        treasury.setBalance(newBalance);
        
        if (treasury.getAvailableBalance().compareTo(newBalance) > 0) {
            treasury.setAvailableBalance(newBalance);
        }

        TreasuryEntity savedTreasury = treasuryRepository.save(treasury);
        return treasuryMapper.toDto(savedTreasury);
    }

    public TreasuryDto updateAvailableBalance(Long id, BigDecimal newAvailableBalance) {
        TreasuryEntity treasury = treasuryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Treasury account not found with id: " + id));

        if (newAvailableBalance.compareTo(treasury.getBalance()) > 0) {
            throw new IllegalArgumentException("Available balance cannot be greater than total balance");
        }

        treasury.setAvailableBalance(newAvailableBalance);
        TreasuryEntity savedTreasury = treasuryRepository.save(treasury);
        return treasuryMapper.toDto(savedTreasury);
    }

    public void activateAccount(Long id) {
        TreasuryEntity treasury = treasuryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Treasury account not found with id: " + id));
        treasury.setStatus(AccountStatus.ACTIVE);
        treasuryRepository.save(treasury);
    }

    public void deactivateAccount(Long id) {
        TreasuryEntity treasury = treasuryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Treasury account not found with id: " + id));
        treasury.setStatus(AccountStatus.INACTIVE);
        treasuryRepository.save(treasury);
    }

    public void suspendAccount(Long id) {
        TreasuryEntity treasury = treasuryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Treasury account not found with id: " + id));
        treasury.setStatus(AccountStatus.SUSPENDED);
        treasuryRepository.save(treasury);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalBalanceByCurrencyAndStatus(String currency, AccountStatus status) {
        BigDecimal total = treasuryRepository.getTotalBalanceByCurrencyAndStatus(currency, status);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalAvailableBalance() {
        BigDecimal total = treasuryRepository.getTotalAvailableBalance();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public List<TreasurySummaryDto> getTreasurySummaryByCurrency() {
        List<Object[]> results = treasuryRepository.getTreasurySummaryByCurrency();
        return results.stream()
                .map(row -> {
                    String currency = (String) row[0];
                    BigDecimal totalBalance = (BigDecimal) row[1];
                    Long accountCount = (Long) row[2];
                    
                    BigDecimal averageBalance = accountCount > 0 ? 
                        totalBalance.divide(BigDecimal.valueOf(accountCount), 2, RoundingMode.HALF_UP) : 
                        BigDecimal.ZERO;
                    
                    return new TreasurySummaryDto(currency, totalBalance, null, accountCount, averageBalance);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TreasuryDto> getLowBalanceAccounts(BigDecimal threshold) {
        return treasuryRepository.findLowBalanceAccounts(threshold)
                .stream()
                .map(treasuryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isAccountNumberAvailable(String accountNumber) {
        return !treasuryRepository.existsByAccountNumber(accountNumber);
    }

    public TreasuryDto transferFunds(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        TreasuryEntity fromAccount = treasuryRepository.findById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found with id: " + fromAccountId));

        TreasuryEntity toAccount = treasuryRepository.findById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found with id: " + toAccountId));

        if (fromAccount.getAvailableBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient available balance for transfer");
        }

        if (!fromAccount.getStatus().equals(AccountStatus.ACTIVE) || 
            !toAccount.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new IllegalArgumentException("Both accounts must be active for transfer");
        }

        fromAccount.setAvailableBalance(fromAccount.getAvailableBalance().subtract(amount));
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));

        toAccount.setAvailableBalance(toAccount.getAvailableBalance().add(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        treasuryRepository.save(fromAccount);
        treasuryRepository.save(toAccount);

        return treasuryMapper.toDto(fromAccount);
    }
}
