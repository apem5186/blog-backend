package com.example.blogbackend.repository;

import com.example.blogbackend.entity.BoardEntity;
import com.example.blogbackend.entity.Like;
import com.example.blogbackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserEntityAndBoardEntity(UserEntity userEntity, BoardEntity boardEntity);

    long countByBoardEntity(BoardEntity boardEntity);
}
