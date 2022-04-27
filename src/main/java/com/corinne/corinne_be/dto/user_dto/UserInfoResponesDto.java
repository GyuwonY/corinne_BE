package com.corinne.corinne_be.dto.user_dto;

import com.corinne.corinne_be.security.UserDetailsImpl;
import lombok.Getter;

@Getter
public class UserInfoResponesDto {
    private Long userId;
    private String userEmail;
    private String nickname;
    private String password;
    private String imageUrl;

    public UserInfoResponesDto(UserDetailsImpl userDetails) {

        this.userId = userDetails.getUser().getUserId();
        this.userEmail = userDetails.getUsername();
        this.password = userDetails.getPassword();
        this.nickname = userDetails.getUser().getNickname();
        try {
            this.imageUrl = userDetails.getUser().getImageUrl();
        }catch (NullPointerException e){
            this.imageUrl = "기본이미지";
        }

    }
    public UserInfoResponesDto(UserRequestdto userRequestdto){
        this.nickname = userRequestdto.getNickname();
        this.password = userRequestdto.getPassword();

    }
}
