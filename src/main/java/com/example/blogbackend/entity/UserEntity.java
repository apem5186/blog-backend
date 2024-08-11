package com.example.blogbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TB_USER")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String userId;
    private String userName;
    private String userPw;

}
