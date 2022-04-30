package com.corinne.corinne_be.dto.rank_dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MyRankResponseDto {

    private int myRank;
    private List<RankDto> rank;
    private int totalPage;


    public MyRankResponseDto() {
    }

    public MyRankResponseDto(int myRank, List<RankDto> rank, int totalPage) {
        this.myRank = myRank;
        this.rank = rank;
        this.totalPage = totalPage;
    }
}
