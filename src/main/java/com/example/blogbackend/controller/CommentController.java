package com.example.blogbackend.controller;

import com.example.blogbackend.dto.CommentDto;
import com.example.blogbackend.dto.ProfileCommentDto;
import com.example.blogbackend.entity.Comment;
import com.example.blogbackend.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/comments")
    public ResponseEntity<Page<ProfileCommentDto>> getComments(Pageable pageable) {
        Page<ProfileCommentDto> comments = commentService.getComments(pageable);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/comments/{author}")
    public ResponseEntity<Page<ProfileCommentDto>> getCommentsByUsername(@PathVariable("author") String author,
                                                                         Pageable pageable) {
        Page<ProfileCommentDto> comments = commentService.getCommentsByUsername(pageable, author);
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
