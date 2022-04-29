package com.corinne.corinne_be.dto.user_dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MyRankResponseDto {

    private int myRank;
    private List<RankDto> rank;


    public MyRankResponseDto() {
    }

    public MyRankResponseDto(int myRank, List<RankDto> rank) {
        this.myRank = myRank;
        this.rank = rank;
    }
}
