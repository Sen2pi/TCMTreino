package com.treasury.kpstreasury.repositories;

import com.treasury.kpstreasury.enums.AccountStatus;
import com.treasury.kpstreasury.enums.CollateralStatus;
import com.treasury.kpstreasury.enums.CollateralType;
import com.treasury.kpstreasury.enums.Rating;
import com.treasury.kpstreasury.models.entity.CollateralEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.AttributeSet;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CollateralRepository extends JpaRepository<CollateralEntity, Long> {

        List<CollateralEntity> findByStatus(CollateralStatus status);
        List<CollateralEntity> findByCollateralType(CollateralType collateralType);
        List<CollateralEntity> findByRating(Rating rating);
        List<CollateralEntity> findByCurrency(String currency);
        List<CollateralEntity> findByCounterparty(String counterparty);

        //Collateral used by Waranty
        List<CollateralEntity> findByStatusAndRatingIn(CollateralStatus status, List<Rating> acceptableRatings);

        //Collatera next to expired
        @Query("SELECT c FROM CollateralEntity c WHERE c.maturityDate BETWEEN :startDate AND :endDate")
        List<CollateralEntity> findByMaturityDateBetween(@Param("startDate")LocalDate startDate, @Param("endDate")LocalDate endDate);

        // Total value of eligible Collateral
        @Query("SELECT SUM(c.eligibleValue) FROM CollateralEntity c WHERE c.status = 'ELIGIBLE'")
        BigDecimal getTotalEligibleValue();


        //  Total Value by Collateral Type
        @Query("SELECT c.collateralType, SUM(c.marketValue), SUM(c.eligibleValue) FROM CollateralEntity c " +
                "WHERE c.status = 'ELIGIBLE' GROUP BY c.collateralType")
        List<Object[]> getCollateralSummaryByType();


        //  Repport bu rating
        @Query("SELECT c.rating, COUNT(c), SUM(c.marketValue) FROM CollateralEntity c " +
                "WHERE c.status = 'ELIGIBLE' GROUP BY c.rating ORDER BY c.rating")
        List<Object[]> getCollateralConcentrationByRating();

        //  Collateral with hier Haircut.
        @Query("SELECT c FROM CollateralEntity c WHERE c.haircut > :haircutThreshold AND c.status = 'ELIGIBLE'")
        List<CollateralEntity> findHighRiskCollateral(@Param("haircutThreshold") BigDecimal haircutThreshold);


        //  search with multiple filtrs

        @Query("SELECT c FROM CollateralEntity c WHERE " +
                "(:type IS NULL OR c.collateralType = :type) AND " +
                "(:minRating IS NULL OR c.rating >= :minRating) AND " +
                "(:currency IS NULL OR c.currency = :currency) AND " +
                "(:status IS NULL OR c.status = :status) AND " +
                "(:minValue IS NULL OR c.marketValue >= :minValue)")
        Page<CollateralEntity> findWithAdvancedFilters(@Param("type") CollateralType type,
                                                       @Param("minRating") Rating minRating,
                                                       @Param("currency") String currency,
                                                       @Param("status") CollateralStatus status,
                                                       @Param("minValue") BigDecimal minValue,
                                                       Pageable pageable);


}
