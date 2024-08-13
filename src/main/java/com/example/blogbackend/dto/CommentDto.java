package com.example.blogbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

    private Long id;
    private Long boardId;
    private String author;
    private String content;
    private String createdAt;
}
