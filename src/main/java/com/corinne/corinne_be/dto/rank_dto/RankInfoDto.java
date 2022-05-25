package com.corinne.corinne_be.dto.rank_dto;

import com.corinne.corinne_be.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RankInfoDto {
    private int exp;
    private Long userId;
    private String nickname;
    private String imageUrl;
    private Long totalBalance;
    private double fluctuationRate;
    private int rank;

    public RankInfoDto() {
    }

    public RankInfoDto(User user, Long totalBalance, double fluctuationRate) {
        this.userId = user.getUserId();
        this.nickname = user.getNickname();
        this.imageUrl = user.getImageUrl();
        this.exp = user.getExp();
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
