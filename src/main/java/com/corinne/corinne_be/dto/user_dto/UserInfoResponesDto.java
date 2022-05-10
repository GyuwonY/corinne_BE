package com.corinne.corinne_be.dto.user_dto;

import com.corinne.corinne_be.model.User;
import lombok.Getter;

@Getter
public class UserInfoResponesDto {
    private Long userId;
    private String userEmail;
    private String nickname;
    private String imageUrl;
    private Long accountBalance;
    private int exp;
    private boolean firstLogin;




    public UserInfoResponesDto(User user) {

        this.userId = user.getUserId();
        this.userEmail = user.getUserEmail();
        this.nickname = user.getNickname();
        try {
            this.imageUrl = user.getImageUrl();
        }catch (NullPointerException e){
            this.imageUrl = "기본이미지";
        }
        this.exp = user.getExp();
        this.accountBalance = user.getAccountBalance();
        this.firstLogin = user.isFirstLogin();
    }
}
//    public UserInfoResponesDto(User followUser) {
//        this.userId = followUser.getUserId();
//        this.userEmail = followUser.getUserEmail();;
//        this.nickname = followUser.getNickname();
//    }
