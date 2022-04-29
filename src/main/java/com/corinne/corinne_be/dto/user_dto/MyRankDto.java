package com.corinne.corinne_be.dto.user_dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MyRankDto {

    private int myRank;
    private double fluctuationRate;

    public MyRankDto() {
    }

    public MyRankDto(int myRank, double fluctuationRate) {
        this.myRank = myRank;
        this.fluctuationRate = fluctuationRate;
    }
}
