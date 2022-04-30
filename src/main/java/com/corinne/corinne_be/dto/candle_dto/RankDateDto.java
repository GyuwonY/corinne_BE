package com.corinne.corinne_be.dto.candle_dto;

import lombok.Getter;

import java.util.List;

@Getter
public class RankDateDto {

   private List<DateReponseDto> rank;

    public RankDateDto() {
    }

    public RankDateDto(List<DateReponseDto> rank) {
        this.rank = rank;
    }
}
