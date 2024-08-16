package com.example.blogbackend.repository;

import com.example.blogbackend.entity.BoardEntity;
import com.example.blogbackend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardEntityOrderByCreatedAt(BoardEntity boardEntity);
}
