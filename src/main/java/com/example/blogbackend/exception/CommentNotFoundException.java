package com.example.blogbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CommentNotFoundException extends RuntimeException{
    public CommentNotFoundException() { super("해당 댓글을 찾을 수 없습니다.");}
}
