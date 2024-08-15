package com.example.blogbackend.controller;

import com.example.blogbackend.dto.CommentDto;
import com.example.blogbackend.entity.Comment;
import com.example.blogbackend.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/comment")
@CrossOrigin
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public Comment createComment(@RequestBody CommentDto commentDto) {
        return commentService.createComment(commentDto);
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<CommentDto>> getCommentsByBoard(@PathVariable("boardId") Long boardId) {
        List<CommentDto> comments = commentService.getCommentsByBoard(boardId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteComment(
            @RequestParam(value = "commentId") String commentId,
            @RequestParam(value = "username") String username) {

        commentService.deleteComment(commentId, username);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/update")
    public ResponseEntity<Comment> updateComment(@RequestBody CommentDto commentDto) {
        Comment comment = commentService.updateComment(commentDto);
        return ResponseEntity.ok(comment);
    }
}
