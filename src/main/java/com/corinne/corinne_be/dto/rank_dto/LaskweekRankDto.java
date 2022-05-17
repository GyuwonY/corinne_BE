package com.corinne.corinne_be.dto.rank_dto;

import lombok.Getter;

import java.util.List;

@Getter
public class LaskweekRankDto {

    private List<RankDto> rank;
    private int totalPage;

    public LaskweekRankDto() {
    }

    public LaskweekRankDto(List<RankDto> rank, int totalPage) {
        this.rank = rank;
        this.totalPage = totalPage;
    }
}
