package com.example.blogbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("board_id")
    private Long boardId;
    @JsonProperty("user_idx")
    private Long userIdx;
    private String author;
    private String content;
    @JsonProperty("created_at")
    private String createdAt;
}
