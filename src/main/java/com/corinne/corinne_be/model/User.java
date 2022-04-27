package com.corinne.corinne_be.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "tbl_user")
public class User {

    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    @Column
    private Long userId;

    @Column
    private String imageUrl;

    @Column(nullable = false, unique = true)
    private String userEmail;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Long accountBalance;

    @Column(unique = true)
    private Long kakaoId;




    public User(String nickname, String password, String userEmail, Long accountBalance, Long kakaoId) {
       this.nickname = nickname;
       this.password = password;
       this.userEmail = userEmail;
       this.accountBalance = accountBalance;
       this.kakaoId = kakaoId;
    }
    public void update(String imageUrl){
        this.imageUrl = imageUrl;
    }
}


