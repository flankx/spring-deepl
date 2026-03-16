package com.github.springfinance.controller;

import com.github.springfinance.service.ExpenseService;
import com.github.springfinance.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    @GetMapping("/summary")
    public Map<String, Double> getSummary(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        Map<String, Double> summary = new HashMap<>();

        Double totalIncome = incomeService.getTotalIncome(startDate, endDate);
        Double totalExpense = expenseService.getTotalExpense(startDate, endDate);
        Double balance = (totalIncome != null ? totalIncome : 0) - (totalExpense != null ? totalExpense : 0);

        summary.put("totalIncome", totalIncome != null ? totalIncome : 0);
        summary.put("totalExpense", totalExpense != null ? totalExpense : 0);
        summary.put("balance", balance);

        return summary;
    }

}
