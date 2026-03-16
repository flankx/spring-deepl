package com.github.springfinance.repository;

import com.github.springfinance.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByType(String type);
    
}
