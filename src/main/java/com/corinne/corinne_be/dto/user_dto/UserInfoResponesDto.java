package com.corinne.corinne_be.dto.user_dto;

import com.corinne.corinne_be.security.UserDetailsImpl;
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




    public UserInfoResponesDto(UserDetailsImpl userDetails) {

        this.userId = userDetails.getUser().getUserId();
        this.userEmail = userDetails.getUsername();
        this.nickname = userDetails.getUser().getNickname();
        try {
            this.imageUrl = userDetails.getUser().getImageUrl();
        }catch (NullPointerException e){
            this.imageUrl = "기본이미지";
        }
        this.exp = userDetails.getUser().getExp();
        this.accountBalance = userDetails.getUser().getAccountBalance();
        this.firstLogin = userDetails.getUser().isFirstLogin();
    }
}
