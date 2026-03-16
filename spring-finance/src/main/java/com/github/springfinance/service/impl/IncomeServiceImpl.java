package com.github.springfinance.service.impl;

import com.github.springfinance.model.Income;
import com.github.springfinance.repository.IncomeRepository;
import com.github.springfinance.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {
    
    private final IncomeRepository incomeRepository;
    
    @Override
    public List<Income> findAll() {
        return incomeRepository.findAll();
    }
    
    @Override
    public List<Income> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        return incomeRepository.findByDateBetween(startDate, endDate);
    }
    
    @Override
    public List<Income> findByCategoryId(Long categoryId) {
        return incomeRepository.findByCategoryId(categoryId);
    }
    
    @Override
    public Income findById(Long id) {
        return incomeRepository.findById(id).orElse(null);
    }
    
    @Override
    public Income save(Income income) {
        return incomeRepository.save(income);
    }
    
    @Override
    public void deleteById(Long id) {
        incomeRepository.deleteById(id);
    }
    
    @Override
    public Double getTotalIncome(LocalDate startDate, LocalDate endDate) {
        return incomeRepository.sumByDateBetween(startDate, endDate);
    }
    
    @Override
    public Double getTotalIncomeByCategory(Long categoryId, LocalDate startDate, LocalDate endDate) {
        return incomeRepository.sumByCategoryIdAndDateBetween(categoryId, startDate, endDate);
    }
    
}
