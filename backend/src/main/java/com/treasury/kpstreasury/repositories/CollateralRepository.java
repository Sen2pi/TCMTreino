package com.treasury.kpstreasury.repositories;

import com.treasury.kpstreasury.models.entity.CollateralEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollateralRepository extends JpaRepository<CollateralEntity, Long> {


}
