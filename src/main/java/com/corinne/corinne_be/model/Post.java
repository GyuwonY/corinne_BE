package com.corinne.corinne_be.model;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "tbl_post")
@EntityListeners(AuditingEntityListener.class)
public class Post {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column
    private Long postId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int hits;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String category;

    @CreatedDate
    private LocalDateTime createdAt;
}
