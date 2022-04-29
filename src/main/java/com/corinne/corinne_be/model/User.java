package com.corinne.corinne_be.model;

import com.corinne.corinne_be.dto.user_dto.UserRequestdto;
import com.corinne.corinne_be.security.UserDetailsImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
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

    @Column(nullable = false, unique = true)
    private Long kakaoId;

    @Column(nullable = false)
    private int exp;

    @Column(nullable = false)
    private boolean firstLogin;

    @Column(nullable = false)
    private double lastFluctuation;

    public User(String nickname, String password, String userEmail, Long accountBalance, Long kakaoId) {
       this.nickname = nickname;
       this.password = password;
       this.userEmail = userEmail;
       this.accountBalance = accountBalance;
       this.kakaoId = kakaoId;
    }
    //회원정보 수정
    public void infoUpdate(UserRequestdto userRequestdto){
        this.nickname = userRequestdto.getNickname();
        this.password = userRequestdto.getPassword();
    }


    //프로필 이미지 수정
    public void profileImgUpdate(String imageUrl){
        this.imageUrl = imageUrl;
    }
}


