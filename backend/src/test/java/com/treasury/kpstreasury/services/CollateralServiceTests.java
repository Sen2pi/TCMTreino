package com.treasury.kpstreasury.services;

import com.treasury.kpstreasury.enums.CollateralStatus;
import com.treasury.kpstreasury.enums.CollateralType;
import com.treasury.kpstreasury.enums.Rating;
import com.treasury.kpstreasury.models.dto.CollateralDto;
import com.treasury.kpstreasury.models.dto.CollateralSummaryDto;
import com.treasury.kpstreasury.models.entity.CollateralEntity;
import com.treasury.kpstreasury.repositories.CollateralRepository;
import com.treasury.kpstreasury.utils.CollateralMapper;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollateralServiceTests {

    @Mock
    private CollateralRepository collateralRepository;

    @Mock
    private CollateralMapper collateralMapper;

    @InjectMocks
    private CollateralService collateralService;

    private CollateralEntity collateralEntity;
    private CollateralDto collateralDto;

    @BeforeEach
    void setUp() {
        collateralEntity = CollateralEntity.builder()
                .id(1L)
                .collateralType(CollateralType.GOVERNMENT_BOND)
                .description("Government Bond XYZ")
                .marketValue(new BigDecimal("100000.00"))
                .haircut(new BigDecimal("0.0500"))
                .eligibleValue(new BigDecimal("95000.00"))
                .currency("EUR")
                .rating(Rating.AAA)
                .maturityDate(LocalDate.now().plusYears(5))
                .status(CollateralStatus.ELIGIBLE)
                .counterparty("Government XYZ")
                .location("Central Bank")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        collateralDto = new CollateralDto(
                1L,
                CollateralType.GOVERNMENT_BOND,
                "Government Bond XYZ",
                new BigDecimal("100000.00"),
                new BigDecimal("0.0500"),
                new BigDecimal("95000.00"),
                "EUR",
                Rating.AAA,
                LocalDate.now().plusYears(5),
                CollateralStatus.ELIGIBLE,
                "Government XYZ",
                "Central Bank",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void createCollateral_ShouldCreateCollateral_WhenValidData() {
        when(collateralMapper.toEntity(collateralDto)).thenReturn(collateralEntity);
        when(collateralRepository.save(collateralEntity)).thenReturn(collateralEntity);
        when(collateralMapper.toDto(collateralEntity)).thenReturn(collateralDto);

        CollateralDto result = collateralService.createCollateral(collateralDto);

        assertNotNull(result);
        assertEquals(CollateralType.GOVERNMENT_BOND, result.getCollateralType());
        verify(collateralRepository).save(collateralEntity);
    }

    @Test
    void getCollateralById_ShouldReturnCollateral_WhenCollateralExists() {
        when(collateralRepository.findById(1L)).thenReturn(Optional.of(collateralEntity));
        when(collateralMapper.toDto(collateralEntity)).thenReturn(collateralDto);

        Optional<CollateralDto> result = collateralService.getCollateralById(1L);

        assertTrue(result.isPresent());
        assertEquals(CollateralType.GOVERNMENT_BOND, result.get().getCollateralType());
        verify(collateralRepository).findById(1L);
    }

    @Test
    void getCollateralById_ShouldReturnEmpty_WhenCollateralNotFound() {
        when(collateralRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<CollateralDto> result = collateralService.getCollateralById(1L);

        assertFalse(result.isPresent());
        verify(collateralRepository).findById(1L);
    }

    @Test
    void getAllCollaterals_ShouldReturnAllCollaterals() {
        List<CollateralEntity> collateralEntities = Arrays.asList(collateralEntity);
        when(collateralRepository.findAll()).thenReturn(collateralEntities);
        when(collateralMapper.toDto(collateralEntity)).thenReturn(collateralDto);

        List<CollateralDto> result = collateralService.getAllCollaterals();

        assertEquals(1, result.size());
        assertEquals(CollateralType.GOVERNMENT_BOND, result.get(0).getCollateralType());
        verify(collateralRepository).findAll();
    }

    @Test
    void getCollateralsByStatus_ShouldReturnCollateralsByStatus() {
        List<CollateralEntity> collateralEntities = Arrays.asList(collateralEntity);
        when(collateralRepository.findByStatus(CollateralStatus.ELIGIBLE)).thenReturn(collateralEntities);
        when(collateralMapper.toDto(collateralEntity)).thenReturn(collateralDto);

        List<CollateralDto> result = collateralService.getCollateralsByStatus(CollateralStatus.ELIGIBLE);

        assertEquals(1, result.size());
        assertEquals(CollateralStatus.ELIGIBLE, result.get(0).getStatus());
        verify(collateralRepository).findByStatus(CollateralStatus.ELIGIBLE);
    }

    @Test
    void getCollateralsByType_ShouldReturnCollateralsByType() {
        List<CollateralEntity> collateralEntities = Arrays.asList(collateralEntity);
        when(collateralRepository.findByCollateralType(CollateralType.GOVERNMENT_BOND)).thenReturn(collateralEntities);
        when(collateralMapper.toDto(collateralEntity)).thenReturn(collateralDto);

        List<CollateralDto> result = collateralService.getCollateralsByType(CollateralType.GOVERNMENT_BOND);

        assertEquals(1, result.size());
        assertEquals(CollateralType.GOVERNMENT_BOND, result.get(0).getCollateralType());
        verify(collateralRepository).findByCollateralType(CollateralType.GOVERNMENT_BOND);
    }

    @Test
    void getCollateralsByRating_ShouldReturnCollateralsByRating() {
        List<CollateralEntity> collateralEntities = Arrays.asList(collateralEntity);
        when(collateralRepository.findByRating(Rating.AAA)).thenReturn(collateralEntities);
        when(collateralMapper.toDto(collateralEntity)).thenReturn(collateralDto);

        List<CollateralDto> result = collateralService.getCollateralsByRating(Rating.AAA);

        assertEquals(1, result.size());
        assertEquals(Rating.AAA, result.get(0).getRating());
        verify(collateralRepository).findByRating(Rating.AAA);
    }

    @Test
    void getCollateralsByCurrency_ShouldReturnCollateralsByCurrency() {
        List<CollateralEntity> collateralEntities = Arrays.asList(collateralEntity);
        when(collateralRepository.findByCurrency("EUR")).thenReturn(collateralEntities);
        when(collateralMapper.toDto(collateralEntity)).thenReturn(collateralDto);

        List<CollateralDto> result = collateralService.getCollateralsByCurrency("EUR");

        assertEquals(1, result.size());
        assertEquals("EUR", result.get(0).getCurrency());
        verify(collateralRepository).findByCurrency("EUR");
    }

    @Test
    void getCollateralsByCounterparty_ShouldReturnCollateralsByCounterparty() {
        List<CollateralEntity> collateralEntities = Arrays.asList(collateralEntity);
        when(collateralRepository.findByCounterparty("Government XYZ")).thenReturn(collateralEntities);
        when(collateralMapper.toDto(collateralEntity)).thenReturn(collateralDto);

        List<CollateralDto> result = collateralService.getCollateralsByCounterparty("Government XYZ");

        assertEquals(1, result.size());
        assertEquals("Government XYZ", result.get(0).getCounterparty());
        verify(collateralRepository).findByCounterparty("Government XYZ");
    }

    @Test
    void getEligibleCollateralsByRating_ShouldReturnEligibleCollaterals() {
        List<Rating> acceptableRatings = Arrays.asList(Rating.AAA, Rating.AA);
        List<CollateralEntity> collateralEntities = Arrays.asList(collateralEntity);
        when(collateralRepository.findByStatusAndRatingIn(CollateralStatus.ELIGIBLE, acceptableRatings))
                .thenReturn(collateralEntities);
        when(collateralMapper.toDto(collateralEntity)).thenReturn(collateralDto);

        List<CollateralDto> result = collateralService.getEligibleCollateralsByRating(acceptableRatings);

        assertEquals(1, result.size());
        assertEquals(CollateralStatus.ELIGIBLE, result.get(0).getStatus());
        verify(collateralRepository).findByStatusAndRatingIn(CollateralStatus.ELIGIBLE, acceptableRatings);
    }

    @Test
    void getCollateralsExpiringBetween_ShouldReturnCollateralsInDateRange() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        List<CollateralEntity> collateralEntities = Arrays.asList(collateralEntity);
        when(collateralRepository.findByMaturityDateBetween(startDate, endDate)).thenReturn(collateralEntities);
        when(collateralMapper.toDto(collateralEntity)).thenReturn(collateralDto);

        List<CollateralDto> result = collateralService.getCollateralsExpiringBetween(startDate, endDate);

        assertEquals(1, result.size());
        verify(collateralRepository).findByMaturityDateBetween(startDate, endDate);
    }

    @Test
    void getCollateralsExpiringInDays_ShouldReturnCollateralsExpiringInSpecifiedDays() {
        int days = 30;
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        List<CollateralEntity> collateralEntities = Arrays.asList(collateralEntity);
        when(collateralRepository.findByMaturityDateBetween(today, futureDate)).thenReturn(collateralEntities);
        when(collateralMapper.toDto(collateralEntity)).thenReturn(collateralDto);

        List<CollateralDto> result = collateralService.getCollateralsExpiringInDays(days);

        assertEquals(1, result.size());
        verify(collateralRepository).findByMaturityDateBetween(today, futureDate);
    }

    @Test
    void getCollateralsWithAdvancedFilters_ShouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CollateralEntity> collateralEntityPage = new PageImpl<>(Arrays.asList(collateralEntity));
        when(collateralRepository.findWithAdvancedFilters(
                CollateralType.GOVERNMENT_BOND, Rating.AAA, "EUR", 
                CollateralStatus.ELIGIBLE, new BigDecimal("50000.00"), pageable))
                .thenReturn(collateralEntityPage);
        when(collateralMapper.toDto(collateralEntity)).thenReturn(collateralDto);

        Page<CollateralDto> result = collateralService.getCollateralsWithAdvancedFilters(
                CollateralType.GOVERNMENT_BOND, Rating.AAA, "EUR", 
                CollateralStatus.ELIGIBLE, new BigDecimal("50000.00"), pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(CollateralType.GOVERNMENT_BOND, result.getContent().get(0).getCollateralType());
        verify(collateralRepository).findWithAdvancedFilters(
                CollateralType.GOVERNMENT_BOND, Rating.AAA, "EUR", 
                CollateralStatus.ELIGIBLE, new BigDecimal("50000.00"), pageable);
    }

    @Test
    void updateCollateral_ShouldUpdateCollateral_WhenValidData() {
        CollateralDto updateDto = new CollateralDto();
        updateDto.setDescription("Updated Description");
        updateDto.setMarketValue(new BigDecimal("120000.00"));

        when(collateralRepository.findById(1L)).thenReturn(Optional.of(collateralEntity));
        when(collateralMapper.updateEntity(collateralEntity, updateDto)).thenReturn(collateralEntity);
        when(collateralRepository.save(collateralEntity)).thenReturn(collateralEntity);
        when(collateralMapper.toDto(collateralEntity)).thenReturn(collateralDto);

        CollateralDto result = collateralService.updateCollateral(1L, updateDto);

        assertNotNull(result);
        verify(collateralRepository).save(collateralEntity);
    }

    @Test
    void updateCollateral_ShouldThrowException_WhenCollateralNotFound() {
        when(collateralRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> collateralService.updateCollateral(1L, collateralDto));

        verify(collateralRepository, never()).save(any());
    }

    @Test
    void deleteCollateral_ShouldDeleteCollateral_WhenCollateralExists() {
        when(collateralRepository.existsById(1L)).thenReturn(true);

        collateralService.deleteCollateral(1L);

        verify(collateralRepository).deleteById(1L);
    }

    @Test
    void deleteCollateral_ShouldThrowException_WhenCollateralNotFound() {
        when(collateralRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> collateralService.deleteCollateral(1L));

        verify(collateralRepository, never()).deleteById(any());
    }

    @Test
    void updateMarketValue_ShouldUpdateMarketValue() {
        BigDecimal newMarketValue = new BigDecimal("110000.00");
        when(collateralRepository.findById(1L)).thenReturn(Optional.of(collateralEntity));
        when(collateralRepository.save(collateralEntity)).thenReturn(collateralEntity);
        when(collateralMapper.toDto(collateralEntity)).thenReturn(collateralDto);

        CollateralDto result = collateralService.updateMarketValue(1L, newMarketValue);

        assertNotNull(result);
        verify(collateralRepository).save(collateralEntity);
        assertEquals(newMarketValue, collateralEntity.getMarketValue());
    }

    @Test
    void updateStatus_ShouldUpdateStatus() {
        when(collateralRepository.findById(1L)).thenReturn(Optional.of(collateralEntity));
        when(collateralRepository.save(collateralEntity)).thenReturn(collateralEntity);
        when(collateralMapper.toDto(collateralEntity)).thenReturn(collateralDto);

        CollateralDto result = collateralService.updateStatus(1L, CollateralStatus.INELIGIBLE);

        assertNotNull(result);
        verify(collateralRepository).save(collateralEntity);
        assertEquals(CollateralStatus.INELIGIBLE, collateralEntity.getStatus());
    }

    @Test
    void markAsEligible_ShouldMarkAsEligible() {
        when(collateralRepository.findById(1L)).thenReturn(Optional.of(collateralEntity));
        when(collateralRepository.save(collateralEntity)).thenReturn(collateralEntity);
        when(collateralMapper.toDto(collateralEntity)).thenReturn(collateralDto);

        collateralService.markAsEligible(1L);

        verify(collateralRepository).save(collateralEntity);
        assertEquals(CollateralStatus.ELIGIBLE, collateralEntity.getStatus());
    }

    @Test
    void markAsIneligible_ShouldMarkAsIneligible() {
        when(collateralRepository.findById(1L)).thenReturn(Optional.of(collateralEntity));
        when(collateralRepository.save(collateralEntity)).thenReturn(collateralEntity);
        when(collateralMapper.toDto(collateralEntity)).thenReturn(collateralDto);

        collateralService.markAsIneligible(1L);

        verify(collateralRepository).save(collateralEntity);
        assertEquals(CollateralStatus.INELIGIBLE, collateralEntity.getStatus());
    }

    @Test
    void markAsMatured_ShouldMarkAsMatured() {
        when(collateralRepository.findById(1L)).thenReturn(Optional.of(collateralEntity));
        when(collateralRepository.save(collateralEntity)).thenReturn(collateralEntity);
        when(collateralMapper.toDto(collateralEntity)).thenReturn(collateralDto);

        collateralService.markAsMatured(1L);

        verify(collateralRepository).save(collateralEntity);
        assertEquals(CollateralStatus.MATURED, collateralEntity.getStatus());
    }

    @Test
    void getTotalEligibleValue_ShouldReturnTotal() {
        BigDecimal expectedTotal = new BigDecimal("500000.00");
        when(collateralRepository.getTotalEligibleValue()).thenReturn(expectedTotal);

        BigDecimal result = collateralService.getTotalEligibleValue();

        assertEquals(expectedTotal, result);
        verify(collateralRepository).getTotalEligibleValue();
    }

    @Test
    void getTotalEligibleValue_ShouldReturnZero_WhenNoResults() {
        when(collateralRepository.getTotalEligibleValue()).thenReturn(null);

        BigDecimal result = collateralService.getTotalEligibleValue();

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void getCollateralSummaryByType_ShouldReturnSummary() {
        Object[] row = {CollateralType.GOVERNMENT_BOND, new BigDecimal("100000.00"), new BigDecimal("95000.00")};
        List<Object[]> results = Arrays.<Object[]>asList(row);
        when(collateralRepository.getCollateralSummaryByType()).thenReturn(results);

        List<CollateralSummaryDto> result = collateralService.getCollateralSummaryByType();

        assertEquals(1, result.size());
        assertEquals(CollateralType.GOVERNMENT_BOND, result.get(0).getCollateralType());
        assertEquals(new BigDecimal("100000.00"), result.get(0).getTotalMarketValue());
        assertEquals(new BigDecimal("95000.00"), result.get(0).getTotalEligibleValue());
    }

    @Test
    void getCollateralConcentrationByRating_ShouldReturnConcentration() {
        Object[] row = {Rating.AAA, 5L, new BigDecimal("500000.00")};
        List<Object[]> results = Arrays.<Object[]>asList(row);
        when(collateralRepository.getCollateralConcentrationByRating()).thenReturn(results);

        List<CollateralSummaryDto> result = collateralService.getCollateralConcentrationByRating();

        assertEquals(1, result.size());
        assertEquals(Rating.AAA, result.get(0).getRating());
        assertEquals(5L, result.get(0).getCount());
        assertEquals(new BigDecimal("500000.00"), result.get(0).getTotalMarketValue());
    }

    @Test
    void getHighRiskCollaterals_ShouldReturnHighRiskCollaterals() {
        BigDecimal threshold = new BigDecimal("0.15");
        List<CollateralEntity> collateralEntities = Arrays.asList(collateralEntity);
        when(collateralRepository.findHighRiskCollateral(threshold)).thenReturn(collateralEntities);
        when(collateralMapper.toDto(collateralEntity)).thenReturn(collateralDto);

        List<CollateralDto> result = collateralService.getHighRiskCollaterals(threshold);

        assertEquals(1, result.size());
        verify(collateralRepository).findHighRiskCollateral(threshold);
    }

    @Test
    void getHighRiskCollaterals_WithDefaultThreshold_ShouldUseDefaultThreshold() {
        BigDecimal defaultThreshold = new BigDecimal("0.15");
        List<CollateralEntity> collateralEntities = Arrays.asList(collateralEntity);
        when(collateralRepository.findHighRiskCollateral(defaultThreshold)).thenReturn(collateralEntities);
        when(collateralMapper.toDto(collateralEntity)).thenReturn(collateralDto);

        List<CollateralDto> result = collateralService.getHighRiskCollaterals();

        assertEquals(1, result.size());
        verify(collateralRepository).findHighRiskCollateral(defaultThreshold);
    }

    @Test
    void revalueCollateral_ShouldUpdateMarketValueAndHaircut() {
        BigDecimal newMarketValue = new BigDecimal("110000.00");
        BigDecimal newHaircut = new BigDecimal("0.0600");
        when(collateralRepository.findById(1L)).thenReturn(Optional.of(collateralEntity));
        when(collateralRepository.save(collateralEntity)).thenReturn(collateralEntity);
        when(collateralMapper.toDto(collateralEntity)).thenReturn(collateralDto);

        CollateralDto result = collateralService.revalueCollateral(1L, newMarketValue, newHaircut);

        assertNotNull(result);
        verify(collateralRepository).save(collateralEntity);
        assertEquals(newMarketValue, collateralEntity.getMarketValue());
        assertEquals(newHaircut, collateralEntity.getHaircut());
    }

    @Test
    void calculateTotalRiskExposure_ShouldCalculateCorrectRiskExposure() {
        List<CollateralEntity> eligibleCollaterals = Arrays.asList(collateralEntity);
        when(collateralRepository.findByStatus(CollateralStatus.ELIGIBLE)).thenReturn(eligibleCollaterals);

        BigDecimal result = collateralService.calculateTotalRiskExposure();

        BigDecimal expectedRisk = collateralEntity.getMarketValue().subtract(collateralEntity.getEligibleValue());
        assertEquals(expectedRisk, result);
        verify(collateralRepository).findByStatus(CollateralStatus.ELIGIBLE);
    }

    @Test
    void getAverageHaircutByType_ShouldCalculateAverageHaircut() {
        List<CollateralEntity> collaterals = Arrays.asList(collateralEntity);
        when(collateralRepository.findByCollateralType(CollateralType.GOVERNMENT_BOND)).thenReturn(collaterals);

        BigDecimal result = collateralService.getAverageHaircutByType(CollateralType.GOVERNMENT_BOND);

        assertEquals(collateralEntity.getHaircut(), result);
        verify(collateralRepository).findByCollateralType(CollateralType.GOVERNMENT_BOND);
    }

    @Test
    void getAverageHaircutByType_ShouldReturnZero_WhenNoCollaterals() {
        when(collateralRepository.findByCollateralType(CollateralType.GOVERNMENT_BOND)).thenReturn(Arrays.asList());

        BigDecimal result = collateralService.getAverageHaircutByType(CollateralType.GOVERNMENT_BOND);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void processMaturedCollaterals_ShouldMarkMaturedCollateralsAsMatured() {
        LocalDate today = LocalDate.now();
        CollateralEntity maturedCollateral = CollateralEntity.builder()
                .id(2L)
                .status(CollateralStatus.ELIGIBLE)
                .maturityDate(today.minusDays(1))
                .build();

        List<CollateralEntity> maturedCollaterals = Arrays.asList(maturedCollateral);
        when(collateralRepository.findByMaturityDateBetween(any(LocalDate.class), eq(today)))
                .thenReturn(maturedCollaterals);
        when(collateralRepository.save(maturedCollateral)).thenReturn(maturedCollateral);

        collateralService.processMaturedCollaterals();

        verify(collateralRepository).save(maturedCollateral);
        assertEquals(CollateralStatus.MATURED, maturedCollateral.getStatus());
    }
}