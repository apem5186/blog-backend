package com.example.blogbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AlreadyLikedException extends RuntimeException {
    public AlreadyLikedException(){super("이미 좋아요가 눌러져있습니다.");}
}
