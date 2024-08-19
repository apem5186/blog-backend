package com.example.blogbackend.service;

import com.example.blogbackend.dto.LikeDto;
import com.example.blogbackend.entity.BoardEntity;
import com.example.blogbackend.entity.Like;
import com.example.blogbackend.entity.UserEntity;
import com.example.blogbackend.exception.AlreadyLikedException;
import com.example.blogbackend.exception.BoardNotFoundException;
import com.example.blogbackend.exception.UsernameNotEqualException;
import com.example.blogbackend.repository.BoardRepository;
import com.example.blogbackend.repository.LikeRepository;
import com.example.blogbackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    /**
     * 좋아요 기능
     */
    @Transactional
    public void insert(LikeDto likeDto) {
        UserEntity userEntity = userRepository.findById(likeDto.getUserId()).orElseThrow(UsernameNotEqualException::new);
        BoardEntity boardEntity = boardRepository.findById(likeDto.getBoardId()).orElseThrow(BoardNotFoundException::new);

        if (likeRepository.findByUserEntityAndBoardEntity(userEntity, boardEntity).isPresent()) {
            throw new AlreadyLikedException();
        }

        Like like = Like.builder()
                .userEntity(userEntity)
                .boardEntity(boardEntity)
                .build();

        likeRepository.save(like);
    }

    /**
     * 좋아요 취소
     */
    @Transactional
    public void delete(LikeDto likeDto) {
        UserEntity userEntity = userRepository.findById(likeDto.getUserId()).orElseThrow(UsernameNotEqualException::new);
        BoardEntity boardEntity = boardRepository.findById(likeDto.getBoardId()).orElseThrow(BoardNotFoundException::new);
        Like like = likeRepository.findByUserEntityAndBoardEntity(userEntity, boardEntity).orElseThrow();

        likeRepository.delete(like);
    }

    /**
     * 좋아요가 눌렸는지 상태확인
     */
    public boolean getStateCheck(LikeDto likeDto) {
        UserEntity userEntity = userRepository.findById(likeDto.getUserId()).orElseThrow(UsernameNotEqualException::new);
        BoardEntity boardEntity = boardRepository.findById(likeDto.getBoardId()).orElseThrow(BoardNotFoundException::new);
        return likeRepository.findByUserEntityAndBoardEntity(userEntity, boardEntity).isPresent();
    }

    /**
     * 좋아요 갯수 반환
     */
    public long getLikeCount(Long boardId) {
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);
        return likeRepository.countByBoardEntity(board);
    }
}
