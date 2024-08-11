package com.example.blogbackend.repository;

import com.example.blogbackend.entity.BoardEntity;
import com.example.blogbackend.entity.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    Page<BoardEntity> findAllByOrderByIdxDesc(Pageable pageable);

    Page<BoardEntity> findByCategoryOrderByCreatedAtDesc(Pageable pageable, Category category);

    @Query("SELECT b.category.name, COUNT(b) FROM BoardEntity b GROUP BY b.category.name")
    List<Object[]> countBoardsByCategory();

    @Query("SELECT COUNT(b) FROM BoardEntity b")
    Long countTotalBoards();
}