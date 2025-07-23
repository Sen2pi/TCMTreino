package com.treasury.kpstreasury.controllers;

import com.treasury.kpstreasury.enums.CollateralStatus;
import com.treasury.kpstreasury.enums.CollateralType;
import com.treasury.kpstreasury.enums.Rating;
import com.treasury.kpstreasury.models.dto.CollateralDto;
import com.treasury.kpstreasury.models.dto.CollateralSummaryDto;
import com.treasury.kpstreasury.services.CollateralService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/collateral")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CollateralController {

    private final CollateralService collateralService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER')")
    public ResponseEntity<CollateralDto> createCollateral(@Valid @RequestBody CollateralDto collateralDto) {
        CollateralDto createdCollateral = collateralService.createCollateral(collateralDto);
        return new ResponseEntity<>(createdCollateral, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER') or hasRole('COLLATERAL_VIEWER')")
    public ResponseEntity<CollateralDto> getCollateralById(@PathVariable Long id) {
        Optional<CollateralDto> collateral = collateralService.getCollateralById(id);
        return collateral.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER') or hasRole('COLLATERAL_VIEWER')")
    public ResponseEntity<List<CollateralDto>> getAllCollaterals() {
        List<CollateralDto> collaterals = collateralService.getAllCollaterals();
        return ResponseEntity.ok(collaterals);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER') or hasRole('COLLATERAL_VIEWER')")
    public ResponseEntity<List<CollateralDto>> getCollateralsByStatus(@PathVariable CollateralStatus status) {
        List<CollateralDto> collaterals = collateralService.getCollateralsByStatus(status);
        return ResponseEntity.ok(collaterals);
    }

    @GetMapping("/type/{collateralType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER') or hasRole('COLLATERAL_VIEWER')")
    public ResponseEntity<List<CollateralDto>> getCollateralsByType(@PathVariable CollateralType collateralType) {
        List<CollateralDto> collaterals = collateralService.getCollateralsByType(collateralType);
        return ResponseEntity.ok(collaterals);
    }

    @GetMapping("/rating/{rating}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER') or hasRole('COLLATERAL_VIEWER')")
    public ResponseEntity<List<CollateralDto>> getCollateralsByRating(@PathVariable Rating rating) {
        List<CollateralDto> collaterals = collateralService.getCollateralsByRating(rating);
        return ResponseEntity.ok(collaterals);
    }

    @GetMapping("/currency/{currency}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER') or hasRole('COLLATERAL_VIEWER')")
    public ResponseEntity<List<CollateralDto>> getCollateralsByCurrency(@PathVariable String currency) {
        List<CollateralDto> collaterals = collateralService.getCollateralsByCurrency(currency);
        return ResponseEntity.ok(collaterals);
    }

    @GetMapping("/counterparty/{counterparty}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER') or hasRole('COLLATERAL_VIEWER')")
    public ResponseEntity<List<CollateralDto>> getCollateralsByCounterparty(@PathVariable String counterparty) {
        List<CollateralDto> collaterals = collateralService.getCollateralsByCounterparty(counterparty);
        return ResponseEntity.ok(collaterals);
    }

    @GetMapping("/eligible-by-rating")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER') or hasRole('COLLATERAL_VIEWER')")
    public ResponseEntity<List<CollateralDto>> getEligibleCollateralsByRating(@RequestParam List<Rating> acceptableRatings) {
        List<CollateralDto> collaterals = collateralService.getEligibleCollateralsByRating(acceptableRatings);
        return ResponseEntity.ok(collaterals);
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER') or hasRole('COLLATERAL_VIEWER')")
    public ResponseEntity<List<CollateralDto>> getCollateralsExpiringBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<CollateralDto> collaterals = collateralService.getCollateralsExpiringBetween(startDate, endDate);
        return ResponseEntity.ok(collaterals);
    }

    @GetMapping("/expiring-in-days")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER') or hasRole('COLLATERAL_VIEWER')")
    public ResponseEntity<List<CollateralDto>> getCollateralsExpiringInDays(@RequestParam int days) {
        List<CollateralDto> collaterals = collateralService.getCollateralsExpiringInDays(days);
        return ResponseEntity.ok(collaterals);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER') or hasRole('COLLATERAL_VIEWER')")
    public ResponseEntity<Page<CollateralDto>> getCollateralsWithAdvancedFilters(
            @RequestParam(required = false) CollateralType type,
            @RequestParam(required = false) Rating minRating,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) CollateralStatus status,
            @RequestParam(required = false) BigDecimal minValue,
            Pageable pageable) {
        Page<CollateralDto> collaterals = collateralService.getCollateralsWithAdvancedFilters(
                type, minRating, currency, status, minValue, pageable);
        return ResponseEntity.ok(collaterals);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER')")
    public ResponseEntity<CollateralDto> updateCollateral(@PathVariable Long id, @Valid @RequestBody CollateralDto collateralDto) {
        try {
            CollateralDto updatedCollateral = collateralService.updateCollateral(id, collateralDto);
            return ResponseEntity.ok(updatedCollateral);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCollateral(@PathVariable Long id) {
        try {
            collateralService.deleteCollateral(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/market-value")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER')")
    public ResponseEntity<CollateralDto> updateMarketValue(@PathVariable Long id, @RequestBody BigDecimal newMarketValue) {
        try {
            CollateralDto updatedCollateral = collateralService.updateMarketValue(id, newMarketValue);
            return ResponseEntity.ok(updatedCollateral);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER')")
    public ResponseEntity<CollateralDto> updateStatus(@PathVariable Long id, @RequestBody CollateralStatus newStatus) {
        try {
            CollateralDto updatedCollateral = collateralService.updateStatus(id, newStatus);
            return ResponseEntity.ok(updatedCollateral);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/mark-eligible")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER')")
    public ResponseEntity<Void> markAsEligible(@PathVariable Long id) {
        try {
            collateralService.markAsEligible(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/mark-ineligible")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER')")
    public ResponseEntity<Void> markAsIneligible(@PathVariable Long id) {
        try {
            collateralService.markAsIneligible(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/mark-matured")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER')")
    public ResponseEntity<Void> markAsMatured(@PathVariable Long id) {
        try {
            collateralService.markAsMatured(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/reports/total-eligible-value")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER') or hasRole('COLLATERAL_VIEWER')")
    public ResponseEntity<BigDecimal> getTotalEligibleValue() {
        BigDecimal total = collateralService.getTotalEligibleValue();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/reports/summary-by-type")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER') or hasRole('COLLATERAL_VIEWER')")
    public ResponseEntity<List<CollateralSummaryDto>> getCollateralSummaryByType() {
        List<CollateralSummaryDto> summary = collateralService.getCollateralSummaryByType();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/reports/concentration-by-rating")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER') or hasRole('COLLATERAL_VIEWER')")
    public ResponseEntity<List<CollateralSummaryDto>> getCollateralConcentrationByRating() {
        List<CollateralSummaryDto> concentration = collateralService.getCollateralConcentrationByRating();
        return ResponseEntity.ok(concentration);
    }

    @GetMapping("/reports/high-risk")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER') or hasRole('COLLATERAL_VIEWER')")
    public ResponseEntity<List<CollateralDto>> getHighRiskCollaterals(@RequestParam(required = false) BigDecimal haircutThreshold) {
        List<CollateralDto> collaterals;
        if (haircutThreshold != null) {
            collaterals = collateralService.getHighRiskCollaterals(haircutThreshold);
        } else {
            collaterals = collateralService.getHighRiskCollaterals();
        }
        return ResponseEntity.ok(collaterals);
    }

    @PutMapping("/{id}/revalue")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER')")
    public ResponseEntity<CollateralDto> revalueCollateral(
            @PathVariable Long id,
            @RequestParam BigDecimal newMarketValue,
            @RequestParam BigDecimal newHaircut) {
        try {
            CollateralDto updatedCollateral = collateralService.revalueCollateral(id, newMarketValue, newHaircut);
            return ResponseEntity.ok(updatedCollateral);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/reports/total-risk-exposure")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER') or hasRole('COLLATERAL_VIEWER')")
    public ResponseEntity<BigDecimal> calculateTotalRiskExposure() {
        BigDecimal riskExposure = collateralService.calculateTotalRiskExposure();
        return ResponseEntity.ok(riskExposure);
    }

    @GetMapping("/reports/average-haircut-by-type/{type}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER') or hasRole('COLLATERAL_VIEWER')")
    public ResponseEntity<BigDecimal> getAverageHaircutByType(@PathVariable CollateralType type) {
        BigDecimal averageHaircut = collateralService.getAverageHaircutByType(type);
        return ResponseEntity.ok(averageHaircut);
    }

    @PostMapping("/process-matured")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COLLATERAL_MANAGER')")
    public ResponseEntity<Void> processMaturedCollaterals() {
        collateralService.processMaturedCollaterals();
        return ResponseEntity.ok().build();
    }
}