package com.corinne.corinne_be.dto.rank_dto;

import com.corinne.corinne_be.model.User;
import lombok.Getter;

@Getter
public class RankInfoDto {

    private Long userId;
    private String nickname;
    private String imageUrl;
    private Long totalBalance;
    private double fluctuationRate;

    public RankInfoDto() {
    }

    public RankInfoDto(Long userId, String nickname, String imageUrl, Long totalBalance, double fluctuationRate) {
        this.userId = userId;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.totalBalance = totalBalance;
        this.fluctuationRate = fluctuationRate;
    }

    public RankInfoDto(User user){
        this.userId = user.getUserId();
        this.nickname = user.getNickname();
        this.imageUrl = user.getImageUrl();
        this.totalBalance = (long)(10000L*user.getLastFluctuation()+1000000L);
        this.fluctuationRate = user.getLastFluctuation();
    }
}
