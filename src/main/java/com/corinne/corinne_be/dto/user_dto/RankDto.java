package com.corinne.corinne_be.dto.user_dto;

import lombok.Getter;

@Getter
public class RankDto {

    private Long userId;
    private String nickname;
    private String imageUrl;
    private int totalBalance;
    private double fluctuationRate;

    public RankDto() {
    }

    public RankDto(Long userId, String nickname, String imageUrl, int totalBalance, double fluctuationRate) {
        this.userId = userId;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.totalBalance = totalBalance;
        this.fluctuationRate = fluctuationRate;
    }
}
