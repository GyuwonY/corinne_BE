package com.corinne.corinne_be.dto.rank_dto;

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
}
