package com.github.springfinance.service;

import com.github.springfinance.model.Expense;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
    
    List<Expense> findAll();
    
    List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Expense> findByCategoryId(Long categoryId);
    
    Expense findById(Long id);
    
    Expense save(Expense expense);
    
    void deleteById(Long id);
    
    Double getTotalExpense(LocalDate startDate, LocalDate endDate);
    
    Double getTotalExpenseByCategory(Long categoryId, LocalDate startDate, LocalDate endDate);
    
}
