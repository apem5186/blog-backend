package com.example.blogbackend.exception;

public class ExistedAccountException extends RuntimeException{
    public ExistedAccountException() {
        super("이 이메일은 이미 가입되어 있습니다.");
    }
}