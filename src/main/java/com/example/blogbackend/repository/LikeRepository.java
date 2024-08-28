package com.example.blogbackend.repository;

import com.example.blogbackend.entity.BoardEntity;
import com.example.blogbackend.entity.Like;
import com.example.blogbackend.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserEntityAndBoardEntity(UserEntity userEntity, BoardEntity boardEntity);

    @Query("SELECT l.boardEntity FROM Like l WHERE l.userEntity.idx = :userId ORDER BY l.id asc")
    Page<BoardEntity> findLikedBoardsByUserId(@Param("userId") Long userId, Pageable pageable);

    long countByBoardEntity(BoardEntity boardEntity);
}
