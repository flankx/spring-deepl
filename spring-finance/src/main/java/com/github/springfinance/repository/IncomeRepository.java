package com.github.springfinance.repository;

import com.github.springfinance.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    
    List<Income> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Income> findByCategoryId(Long categoryId);
    
    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.date BETWEEN :startDate AND :endDate")
    Double sumByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.category.id = :categoryId AND i.date BETWEEN :startDate AND :endDate")
    Double sumByCategoryIdAndDateBetween(@Param("categoryId") Long categoryId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
}
