package com.github.springfinance.controller;

import com.github.springfinance.model.Income;
import com.github.springfinance.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/finance/incomes")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @GetMapping
    public List<Income> findAll() {
        return incomeService.findAll();
    }

    @GetMapping("/date-range")
    public List<Income> findByDateBetween(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return incomeService.findByDateBetween(startDate, endDate);
    }

    @GetMapping("/category/{categoryId}")
    public List<Income> findByCategoryId(@PathVariable Long categoryId) {
        return incomeService.findByCategoryId(categoryId);
    }

    @GetMapping("/{id}")
    public Income findById(@PathVariable Long id) {
        return incomeService.findById(id);
    }

    @PostMapping
    public Income save(@RequestBody Income income) {
        return incomeService.save(income);
    }

    @PutMapping("/{id}")
    public Income update(@PathVariable Long id, @RequestBody Income income) {
        income.setId(id);
        return incomeService.save(income);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        incomeService.deleteById(id);
    }

    @GetMapping("/total")
    public Double getTotalIncome(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return incomeService.getTotalIncome(startDate, endDate);
    }

    @GetMapping("/total/category/{categoryId}")
    public Double getTotalIncomeByCategory(@PathVariable Long categoryId, @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return incomeService.getTotalIncomeByCategory(categoryId, startDate, endDate);
    }

}
