package com.corinne.corinne_be.dto.rank_dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MyRankResponseDto {

    private int myRank;
    private List<RankInfoDto> rank;

    public MyRankResponseDto() {
    }

    public MyRankResponseDto(List<RankInfoDto> rank, int myRank) {
        this.rank = rank;
        this.myRank = myRank;
    }
}
