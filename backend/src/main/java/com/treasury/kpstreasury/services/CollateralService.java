package com.treasury.KPStreasury.services;

import com.treasury.KPStreasury.exceptions.BusinessException;
import com.treasury.KPStreasury.mappers.CollateralMapper;
import com.treasury.KPStreasury.models.dto.CollateralDto;
import com.treasury.KPStreasury.models.entity.Collateral;
import com.treasury.KPStreasury.repository.CollateralRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CollateralService {
    
    private final CollateralRepository collateralRepository;
    private final CollateralMapper collateralMapper;
    private final NotificationService notificationService;
    
    @Transactional(readOnly = true)
    public Page<CollateralDto> findAll(Pageable pageable) {
        log.debug("Finding all collateral with pagination: {}", pageable);
        return collateralRepository.findAll(pageable)
                .map(collateralMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public CollateralDto findById(Long id) {
        log.debug("Finding collateral by id: {}", id);
        Collateral collateral = collateralRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Collateral not found with id: " + id));
        return collateralMapper.toDto(collateral);
    }
    
    public CollateralDto create(CollateralDto collateralDto) {
        log.debug("Creating new collateral: {}", collateralDto.getDescription());
        
        validateCollateralData(collateralDto);
        calculateEligibleValue(collateralDto);
        
        Collateral collateral = collateralMapper.toEntity(collateralDto);
        Collateral saved = collateralRepository.save(collateral);
        
        notificationService.sendCollateralCreatedNotification(saved);
        log.info("Collateral created successfully: {}", saved.getDescription());
        
        return collateralMapper.toDto(saved);
    }
    
    public CollateralDto update(Long id, CollateralDto collateralDto) {
        log.debug("Updating collateral with id: {}", id);
        
        Collateral existing = collateralRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Collateral not found with id: " + id));
        
        validateCollateralData(collateralDto);
        calculateEligibleValue(collateralDto);
        
        collateralMapper.updateEntityFromDto(collateralDto, existing);
        Collateral updated = collateralRepository.save(existing);
        
        notificationService.sendCollateralUpdatedNotification(updated);
        log.info("Collateral updated successfully: {}", updated.getDescription());
        
        return collateralMapper.toDto(updated);
    }
    
    public void delete(Long id) {
        log.debug("Deleting collateral with id: {}", id);
        
        Collateral collateral = collateralRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Collateral not found with id: " + id));
        
        collateralRepository.delete(collateral);
        
        notificationService.sendCollateralDeletedNotification(collateral);
        log.info("Collateral deleted successfully: {}", collateral.getDescription());
    }
    
    @Transactional(readOnly = true)
    public List<CollateralDto> findByStatus(String status) {
        log.debug("Finding collateral by status: {}", status);
        return collateralRepository.findByStatus(status)
                .stream()
                .map(collateralMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<CollateralDto> findByCollateralType(String collateralType) {
        log.debug("Finding collateral by type: {}", collateralType);
        return collateralRepository.findByCollateralType(collateralType)
                .stream()
                .map(collateralMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<CollateralDto> getEligibleCollateral() {
        log.debug("Finding eligible collateral");
        return collateralRepository.findByStatusAndMaturityDateAfter("ELIGIBLE", LocalDate.now())
                .stream()
                .map(collateralMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<CollateralDto> getMaturingCollateral(int daysAhead) {
        log.debug("Finding collateral maturing in {} days", daysAhead);
        LocalDate maturityDate = LocalDate.now().plusDays(daysAhead);
        return collateralRepository.findByMaturityDateBefore(maturityDate)
                .stream()
                .map(collateralMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getTotalEligibleValueByCurrency(String currency) {
        log.debug("Calculating total eligible value for currency: {}", currency);
        return collateralRepository.sumEligibleValueByCurrency(currency);
    }
    
    @Transactional(readOnly = true)
    public List<Object> getCollateralSummary() {
        log.debug("Getting collateral summary");
        return collateralRepository.getCollateralSummaryByType();
    }
    
    public void performDailyValuation() {
        log.info("Starting daily collateral valuation");
        List<Collateral> allCollateral = collateralRepository.findAll();
        
        for (Collateral collateral : allCollateral) {
            try {
                BigDecimal newMarketValue = fetchMarketValue(collateral);
                if (newMarketValue != null && !newMarketValue.equals(collateral.getMarketValue())) {
                    collateral.setMarketValue(newMarketValue);
                    calculateAndSetEligibleValue(collateral);
                    collateralRepository.save(collateral);
                    
                    log.debug("Updated market value for collateral {}: {}", 
                             collateral.getId(), newMarketValue);
                }
            } catch (Exception e) {
                log.error("Error updating market value for collateral {}: {}", 
                         collateral.getId(), e.getMessage());
            }
        }
        
        log.info("Daily collateral valuation completed");
    }
    
    private void validateCollateralData(CollateralDto collateralDto) {
        if (collateralDto.getDescription() == null || collateralDto.getDescription().trim().isEmpty()) {
            throw new BusinessException("Description is required");
        }
        
        if (collateralDto.getCollateralType() == null || collateralDto.getCollateralType().trim().isEmpty()) {
            throw new BusinessException("Collateral type is required");
        }
        
        if (collateralDto.getMarketValue() == null || collateralDto.getMarketValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Market value must be positive");
        }
        
        if (collateralDto.getHaircut() == null || 
            collateralDto.getHaircut().compareTo(BigDecimal.ZERO) < 0 || 
            collateralDto.getHaircut().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BusinessException("Haircut must be between 0 and 100");
        }
        
        if (collateralDto.getMaturityDate() == null) {
            throw new BusinessException("Maturity date is required");
        }
        
        if (collateralDto.getMaturityDate().isBefore(LocalDate.now())) {
            throw new BusinessException("Maturity date cannot be in the past");
        }
        
        if (collateralDto.getCurrency() == null || collateralDto.getCurrency().trim().isEmpty()) {
            throw new BusinessException("Currency is required");
        }
        
        if (collateralDto.getCounterparty() == null || collateralDto.getCounterparty().trim().isEmpty()) {
            throw new BusinessException("Counterparty is required");
        }
    }
    
    private void calculateEligibleValue(CollateralDto collateralDto) {
        if (collateralDto.getMarketValue() != null && collateralDto.getHaircut() != null) {
            BigDecimal haircutMultiplier = BigDecimal.ONE.subtract(
                collateralDto.getHaircut().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
            );
            BigDecimal eligibleValue = collateralDto.getMarketValue()
                .multiply(haircutMultiplier)
                .setScale(2, RoundingMode.HALF_UP);
            collateralDto.setEligibleValue(eligibleValue);
        }
    }
    
    private void calculateAndSetEligibleValue(Collateral collateral) {
        if (collateral.getMarketValue() != null && collateral.getHaircut() != null) {
            BigDecimal haircutMultiplier = BigDecimal.ONE.subtract(
                collateral.getHaircut().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
            );
            BigDecimal eligibleValue = collateral.getMarketValue()
                .multiply(haircutMultiplier)
                .setScale(2, RoundingMode.HALF_UP);
            collateral.setEligibleValue(eligibleValue);
        }
    }
    
    private BigDecimal fetchMarketValue(Collateral collateral) {
        // Simulate market data fetch - in real implementation, this would call external market data service
        return collateral.getMarketValue().multiply(
            BigDecimal.valueOf(0.98 + Math.random() * 0.04)
        ).setScale(2, RoundingMode.HALF_UP);
    }
}
