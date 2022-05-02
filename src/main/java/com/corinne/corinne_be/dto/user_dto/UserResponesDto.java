package com.corinne.corinne_be.dto.user_dto;

import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.security.UserDetailsImpl;
import lombok.Getter;


@Getter
public class UserResponesDto {
    private Long userId;
    private String userEmail;
    private String nickname;


    public UserResponesDto(UserDetailsImpl userDetails, UserRequestdto userRequestdto){
        this.userId = userDetails.getUser().getUserId();
        this.userEmail = userDetails.getUsername();
        this.nickname = userRequestdto.getNickname();
    }

}
