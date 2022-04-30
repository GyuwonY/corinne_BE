package com.corinne.corinne_be.dto.rank_dto;

import lombok.Getter;

import java.util.List;

@Getter
public class RankResponseDto {

    private List<RankDto> rank;


    public RankResponseDto() {
    }

    public RankResponseDto(List<RankDto> rank) {
        this.rank = rank;
    }
}
