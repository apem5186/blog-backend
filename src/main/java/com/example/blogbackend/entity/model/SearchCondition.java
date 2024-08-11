package com.example.blogbackend.entity.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchCondition {
    private String sk;  // search key
    private String sv;  // search value
}
