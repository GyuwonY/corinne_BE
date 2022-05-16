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

    public MyRankDto(int myRank, double fluctuationRate, Long totalBalance) {
        this.myRank = myRank;
        this.fluctuationRate = fluctuationRate;
        this.totalBalance = totalBalance;
    }
}
