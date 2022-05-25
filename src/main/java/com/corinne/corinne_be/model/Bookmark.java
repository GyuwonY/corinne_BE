package com.corinne.corinne_be.model;

import lombok.Getter;
import javax.persistence.*;

@Getter
@Entity
@Table(name = "tbl_bookmark")
public class Bookmark {

    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column
    private Long bookmarkId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String tiker;

    public Bookmark() {
    }

    public Bookmark(Long userId, String tiker) {
        this.userId = userId;
        this.tiker = tiker;
    }
}
