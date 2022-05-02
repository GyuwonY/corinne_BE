package com.corinne.corinne_be.dto.follow_dto;

import com.corinne.corinne_be.dto.user_dto.UserInfoResponesDto;

public class FollowDto {
    private String nickname;
    private String profile;
    private  boolean followstate;
    private  boolean isFollowstate;

    public FollowDto(UserInfoResponesDto followUserDto, boolean followstate, boolean isFollowstatef){
        this.nickname = followUserDto.getNickname();
        this.profile = followUserDto.getImageUrl();
        this.followstate = followstate;
        this.isFollowstate = isFollowstatef;
    }
}
