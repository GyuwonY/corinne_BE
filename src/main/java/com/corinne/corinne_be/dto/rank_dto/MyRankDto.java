package com.corinne.corinne_be.dto.rank_dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MyRankDto {

    private int myRank;
    private double fluctuationRate;
    private Long totalBalance;

    public MyRankDto() {
    }

    public MyRankDto(RankInfoDto rankInfoDto){
        this.myRank = rankInfoDto.getRank();
        this.fluctuationRate = rankInfoDto.getFluctuationRate();
        this.totalBalance = rankInfoDto.getTotalBalance();
    }
}
