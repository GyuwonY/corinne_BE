package com.corinne.corinne_be.model;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "tbl_comment")
@EntityListeners(AuditingEntityListener.class)
public class Comment {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column
    private Long commentId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String content;

    @CreatedDate
    private LocalDateTime createdAt;
}