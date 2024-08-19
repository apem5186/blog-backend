package com.example.blogbackend.repository;

import com.example.blogbackend.entity.BoardEntity;
import com.example.blogbackend.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardEntityOrderByCreatedAt(BoardEntity boardEntity);

    // Pageable을 이용한 메서드 추가
    Page<Comment> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
