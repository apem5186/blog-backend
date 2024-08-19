package com.example.blogbackend.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeDto {

    private Long boardId;
    private Long userId;
}
