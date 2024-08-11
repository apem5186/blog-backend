package com.example.blogbackend.repository;

import com.example.blogbackend.entity.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findCategoryByName(String name);

    @Transactional
    void deleteCategoryByName(String name);
    Optional<Category> findByName(String name);

}
