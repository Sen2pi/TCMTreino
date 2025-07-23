package com.treasury.kpstreasury.services;

import com.treasury.kpstreasury.enums.CollateralStatus;
import com.treasury.kpstreasury.enums.CollateralType;
import com.treasury.kpstreasury.enums.Rating;
import com.treasury.kpstreasury.models.dto.CollateralDto;
import com.treasury.kpstreasury.models.dto.CollateralSummaryDto;
import com.treasury.kpstreasury.models.entity.CollateralEntity;
import com.treasury.kpstreasury.repositories.CollateralRepository;
import com.treasury.kpstreasury.utils.CollateralMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CollateralService {

    private final CollateralRepository collateralRepository;
    private final CollateralMapper collateralMapper;

    public CollateralDto createCollateral(CollateralDto collateralDto) {
        CollateralEntity collateralEntity = collateralMapper.toEntity(collateralDto);
        CollateralEntity savedCollateral = collateralRepository.save(collateralEntity);
        return collateralMapper.toDto(savedCollateral);
    }

    @Transactional(readOnly = true)
    public Optional<CollateralDto> getCollateralById(Long id) {
        return collateralRepository.findById(id)
                .map(collateralMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<CollateralDto> getAllCollaterals() {
        return collateralRepository.findAll()
                .stream()
                .map(collateralMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CollateralDto> getCollateralsByStatus(CollateralStatus status) {
        return collateralRepository.findByStatus(status)
                .stream()
                .map(collateralMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CollateralDto> getCollateralsByType(CollateralType collateralType) {
        return collateralRepository.findByCollateralType(collateralType)
                .stream()
                .map(collateralMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CollateralDto> getCollateralsByRating(Rating rating) {
        return collateralRepository.findByRating(rating)
                .stream()
                .map(collateralMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CollateralDto> getCollateralsByCurrency(String currency) {
        return collateralRepository.findByCurrency(currency)
                .stream()
                .map(collateralMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CollateralDto> getCollateralsByCounterparty(String counterparty) {
        return collateralRepository.findByCounterparty(counterparty)
                .stream()
                .map(collateralMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CollateralDto> getEligibleCollateralsByRating(List<Rating> acceptableRatings) {
        return collateralRepository.findByStatusAndRatingIn(CollateralStatus.ELIGIBLE, acceptableRatings)
                .stream()
                .map(collateralMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CollateralDto> getCollateralsExpiringBetween(LocalDate startDate, LocalDate endDate) {
        return collateralRepository.findByMaturityDateBetween(startDate, endDate)
                .stream()
                .map(collateralMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CollateralDto> getCollateralsExpiringInDays(int days) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        return getCollateralsExpiringBetween(today, futureDate);
    }

    @Transactional(readOnly = true)
    public Page<CollateralDto> getCollateralsWithAdvancedFilters(CollateralType type, Rating minRating, 
                                                               String currency, CollateralStatus status, 
                                                               BigDecimal minValue, Pageable pageable) {
        return collateralRepository.findWithAdvancedFilters(type, minRating, currency, status, minValue, pageable)
                .map(collateralMapper::toDto);
    }

    public CollateralDto updateCollateral(Long id, CollateralDto collateralDto) {
        CollateralEntity existingCollateral = collateralRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Collateral not found with id: " + id));

        CollateralEntity updatedCollateral = collateralMapper.updateEntity(existingCollateral, collateralDto);
        CollateralEntity savedCollateral = collateralRepository.save(updatedCollateral);
        return collateralMapper.toDto(savedCollateral);
    }

    public void deleteCollateral(Long id) {
        if (!collateralRepository.existsById(id)) {
            throw new IllegalArgumentException("Collateral not found with id: " + id);
        }
        collateralRepository.deleteById(id);
    }

    public CollateralDto updateMarketValue(Long id, BigDecimal newMarketValue) {
        CollateralEntity collateral = collateralRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Collateral not found with id: " + id));

        collateral.setMarketValue(newMarketValue);
        CollateralEntity savedCollateral = collateralRepository.save(collateral);
        return collateralMapper.toDto(savedCollateral);
    }

    public CollateralDto updateStatus(Long id, CollateralStatus newStatus) {
        CollateralEntity collateral = collateralRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Collateral not found with id: " + id));

        collateral.setStatus(newStatus);
        CollateralEntity savedCollateral = collateralRepository.save(collateral);
        return collateralMapper.toDto(savedCollateral);
    }

    public void markAsEligible(Long id) {
        updateStatus(id, CollateralStatus.ELIGIBLE);
    }

    public void markAsIneligible(Long id) {
        updateStatus(id, CollateralStatus.INELIGIBLE);
    }

    public void markAsMatured(Long id) {
        updateStatus(id, CollateralStatus.MATURED);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalEligibleValue() {
        BigDecimal total = collateralRepository.getTotalEligibleValue();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public List<CollateralSummaryDto> getCollateralSummaryByType() {
        List<Object[]> results = collateralRepository.getCollateralSummaryByType();
        return results.stream()
                .map(row -> {
                    CollateralType type = (CollateralType) row[0];
                    BigDecimal totalMarketValue = (BigDecimal) row[1];
                    BigDecimal totalEligibleValue = (BigDecimal) row[2];
                    
                    CollateralSummaryDto summary = new CollateralSummaryDto();
                    summary.setCollateralType(type);
                    summary.setTotalMarketValue(totalMarketValue);
                    summary.setTotalEligibleValue(totalEligibleValue);
                    
                    return summary;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CollateralSummaryDto> getCollateralConcentrationByRating() {
        List<Object[]> results = collateralRepository.getCollateralConcentrationByRating();
        return results.stream()
                .map(row -> {
                    Rating rating = (Rating) row[0];
                    Long count = (Long) row[1];
                    BigDecimal totalMarketValue = (BigDecimal) row[2];
                    
                    CollateralSummaryDto summary = new CollateralSummaryDto();
                    summary.setRating(rating);
                    summary.setCount(count);
                    summary.setTotalMarketValue(totalMarketValue);
                    
                    return summary;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CollateralDto> getHighRiskCollaterals(BigDecimal haircutThreshold) {
        return collateralRepository.findHighRiskCollateral(haircutThreshold)
                .stream()
                .map(collateralMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CollateralDto> getHighRiskCollaterals() {
        BigDecimal defaultThreshold = new BigDecimal("0.15"); // 15%
        return getHighRiskCollaterals(defaultThreshold);
    }

    public CollateralDto revalueCollateral(Long id, BigDecimal newMarketValue, BigDecimal newHaircut) {
        CollateralEntity collateral = collateralRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Collateral not found with id: " + id));

        collateral.setMarketValue(newMarketValue);
        collateral.setHaircut(newHaircut);
        
        CollateralEntity savedCollateral = collateralRepository.save(collateral);
        return collateralMapper.toDto(savedCollateral);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateTotalRiskExposure() {
        List<CollateralEntity> eligibleCollaterals = collateralRepository.findByStatus(CollateralStatus.ELIGIBLE);
        
        return eligibleCollaterals.stream()
                .map(collateral -> {
                    BigDecimal riskValue = collateral.getMarketValue().subtract(collateral.getEligibleValue());
                    return riskValue;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional(readOnly = true)
    public BigDecimal getAverageHaircutByType(CollateralType type) {
        List<CollateralEntity> collaterals = collateralRepository.findByCollateralType(type);
        
        if (collaterals.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalHaircut = collaterals.stream()
                .map(CollateralEntity::getHaircut)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return totalHaircut.divide(BigDecimal.valueOf(collaterals.size()), 4, RoundingMode.HALF_UP);
    }

    public void processMaturedCollaterals() {
        LocalDate today = LocalDate.now();
        List<CollateralEntity> maturedCollaterals = collateralRepository.findByMaturityDateBetween(
            LocalDate.of(1900, 1, 1), today);
        
        maturedCollaterals.forEach(collateral -> {
            if (!collateral.getStatus().equals(CollateralStatus.MATURED)) {
                collateral.setStatus(CollateralStatus.MATURED);
                collateralRepository.save(collateral);
            }
        });
    }
}