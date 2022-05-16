package com.corinne.corinne_be.dto.user_dto;

import com.corinne.corinne_be.dto.rank_dto.MyRankDto;
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
    private int myRank;
    private int highRank;
    private Long follower;
    private Long following;
    private double fluctuationRate;
    private Long totalBalance;
    private Long resetCount;



    public UserInfoResponesDto(User user, MyRankDto myRankDto, Long resetCount, Long follower, Long following) {

        this.userId = user.getUserId();
        this.userEmail = user.getUserEmail();
        this.nickname = user.getNickname();
        this.imageUrl = user.getImageUrl();
        this.exp = user.getExp();
        this.accountBalance = user.getAccountBalance();
        this.firstLogin = user.isFirstLogin();
        this.highRank = user.getHighRank();
        this.fluctuationRate = myRankDto.getFluctuationRate();
        this.myRank = myRankDto.getMyRank();
        this.totalBalance = myRankDto.getTotalBalance();
        this.resetCount = resetCount;
        this.follower = follower;
        this.following = following;
    }
}
//    public UserInfoResponesDto(User followUser) {
//        this.userId = followUser.getUserId();
//        this.userEmail = followUser.getUserEmail();;
//        this.nickname = followUser.getNickname();
//    }
