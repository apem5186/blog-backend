package com.example.blogbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UsernameNotEqualException extends RuntimeException {
    public UsernameNotEqualException() {super("계정정보가 맞지 않습니다.");}
}
