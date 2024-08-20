package com.example.blogbackend.entity;

import com.example.blogbackend.entity.model.Category;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "TB_BOARD")
@Builder
@Entity
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String title;
    private String contents;
    private String author;
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long viewCount;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "boardEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @PrePersist
    protected void onCreate() {
        this.viewCount = 0L;
        this.createdAt = LocalDateTime.now();
    }
}