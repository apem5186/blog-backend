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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

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

    public List<CommentDto> getCommentsByBoard(Long boardId) {
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFoundException::new);

        return commentRepository.findByBoardEntity(board).stream()
                .map(comment -> CommentDto.builder()
                        .id(comment.getId())
                        .boardId(boardId)
                        .author(comment.getAuthor())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt().toString())
                        .build())
                .collect(Collectors.toList());
    }
}
