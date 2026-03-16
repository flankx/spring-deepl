package com.github.springfinance.service;

import com.github.springfinance.model.Income;
import java.time.LocalDate;
import java.util.List;

public interface IncomeService {

    List<Income> findAll();

    List<Income> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Income> findByCategoryId(Long categoryId);

    Income findById(Long id);

    Income save(Income income);

    void deleteById(Long id);

    Double getTotalIncome(LocalDate startDate, LocalDate endDate);

    Double getTotalIncomeByCategory(Long categoryId, LocalDate startDate, LocalDate endDate);

}
