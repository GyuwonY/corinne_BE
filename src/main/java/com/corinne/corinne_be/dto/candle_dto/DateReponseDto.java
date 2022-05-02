package com.corinne.corinne_be.dto.candle_dto;

import lombok.Getter;

@Getter
public class DateReponseDto {

    private String tiker;
    private Long tradePrice;
    private double fluctuationRate;


    public DateReponseDto() {
    }

    public DateReponseDto(String tiker, Long tradePrice, double fluctuationRate) {
        this.tiker = tiker;
        this.tradePrice = tradePrice;
        this.fluctuationRate = fluctuationRate;
    }
}
