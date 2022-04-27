package com.corinne.corinne_be.dto.user_dto;

import lombok.Getter;

@Getter
public class ProfileResponseDto {
    private String userImageUrl;

    public ProfileResponseDto(String userImageUrl) {

        this.userImageUrl = userImageUrl;
    }
    public ProfileResponseDto(){
    }
}
