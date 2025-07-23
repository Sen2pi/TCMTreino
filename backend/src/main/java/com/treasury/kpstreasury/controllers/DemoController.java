package com.treasury.kpstreasury.controllers;

import com.treasury.kpstreasury.models.dto.UserDto;
import com.treasury.kpstreasury.models.dto.TreasuryDto;
import com.treasury.kpstreasury.models.dto.CollateralDto;
import com.treasury.kpstreasury.services.UserService;
import com.treasury.kpstreasury.services.TreasuryService;
import com.treasury.kpstreasury.services.CollateralService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/demo")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DemoController {

    private final UserService userService;
    private final TreasuryService treasuryService;
    private final CollateralService collateralService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "KPS Treasury Management System");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/treasury")
    public ResponseEntity<List<TreasuryDto>> getAllTreasuryAccounts() {
        List<TreasuryDto> treasuries = treasuryService.getAllTreasuryAccounts();
        return ResponseEntity.ok(treasuries);
    }

    @GetMapping("/collateral")
    public ResponseEntity<List<CollateralDto>> getAllCollaterals() {
        List<CollateralDto> collaterals = collateralService.getAllCollaterals();
        return ResponseEntity.ok(collaterals);
    }

    @GetMapping("/reports/treasury-summary")
    public ResponseEntity<Map<String, Object>> getTreasurySummary() {
        Map<String, Object> summary = new HashMap<>();
        
        BigDecimal totalAvailableBalance = treasuryService.getTotalAvailableBalance();
        long totalAccounts = treasuryService.getAllTreasuryAccounts().size();
        
        summary.put("totalAvailableBalance", totalAvailableBalance);
        summary.put("totalAccounts", totalAccounts);
        summary.put("currency", "EUR");
        
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/reports/collateral-summary")
    public ResponseEntity<Map<String, Object>> getCollateralSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        BigDecimal totalEligibleValue = collateralService.getTotalEligibleValue();
        long totalCollaterals = collateralService.getAllCollaterals().size();
        
        summary.put("totalEligibleValue", totalEligibleValue);
        summary.put("totalCollaterals", totalCollaterals);
        
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalUsers", userService.getAllUsers().size());
        stats.put("totalTreasuryAccounts", treasuryService.getAllTreasuryAccounts().size());
        stats.put("totalCollaterals", collateralService.getAllCollaterals().size());
        stats.put("totalAvailableBalance", treasuryService.getTotalAvailableBalance());
        stats.put("totalEligibleValue", collateralService.getTotalEligibleValue());
        
        return ResponseEntity.ok(stats);
    }
}