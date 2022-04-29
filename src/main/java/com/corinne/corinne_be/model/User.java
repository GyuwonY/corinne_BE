package com.corinne.corinne_be.model;

import lombok.Getter;
import javax.persistence.*;

@Getter
@Entity
@Table(name = "tbl_user")
public class User {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column
    private Long userId;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false, unique = true)
    private String userEmail;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Long accountBalance;

    @Column(nullable = false)
    private int exp;

    @Column(nullable = false)
    private boolean firstLogin;

    @Column(nullable = false)
    private double lastFluctuation;



    public User() {
    }

    public void update(Long accountBalance) {
        this.accountBalance = accountBalance;
    }
}
