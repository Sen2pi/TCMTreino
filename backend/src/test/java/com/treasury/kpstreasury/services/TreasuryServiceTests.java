package com.treasury.kpstreasury.services;

import com.treasury.kpstreasury.enums.AccountStatus;
import com.treasury.kpstreasury.enums.AccountType;
import com.treasury.kpstreasury.models.dto.TreasuryDto;
import com.treasury.kpstreasury.models.dto.TreasurySummaryDto;
import com.treasury.kpstreasury.models.entity.TreasuryEntity;
import com.treasury.kpstreasury.repositories.TreasuryRepository;
import com.treasury.kpstreasury.utils.TreasuryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreasuryServiceTests {

    @Mock
    private TreasuryRepository treasuryRepository;

    @Mock
    private TreasuryMapper treasuryMapper;

    @InjectMocks
    private TreasuryService treasuryService;

    private TreasuryEntity treasuryEntity;
    private TreasuryDto treasuryDto;

    @BeforeEach
    void setUp() {
        treasuryEntity = TreasuryEntity.builder()
                .id(1L)
                .accountNumber("ACC001")
                .currency("EUR")
                .balance(new BigDecimal("10000.00"))
                .availableBalance(new BigDecimal("8000.00"))
                .accountType(AccountType.CHECKING)
                .status(AccountStatus.ACTIVE)
                .bankName("Test Bank")
                .branchCode("0001")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        treasuryDto = TreasuryDto.builder()
                .id(1L)
                .accountNumber("ACC001")
                .currency("EUR")
                .balance(new BigDecimal("10000.00"))
                .availableBalance(new BigDecimal("8000.00"))
                .accountType(AccountType.CHECKING)
                .status(AccountStatus.ACTIVE)
                .bankName("Test Bank")
                .branchCode("0001")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createTreasuryAccount_ShouldCreateAccount_WhenValidData() {
        when(treasuryRepository.existsByAccountNumber("ACC001")).thenReturn(false);
        when(treasuryMapper.toEntity(treasuryDto)).thenReturn(treasuryEntity);
        when(treasuryRepository.save(treasuryEntity)).thenReturn(treasuryEntity);
        when(treasuryMapper.toDto(treasuryEntity)).thenReturn(treasuryDto);

        TreasuryDto result = treasuryService.createTreasuryAccount(treasuryDto);

        assertNotNull(result);
        assertEquals("ACC001", result.getAccountNumber());
        verify(treasuryRepository).save(treasuryEntity);
    }

    @Test
    void createTreasuryAccount_ShouldThrowException_WhenAccountNumberExists() {
        when(treasuryRepository.existsByAccountNumber("ACC001")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> treasuryService.createTreasuryAccount(treasuryDto));

        assertTrue(exception.getMessage().contains("Account number already exists"));
        verify(treasuryRepository, never()).save(any());
    }

    @Test
    void getTreasuryById_ShouldReturnTreasury_WhenAccountExists() {
        when(treasuryRepository.findById(1L)).thenReturn(Optional.of(treasuryEntity));
        when(treasuryMapper.toDto(treasuryEntity)).thenReturn(treasuryDto);

        Optional<TreasuryDto> result = treasuryService.getTreasuryById(1L);

        assertTrue(result.isPresent());
        assertEquals("ACC001", result.get().getAccountNumber());
        verify(treasuryRepository).findById(1L);
    }

    @Test
    void getTreasuryById_ShouldReturnEmpty_WhenAccountNotFound() {
        when(treasuryRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<TreasuryDto> result = treasuryService.getTreasuryById(1L);

        assertFalse(result.isPresent());
        verify(treasuryRepository).findById(1L);
    }

    @Test
    void getTreasuryByAccountNumber_ShouldReturnTreasury_WhenAccountExists() {
        when(treasuryRepository.findByAccountNumber("ACC001")).thenReturn(Optional.of(treasuryEntity));
        when(treasuryMapper.toDto(treasuryEntity)).thenReturn(treasuryDto);

        Optional<TreasuryDto> result = treasuryService.getTreasuryByAccountNumber("ACC001");

        assertTrue(result.isPresent());
        assertEquals("ACC001", result.get().getAccountNumber());
        verify(treasuryRepository).findByAccountNumber("ACC001");
    }

    @Test
    void getAllTreasuryAccounts_ShouldReturnAllAccounts() {
        List<TreasuryEntity> treasuryEntities = Arrays.asList(treasuryEntity);
        when(treasuryRepository.findAll()).thenReturn(treasuryEntities);
        when(treasuryMapper.toDto(treasuryEntity)).thenReturn(treasuryDto);

        List<TreasuryDto> result = treasuryService.getAllTreasuryAccounts();

        assertEquals(1, result.size());
        assertEquals("ACC001", result.get(0).getAccountNumber());
        verify(treasuryRepository).findAll();
    }

    @Test
    void getTreasuryAccountsByStatus_ShouldReturnAccountsByStatus() {
        List<TreasuryEntity> treasuryEntities = Arrays.asList(treasuryEntity);
        when(treasuryRepository.findByStatus(AccountStatus.ACTIVE)).thenReturn(treasuryEntities);
        when(treasuryMapper.toDto(treasuryEntity)).thenReturn(treasuryDto);

        List<TreasuryDto> result = treasuryService.getTreasuryAccountsByStatus(AccountStatus.ACTIVE);

        assertEquals(1, result.size());
        assertEquals(AccountStatus.ACTIVE, result.get(0).getStatus());
        verify(treasuryRepository).findByStatus(AccountStatus.ACTIVE);
    }

    @Test
    void getTreasuryAccountsByType_ShouldReturnAccountsByType() {
        List<TreasuryEntity> treasuryEntities = Arrays.asList(treasuryEntity);
        when(treasuryRepository.findByAccountType(AccountType.CHECKING)).thenReturn(treasuryEntities);
        when(treasuryMapper.toDto(treasuryEntity)).thenReturn(treasuryDto);

        List<TreasuryDto> result = treasuryService.getTreasuryAccountsByType(AccountType.CHECKING);

        assertEquals(1, result.size());
        assertEquals(AccountType.CHECKING, result.get(0).getAccountType());
        verify(treasuryRepository).findByAccountType(AccountType.CHECKING);
    }

    @Test
    void getTreasuryAccountsByCurrency_ShouldReturnAccountsByCurrency() {
        List<TreasuryEntity> treasuryEntities = Arrays.asList(treasuryEntity);
        when(treasuryRepository.findByCurrency("EUR")).thenReturn(treasuryEntities);
        when(treasuryMapper.toDto(treasuryEntity)).thenReturn(treasuryDto);

        List<TreasuryDto> result = treasuryService.getTreasuryAccountsByCurrency("EUR");

        assertEquals(1, result.size());
        assertEquals("EUR", result.get(0).getCurrency());
        verify(treasuryRepository).findByCurrency("EUR");
    }

    @Test
    void getTreasuryAccountsByBank_ShouldReturnAccountsByBank() {
        List<TreasuryEntity> treasuryEntities = Arrays.asList(treasuryEntity);
        when(treasuryRepository.findByBankName("Test Bank")).thenReturn(treasuryEntities);
        when(treasuryMapper.toDto(treasuryEntity)).thenReturn(treasuryDto);

        List<TreasuryDto> result = treasuryService.getTreasuryAccountsByBank("Test Bank");

        assertEquals(1, result.size());
        assertEquals("Test Bank", result.get(0).getBankName());
        verify(treasuryRepository).findByBankName("Test Bank");
    }

    @Test
    void getTreasuryAccountsWithFilters_ShouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TreasuryEntity> treasuryEntityPage = new PageImpl<>(Arrays.asList(treasuryEntity));
        when(treasuryRepository.findWithFilters("EUR", "Test Bank", AccountStatus.ACTIVE, pageable))
                .thenReturn(treasuryEntityPage);
        when(treasuryMapper.toDto(treasuryEntity)).thenReturn(treasuryDto);

        Page<TreasuryDto> result = treasuryService.getTreasuryAccountsWithFilters(
                "EUR", "Test Bank", AccountStatus.ACTIVE, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("EUR", result.getContent().get(0).getCurrency());
        verify(treasuryRepository).findWithFilters("EUR", "Test Bank", AccountStatus.ACTIVE, pageable);
    }

    @Test
    void updateTreasuryAccount_ShouldUpdateAccount_WhenValidData() {
        TreasuryDto updateDto = TreasuryDto.builder()
                .balance(new BigDecimal("15000.00"))
                .availableBalance(new BigDecimal("12000.00"))
                .status(AccountStatus.ACTIVE)
                .bankName("Updated Bank")
                .branchCode("0002")
                .build();

        when(treasuryRepository.findById(1L)).thenReturn(Optional.of(treasuryEntity));
        when(treasuryMapper.updateEntity(treasuryEntity, updateDto)).thenReturn(treasuryEntity);
        when(treasuryRepository.save(treasuryEntity)).thenReturn(treasuryEntity);
        when(treasuryMapper.toDto(treasuryEntity)).thenReturn(treasuryDto);

        TreasuryDto result = treasuryService.updateTreasuryAccount(1L, updateDto);

        assertNotNull(result);
        verify(treasuryRepository).save(treasuryEntity);
    }

    @Test
    void updateTreasuryAccount_ShouldThrowException_WhenAccountNotFound() {
        when(treasuryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> treasuryService.updateTreasuryAccount(1L, treasuryDto));

        verify(treasuryRepository, never()).save(any());
    }

    @Test
    void deleteTreasuryAccount_ShouldDeleteAccount_WhenAccountExists() {
        when(treasuryRepository.existsById(1L)).thenReturn(true);

        treasuryService.deleteTreasuryAccount(1L);

        verify(treasuryRepository).deleteById(1L);
    }

    @Test
    void deleteTreasuryAccount_ShouldThrowException_WhenAccountNotFound() {
        when(treasuryRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> treasuryService.deleteTreasuryAccount(1L));

        verify(treasuryRepository, never()).deleteById(any());
    }

    @Test
    void updateBalance_ShouldUpdateBalance() {
        BigDecimal newBalance = new BigDecimal("12000.00");
        when(treasuryRepository.findById(1L)).thenReturn(Optional.of(treasuryEntity));
        when(treasuryRepository.save(treasuryEntity)).thenReturn(treasuryEntity);
        when(treasuryMapper.toDto(treasuryEntity)).thenReturn(treasuryDto);

        TreasuryDto result = treasuryService.updateBalance(1L, newBalance);

        assertNotNull(result);
        verify(treasuryRepository).save(treasuryEntity);
        assertEquals(newBalance, treasuryEntity.getBalance());
    }

    @Test
    void updateBalance_ShouldAdjustAvailableBalance_WhenNewBalanceIsLower() {
        BigDecimal newBalance = new BigDecimal("5000.00");
        treasuryEntity.setAvailableBalance(new BigDecimal("8000.00"));
        
        when(treasuryRepository.findById(1L)).thenReturn(Optional.of(treasuryEntity));
        when(treasuryRepository.save(treasuryEntity)).thenReturn(treasuryEntity);
        when(treasuryMapper.toDto(treasuryEntity)).thenReturn(treasuryDto);

        treasuryService.updateBalance(1L, newBalance);

        assertEquals(newBalance, treasuryEntity.getBalance());
        assertEquals(newBalance, treasuryEntity.getAvailableBalance());
    }

    @Test
    void updateAvailableBalance_ShouldUpdateAvailableBalance_WhenValid() {
        BigDecimal newAvailableBalance = new BigDecimal("7000.00");
        when(treasuryRepository.findById(1L)).thenReturn(Optional.of(treasuryEntity));
        when(treasuryRepository.save(treasuryEntity)).thenReturn(treasuryEntity);
        when(treasuryMapper.toDto(treasuryEntity)).thenReturn(treasuryDto);

        TreasuryDto result = treasuryService.updateAvailableBalance(1L, newAvailableBalance);

        assertNotNull(result);
        verify(treasuryRepository).save(treasuryEntity);
        assertEquals(newAvailableBalance, treasuryEntity.getAvailableBalance());
    }

    @Test
    void updateAvailableBalance_ShouldThrowException_WhenGreaterThanBalance() {
        BigDecimal newAvailableBalance = new BigDecimal("15000.00");
        when(treasuryRepository.findById(1L)).thenReturn(Optional.of(treasuryEntity));

        assertThrows(IllegalArgumentException.class,
                () -> treasuryService.updateAvailableBalance(1L, newAvailableBalance));

        verify(treasuryRepository, never()).save(any());
    }

    @Test
    void activateAccount_ShouldActivateAccount() {
        when(treasuryRepository.findById(1L)).thenReturn(Optional.of(treasuryEntity));
        when(treasuryRepository.save(treasuryEntity)).thenReturn(treasuryEntity);

        treasuryService.activateAccount(1L);

        verify(treasuryRepository).save(treasuryEntity);
        assertEquals(AccountStatus.ACTIVE, treasuryEntity.getStatus());
    }

    @Test
    void deactivateAccount_ShouldDeactivateAccount() {
        when(treasuryRepository.findById(1L)).thenReturn(Optional.of(treasuryEntity));
        when(treasuryRepository.save(treasuryEntity)).thenReturn(treasuryEntity);

        treasuryService.deactivateAccount(1L);

        verify(treasuryRepository).save(treasuryEntity);
        assertEquals(AccountStatus.INACTIVE, treasuryEntity.getStatus());
    }

    @Test
    void suspendAccount_ShouldSuspendAccount() {
        when(treasuryRepository.findById(1L)).thenReturn(Optional.of(treasuryEntity));
        when(treasuryRepository.save(treasuryEntity)).thenReturn(treasuryEntity);

        treasuryService.suspendAccount(1L);

        verify(treasuryRepository).save(treasuryEntity);
        assertEquals(AccountStatus.SUSPENDED, treasuryEntity.getStatus());
    }

    @Test
    void getTotalBalanceByCurrencyAndStatus_ShouldReturnTotal() {
        BigDecimal expectedTotal = new BigDecimal("50000.00");
        when(treasuryRepository.getTotalBalanceByCurrencyAndStatus("EUR", AccountStatus.ACTIVE))
                .thenReturn(expectedTotal);

        BigDecimal result = treasuryService.getTotalBalanceByCurrencyAndStatus("EUR", AccountStatus.ACTIVE);

        assertEquals(expectedTotal, result);
        verify(treasuryRepository).getTotalBalanceByCurrencyAndStatus("EUR", AccountStatus.ACTIVE);
    }

    @Test
    void getTotalBalanceByCurrencyAndStatus_ShouldReturnZero_WhenNoResults() {
        when(treasuryRepository.getTotalBalanceByCurrencyAndStatus("USD", AccountStatus.ACTIVE))
                .thenReturn(null);

        BigDecimal result = treasuryService.getTotalBalanceByCurrencyAndStatus("USD", AccountStatus.ACTIVE);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void getTotalAvailableBalance_ShouldReturnTotal() {
        BigDecimal expectedTotal = new BigDecimal("40000.00");
        when(treasuryRepository.getTotalAvailableBalance()).thenReturn(expectedTotal);

        BigDecimal result = treasuryService.getTotalAvailableBalance();

        assertEquals(expectedTotal, result);
        verify(treasuryRepository).getTotalAvailableBalance();
    }

    @Test
    void getTreasurySummaryByCurrency_ShouldReturnSummary() {
        Object[] row = {"EUR", new BigDecimal("50000.00"), 5L};
        List<Object[]> results = Arrays.<Object[]>asList(row);
        when(treasuryRepository.getTreasurySummaryByCurrency()).thenReturn(results);

        List<TreasurySummaryDto> result = treasuryService.getTreasurySummaryByCurrency();

        assertEquals(1, result.size());
        assertEquals("EUR", result.get(0).getCurrency());
        assertEquals(new BigDecimal("50000.00"), result.get(0).getTotalBalance());
        assertEquals(5L, result.get(0).getAccountCount());
    }

    @Test
    void getLowBalanceAccounts_ShouldReturnLowBalanceAccounts() {
        BigDecimal threshold = new BigDecimal("1000.00");
        List<TreasuryEntity> treasuryEntities = Arrays.asList(treasuryEntity);
        when(treasuryRepository.findLowBalanceAccounts(threshold)).thenReturn(treasuryEntities);
        when(treasuryMapper.toDto(treasuryEntity)).thenReturn(treasuryDto);

        List<TreasuryDto> result = treasuryService.getLowBalanceAccounts(threshold);

        assertEquals(1, result.size());
        verify(treasuryRepository).findLowBalanceAccounts(threshold);
    }

    @Test
    void isAccountNumberAvailable_ShouldReturnTrue_WhenAccountNotExists() {
        when(treasuryRepository.existsByAccountNumber("ACC999")).thenReturn(false);

        boolean result = treasuryService.isAccountNumberAvailable("ACC999");

        assertTrue(result);
        verify(treasuryRepository).existsByAccountNumber("ACC999");
    }

    @Test
    void transferFunds_ShouldTransferFunds_WhenValidData() {
        TreasuryEntity fromAccount = TreasuryEntity.builder()
                .id(1L)
                .balance(new BigDecimal("10000.00"))
                .availableBalance(new BigDecimal("8000.00"))
                .status(AccountStatus.ACTIVE)
                .build();

        TreasuryEntity toAccount = TreasuryEntity.builder()
                .id(2L)
                .balance(new BigDecimal("5000.00"))
                .availableBalance(new BigDecimal("5000.00"))
                .status(AccountStatus.ACTIVE)
                .build();

        BigDecimal transferAmount = new BigDecimal("2000.00");

        when(treasuryRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(treasuryRepository.findById(2L)).thenReturn(Optional.of(toAccount));
        when(treasuryRepository.save(fromAccount)).thenReturn(fromAccount);
        when(treasuryRepository.save(toAccount)).thenReturn(toAccount);
        when(treasuryMapper.toDto(fromAccount)).thenReturn(treasuryDto);

        TreasuryDto result = treasuryService.transferFunds(1L, 2L, transferAmount);

        assertNotNull(result);
        assertEquals(new BigDecimal("6000.00"), fromAccount.getAvailableBalance());
        assertEquals(new BigDecimal("8000.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("7000.00"), toAccount.getAvailableBalance());
        assertEquals(new BigDecimal("7000.00"), toAccount.getBalance());
        verify(treasuryRepository, times(2)).save(any(TreasuryEntity.class));
    }

    @Test
    void transferFunds_ShouldThrowException_WhenInsufficientFunds() {
        TreasuryEntity fromAccount = TreasuryEntity.builder()
                .id(1L)
                .availableBalance(new BigDecimal("1000.00"))
                .status(AccountStatus.ACTIVE)
                .build();

        TreasuryEntity toAccount = TreasuryEntity.builder()
                .id(2L)
                .status(AccountStatus.ACTIVE)
                .build();

        BigDecimal transferAmount = new BigDecimal("2000.00");

        when(treasuryRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(treasuryRepository.findById(2L)).thenReturn(Optional.of(toAccount));

        assertThrows(IllegalArgumentException.class,
                () -> treasuryService.transferFunds(1L, 2L, transferAmount));

        verify(treasuryRepository, never()).save(any());
    }

    @Test
    void transferFunds_ShouldThrowException_WhenNegativeAmount() {
        BigDecimal transferAmount = new BigDecimal("-100.00");

        assertThrows(IllegalArgumentException.class,
                () -> treasuryService.transferFunds(1L, 2L, transferAmount));

        verify(treasuryRepository, never()).findById(any());
    }
}