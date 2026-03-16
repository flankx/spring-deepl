package com.github.springfinance.controller;

import com.github.springfinance.model.Expense;
import com.github.springfinance.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/finance/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    
    private final ExpenseService expenseService;
    
    @GetMapping
    public List<Expense> findAll() {
        return expenseService.findAll();
    }
    
    @GetMapping("/date-range")
    public List<Expense> findByDateBetween(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return expenseService.findByDateBetween(startDate, endDate);
    }
    
    @GetMapping("/category/{categoryId}")
    public List<Expense> findByCategoryId(@PathVariable Long categoryId) {
        return expenseService.findByCategoryId(categoryId);
    }
    
    @GetMapping("/{id}")
    public Expense findById(@PathVariable Long id) {
        return expenseService.findById(id);
    }
    
    @PostMapping
    public Expense save(@RequestBody Expense expense) {
        return expenseService.save(expense);
    }
    
    @PutMapping("/{id}")
    public Expense update(@PathVariable Long id, @RequestBody Expense expense) {
        expense.setId(id);
        return expenseService.save(expense);
    }
    
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        expenseService.deleteById(id);
    }
    
    @GetMapping("/total")
    public Double getTotalExpense(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return expenseService.getTotalExpense(startDate, endDate);
    }
    
    @GetMapping("/total/category/{categoryId}")
    public Double getTotalExpenseByCategory(@PathVariable Long categoryId, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return expenseService.getTotalExpenseByCategory(categoryId, startDate, endDate);
    }
    
}
