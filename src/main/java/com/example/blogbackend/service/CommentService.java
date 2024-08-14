package com.example.blogbackend.service;

import com.example.blogbackend.dto.CommentDto;
import com.example.blogbackend.entity.BoardEntity;
import com.example.blogbackend.entity.Comment;
import com.example.blogbackend.exception.BoardNotFoundException;
import com.example.blogbackend.repository.BoardRepository;
import com.example.blogbackend.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    /**
     * 댓글 생성
     */
    public Comment createComment(CommentDto commentDto) {
        BoardEntity board = boardRepository.findById(commentDto.getBoardId())
                .orElseThrow(BoardNotFoundException::new);

        Comment comment = Comment.builder()
                .boardEntity(board)
                .author(commentDto.getAuthor())
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

        return commentRepository.findByBoardEntity(board).stream()
                .map(comment -> CommentDto.builder()
                        .id(comment.getId())
                        .boardId(boardId)
                        .author(comment.getAuthor())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")))
                        .build())
                .collect(Collectors.toList());
    }
}
