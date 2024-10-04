package com.example.blogbackend.service;

import com.example.blogbackend.dto.CommentDto;
import com.example.blogbackend.dto.ProfileCommentDto;
import com.example.blogbackend.entity.BoardEntity;
import com.example.blogbackend.entity.Comment;
import com.example.blogbackend.entity.UserEntity;
import com.example.blogbackend.exception.BoardNotFoundException;
import com.example.blogbackend.exception.CommentNotFoundException;
import com.example.blogbackend.exception.UsernameNotEqualException;
import com.example.blogbackend.repository.BoardRepository;
import com.example.blogbackend.repository.CommentRepository;
import com.example.blogbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    /**
     * 댓글 생성
     */
    public Comment createComment(CommentDto commentDto) {
        BoardEntity board = boardRepository.findById(commentDto.getBoardId())
                .orElseThrow(BoardNotFoundException::new);
        UserEntity user = userRepository.findById(commentDto.getUserIdx())
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));
        Comment comment = Comment.builder()
                .boardEntity(board)
                .userEntity(user)
                .content(commentDto.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        return commentRepository.save(comment);
    }

    /**
     * 게시글 별로 댓글 가져오기
     */
    public List<CommentDto> getCommentsByBoard(Long boardId) {
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);

        return commentRepository.findByBoardEntityOrderByCreatedAt(board).stream()
                .map(comment -> CommentDto.builder()
                        .id(comment.getId())
                        .boardId(boardId)
                        .author(comment.getAuthor())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 댓글 가져오기 (Pagination 적용)
     */
    @Transactional(readOnly = true)
    public Page<ProfileCommentDto> getComments(Pageable pageable) {
        return commentRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(comment -> ProfileCommentDto.builder()
                        .id(comment.getId())
                        .boardId(comment.getBoardEntity().getIdx())
                        .boardTitle(comment.getBoardEntity().getTitle())
                        .boardCategory(comment.getBoardEntity().getCategory().getName())
                        .author(comment.getAuthor())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")))
                        .build());
    }

    /**
     * Username별로 댓글 가져오기(Pagination 적용)
     */
    @Transactional(readOnly = true)
    public Page<ProfileCommentDto> getCommentsByUsername(Pageable pageable, String username) {
        return commentRepository.findByAuthorOrderByCreatedAtDesc(pageable, username)
                .map(comment -> ProfileCommentDto.builder()
                        .id(comment.getId())
                        .boardId(comment.getBoardEntity().getIdx())
                        .boardTitle(comment.getBoardEntity().getTitle())
                        .boardCategory(comment.getBoardEntity().getCategory().getName())
                        .author(comment.getAuthor())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")))
                        .build());
    }

    /**
     * 댓글 지우기
     */
    public void deleteComment(String commentId, String username) {
        Comment comment = commentRepository.findById(Long.valueOf(commentId)).orElseThrow(CommentNotFoundException::new);
        if (!comment.getAuthor().equals(username)) {
            throw new UsernameNotFoundException("해당 유저를 찾을 수 없습니다.");
        }
        commentRepository.delete(comment);
    }

    /**
     * 댓글 수정
     */
    public Comment updateComment(CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentDto.getId()).orElseThrow(CommentNotFoundException::new);
        if (!comment.getAuthor().equals(commentDto.getAuthor())) {
            throw new UsernameNotFoundException("해당 유저를 찾을 수 없습니다.");
        }
        comment.setContent(commentDto.getContent());
        return commentRepository.save(comment);
    }
}
