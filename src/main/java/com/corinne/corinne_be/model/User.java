package com.corinne.corinne_be.model;

import com.corinne.corinne_be.dto.user_dto.UserRequestdto;
import com.corinne.corinne_be.security.UserDetailsImpl;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

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
    private Long accountBalance = 1000000L;

    @Column(nullable = false)
    private int exp = 0;

    @Column(nullable = false)
    private boolean firstLogin = true;

    @Column(nullable = false)
    private double lastFluctuation;

    @Version
    private Integer version;

    public User(String nickname, String password, String userEmail) {
        this.nickname = nickname;
        this.password = password;
        this.userEmail = userEmail;
    }
    //회원정보 수정
    public void infoUpdate(UserRequestdto userRequestdto){
        this.nickname = userRequestdto.getNickname();
        this.firstLogin = false;
    }


    //프로필 이미지 수정
    public void profileImgUpdate(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public void update(Long accountBalance) {
        this.accountBalance = accountBalance;
    }
}


