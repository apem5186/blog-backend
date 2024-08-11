package com.example.blogbackend.exception;

public class NotFoundAccountException extends RuntimeException{
    public NotFoundAccountException() {
        super("잘못된 계정입니다.");
    }
}