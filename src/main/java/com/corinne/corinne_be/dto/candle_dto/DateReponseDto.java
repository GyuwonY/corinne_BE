package com.corinne.corinne_be.dto.candle_dto;

import lombok.Getter;

@Getter
public class DateReponseDto {

    private String tiker;
    private String tikername;
    private int tradePrice;
    private double fluctuationRate;
    private String unit;
    private boolean favorite;


    public DateReponseDto() {
    }

    public DateReponseDto(String tiker, String tikername, int tradePrice, double fluctuationRate, String unit, boolean favorite) {
        this.tiker = tiker;
        this.tikername = tikername;
        this.tradePrice = tradePrice;
        this.fluctuationRate = fluctuationRate;
        this.unit = unit;
        this.favorite = favorite;
    }
}
