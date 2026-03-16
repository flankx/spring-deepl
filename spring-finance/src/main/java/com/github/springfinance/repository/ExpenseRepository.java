package com.github.springfinance.repository;

import com.github.springfinance.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Expense> findByCategoryId(Long categoryId);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.date BETWEEN :startDate AND :endDate")
    Double sumByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.category.id = :categoryId AND e.date BETWEEN :startDate AND :endDate")
    Double sumByCategoryIdAndDateBetween(@Param("categoryId") Long categoryId, @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

}
