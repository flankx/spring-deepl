package com.github.springfinance.service.impl;

import com.github.springfinance.model.Expense;
import com.github.springfinance.repository.ExpenseRepository;
import com.github.springfinance.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Override
    public List<Expense> findAll() {
        return expenseRepository.findAll();
    }

    @Override
    public List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByDateBetween(startDate, endDate);
    }

    @Override
    public List<Expense> findByCategoryId(Long categoryId) {
        return expenseRepository.findByCategoryId(categoryId);
    }

    @Override
    public Expense findById(Long id) {
        return expenseRepository.findById(id).orElse(null);
    }

    @Override
    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    public void deleteById(Long id) {
        expenseRepository.deleteById(id);
    }

    @Override
    public Double getTotalExpense(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.sumByDateBetween(startDate, endDate);
    }

    @Override
    public Double getTotalExpenseByCategory(Long categoryId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.sumByCategoryIdAndDateBetween(categoryId, startDate, endDate);
    }

}
