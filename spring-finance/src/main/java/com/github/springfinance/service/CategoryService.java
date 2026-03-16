package com.github.springfinance.service;

import com.github.springfinance.model.Category;
import java.util.List;

public interface CategoryService {

    List<Category> findAll();

    List<Category> findByType(String type);

    Category findById(Long id);

    Category save(Category category);

    void deleteById(Long id);

}
