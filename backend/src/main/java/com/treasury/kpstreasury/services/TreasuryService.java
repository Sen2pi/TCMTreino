package com.treasury.KPStreasury.services;

import com.treasury.KPStreasury.exceptions.BusinessException;
import com.treasury.KPStreasury.kafka.TreasuryProducer;
import com.treasury.KPStreasury.mappers.TreasuryMapper;
import com.treasury.KPStreasury.models.dto.TreasuryDto;
import com.treasury.KPStreasury.models.entity.TreasuryEntity;
import com.treasury.KPStreasury.repository.TreasuryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TreasuryService {
    
    private final TreasuryRepository treasuryRepository;
    private final TreasuryMapper treasuryMapper;
    private final TreasuryProducer treasuryProducer;
    
    @Transactional(readOnly = true)
    public Page<TreasuryDto> findAll(Pageable pageable) {
        log.debug("Finding all treasury accounts with pagination: {}", pageable);
        return treasuryRepository.findAll(pageable)
                .map(treasuryMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public TreasuryDto findById(Long id) {
        log.debug("Finding treasury account by id: {}", id);
        TreasuryEntity treasury = treasuryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Treasury account not found with id: " + id));
        return treasuryMapper.toDto(treasury);
    }
    
    public TreasuryDto create(TreasuryDto treasuryDto) {
        log.debug("Creating new treasury account: {}", treasuryDto.getAccountNumber());
        
        validateTreasuryData(treasuryDto);
        checkAccountNumberUniqueness(treasuryDto.getAccountNumber());
        
        TreasuryEntity treasury = treasuryMapper.toEntity(treasuryDto);
        TreasuryEntity saved = treasuryRepository.save(treasury);
        
        treasuryProducer.sendTreasuryCreatedEvent(saved.getId(), saved.getAccountNumber());
        log.info("Treasury account created successfully: {}", saved.getAccountNumber());
        
        return treasuryMapper.toDto(saved);
    }
    
    public TreasuryDto update(Long id, TreasuryDto treasuryDto) {
        log.debug("Updating treasury account with id: {}", id);
        
        TreasuryEntity existing = treasuryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Treasury account not found with id: " + id));
        
        validateTreasuryData(treasuryDto);
        
        if (!existing.getAccountNumber().equals(treasuryDto.getAccountNumber())) {
            checkAccountNumberUniqueness(treasuryDto.getAccountNumber());
        }
        
        treasuryMapper.updateEntityFromDto(treasuryDto, existing);
        TreasuryEntity updated = treasuryRepository.save(existing);
        
        treasuryProducer.sendTreasuryUpdatedEvent(updated.getId(), updated.getAccountNumber());
        log.info("Treasury account updated successfully: {}", updated.getAccountNumber());
        
        return treasuryMapper.toDto(updated);
    }
    
    public void delete(Long id) {
        log.debug("Deleting treasury account with id: {}", id);
        
        TreasuryEntity treasury = treasuryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Treasury account not found with id: " + id));
        
        treasuryRepository.delete(treasury);
        
        treasuryProducer.sendTreasuryDeletedEvent(id, treasury.getAccountNumber());
        log.info("Treasury account deleted successfully: {}", treasury.getAccountNumber());
    }
    
    @Transactional(readOnly = true)
    public List<TreasuryDto> findByStatus(String status) {
        log.debug("Finding treasury accounts by status: {}", status);
        return treasuryRepository.findByStatus(status)
                .stream()
                .map(treasuryMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TreasuryDto> findByCurrency(String currency) {
        log.debug("Finding treasury accounts by currency: {}", currency);
        return treasuryRepository.findByCurrency(currency)
                .stream()
                .map(treasuryMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getTotalBalanceByCurrency(String currency) {
        log.debug("Calculating total balance for currency: {}", currency);
        return treasuryRepository.sumBalanceByCurrency(currency);
    }
    
    @Transactional(readOnly = true)
    public List<Object> getTreasurySummary() {
        log.debug("Getting treasury summary");
        return treasuryRepository.getTreasurySummaryByCurrency();
    }
    
    private void validateTreasuryData(TreasuryDto treasuryDto) {
        if (treasuryDto.getAccountNumber() == null || treasuryDto.getAccountNumber().trim().isEmpty()) {
            throw new BusinessException("Account number is required");
        }
        
        if (treasuryDto.getBankName() == null || treasuryDto.getBankName().trim().isEmpty()) {
            throw new BusinessException("Bank name is required");
        }
        
        if (treasuryDto.getCurrency() == null || treasuryDto.getCurrency().trim().isEmpty()) {
            throw new BusinessException("Currency is required");
        }
        
        if (treasuryDto.getBalance() != null && treasuryDto.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Balance cannot be negative");
        }
        
        if (treasuryDto.getAvailableBalance() != null && treasuryDto.getAvailableBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Available balance cannot be negative");
        }
        
        if (treasuryDto.getBalance() != null && treasuryDto.getAvailableBalance() != null &&
            treasuryDto.getAvailableBalance().compareTo(treasuryDto.getBalance()) > 0) {
            throw new BusinessException("Available balance cannot exceed total balance");
        }
    }
    
    private void checkAccountNumberUniqueness(String accountNumber) {
        if (treasuryRepository.existsByAccountNumber(accountNumber)) {
            throw new BusinessException("Account number already exists: " + accountNumber);
        }
    }
}
