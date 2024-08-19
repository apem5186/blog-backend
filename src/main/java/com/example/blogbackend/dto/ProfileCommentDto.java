package com.example.blogbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileCommentDto {

    private Long id;
    @JsonProperty("board_id")
    private Long boardId;
    @JsonProperty("board_title")
    private String boardTitle;
    @JsonProperty("board_category")
    private String boardCategory;
    private String author;
    private String content;
    @JsonProperty("created_at")
    private String createdAt;
}
