package com.treasury.kpstreasury.controllers;

import com.treasury.kpstreasury.enums.AccountStatus;
import com.treasury.kpstreasury.enums.AccountType;
import com.treasury.kpstreasury.models.dto.TreasuryDto;
import com.treasury.kpstreasury.models.dto.TreasurySummaryDto;
import com.treasury.kpstreasury.services.TreasuryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/treasury")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TreasuryController {

    private final TreasuryService treasuryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY_MANAGER')")
    public ResponseEntity<TreasuryDto> createTreasuryAccount(@Valid @RequestBody TreasuryDto treasuryDto) {
        try {
            TreasuryDto createdAccount = treasuryService.createTreasuryAccount(treasuryDto);
            return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY_MANAGER') or hasRole('TREASURY_VIEWER')")
    public ResponseEntity<TreasuryDto> getTreasuryById(@PathVariable Long id) {
        Optional<TreasuryDto> treasury = treasuryService.getTreasuryById(id);
        return treasury.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/account/{accountNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY_MANAGER') or hasRole('TREASURY_VIEWER')")
    public ResponseEntity<TreasuryDto> getTreasuryByAccountNumber(@PathVariable String accountNumber) {
        Optional<TreasuryDto> treasury = treasuryService.getTreasuryByAccountNumber(accountNumber);
        return treasury.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY_MANAGER') or hasRole('TREASURY_VIEWER')")
    public ResponseEntity<List<TreasuryDto>> getAllTreasuryAccounts() {
        List<TreasuryDto> treasuries = treasuryService.getAllTreasuryAccounts();
        return ResponseEntity.ok(treasuries);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY_MANAGER') or hasRole('TREASURY_VIEWER')")
    public ResponseEntity<List<TreasuryDto>> getTreasuryAccountsByStatus(@PathVariable AccountStatus status) {
        List<TreasuryDto> treasuries = treasuryService.getTreasuryAccountsByStatus(status);
        return ResponseEntity.ok(treasuries);
    }

    @GetMapping("/type/{accountType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY_MANAGER') or hasRole('TREASURY_VIEWER')")
    public ResponseEntity<List<TreasuryDto>> getTreasuryAccountsByType(@PathVariable AccountType accountType) {
        List<TreasuryDto> treasuries = treasuryService.getTreasuryAccountsByType(accountType);
        return ResponseEntity.ok(treasuries);
    }

    @GetMapping("/currency/{currency}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY_MANAGER') or hasRole('TREASURY_VIEWER')")
    public ResponseEntity<List<TreasuryDto>> getTreasuryAccountsByCurrency(@PathVariable String currency) {
        List<TreasuryDto> treasuries = treasuryService.getTreasuryAccountsByCurrency(currency);
        return ResponseEntity.ok(treasuries);
    }

    @GetMapping("/bank/{bankName}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY_MANAGER') or hasRole('TREASURY_VIEWER')")
    public ResponseEntity<List<TreasuryDto>> getTreasuryAccountsByBank(@PathVariable String bankName) {
        List<TreasuryDto> treasuries = treasuryService.getTreasuryAccountsByBank(bankName);
        return ResponseEntity.ok(treasuries);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY_MANAGER') or hasRole('TREASURY_VIEWER')")
    public ResponseEntity<Page<TreasuryDto>> getTreasuryAccountsWithFilters(
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String bankName,
            @RequestParam(required = false) AccountStatus status,
            Pageable pageable) {
        Page<TreasuryDto> treasuries = treasuryService.getTreasuryAccountsWithFilters(
                currency, bankName, status, pageable);
        return ResponseEntity.ok(treasuries);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY_MANAGER')")
    public ResponseEntity<TreasuryDto> updateTreasuryAccount(@PathVariable Long id, @Valid @RequestBody TreasuryDto treasuryDto) {
        try {
            TreasuryDto updatedTreasury = treasuryService.updateTreasuryAccount(id, treasuryDto);
            return ResponseEntity.ok(updatedTreasury);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTreasuryAccount(@PathVariable Long id) {
        try {
            treasuryService.deleteTreasuryAccount(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/balance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY_MANAGER')")
    public ResponseEntity<TreasuryDto> updateBalance(@PathVariable Long id, @RequestBody BigDecimal newBalance) {
        try {
            TreasuryDto updatedTreasury = treasuryService.updateBalance(id, newBalance);
            return ResponseEntity.ok(updatedTreasury);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/available-balance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY_MANAGER')")
    public ResponseEntity<TreasuryDto> updateAvailableBalance(@PathVariable Long id, @RequestBody BigDecimal newAvailableBalance) {
        try {
            TreasuryDto updatedTreasury = treasuryService.updateAvailableBalance(id, newAvailableBalance);
            return ResponseEntity.ok(updatedTreasury);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY_MANAGER')")
    public ResponseEntity<Void> activateAccount(@PathVariable Long id) {
        try {
            treasuryService.activateAccount(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY_MANAGER')")
    public ResponseEntity<Void> deactivateAccount(@PathVariable Long id) {
        try {
            treasuryService.deactivateAccount(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/suspend")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY_MANAGER')")
    public ResponseEntity<Void> suspendAccount(@PathVariable Long id) {
        try {
            treasuryService.suspendAccount(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/reports/total-balance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY_MANAGER') or hasRole('TREASURY_VIEWER')")
    public ResponseEntity<BigDecimal> getTotalBalanceByCurrencyAndStatus(
            @RequestParam String currency,
            @RequestParam AccountStatus status) {
        BigDecimal total = treasuryService.getTotalBalanceByCurrencyAndStatus(currency, status);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/reports/total-available-balance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY_MANAGER') or hasRole('TREASURY_VIEWER')")
    public ResponseEntity<BigDecimal> getTotalAvailableBalance() {
        BigDecimal total = treasuryService.getTotalAvailableBalance();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/reports/summary-by-currency")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY_MANAGER') or hasRole('TREASURY_VIEWER')")
    public ResponseEntity<List<TreasurySummaryDto>> getTreasurySummaryByCurrency() {
        List<TreasurySummaryDto> summary = treasuryService.getTreasurySummaryByCurrency();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/reports/low-balance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY_MANAGER') or hasRole('TREASURY_VIEWER')")
    public ResponseEntity<List<TreasuryDto>> getLowBalanceAccounts(@RequestParam BigDecimal threshold) {
        List<TreasuryDto> accounts = treasuryService.getLowBalanceAccounts(threshold);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/check-account-number")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY_MANAGER')")
    public ResponseEntity<Boolean> isAccountNumberAvailable(@RequestParam String accountNumber) {
        boolean available = treasuryService.isAccountNumberAvailable(accountNumber);
        return ResponseEntity.ok(available);
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TREASURY_MANAGER')")
    public ResponseEntity<TreasuryDto> transferFunds(
            @RequestParam Long fromAccountId,
            @RequestParam Long toAccountId,
            @RequestParam BigDecimal amount) {
        try {
            TreasuryDto result = treasuryService.transferFunds(fromAccountId, toAccountId, amount);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}