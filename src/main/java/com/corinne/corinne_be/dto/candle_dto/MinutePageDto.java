package com.corinne.corinne_be.dto.candle_dto;

import lombok.Getter;

@Getter
public class MinutePageDto {
    private String tiker;
    private int startPrice;
    private int endPrice;
    private int highPrice;
    private int lowPrice;
    private String tradeDate;
    private String tradeTime;


    public MinutePageDto() {
    }

    public MinutePageDto(String tiker, int startPrice, int endPrice, int highPrice, int lowPrice, String tradeDate, String tradeTime) {
        this.tiker = tiker;
        this.startPrice = startPrice;
        this.endPrice = endPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.tradeDate = tradeDate;
        this.tradeTime = tradeTime;
    }
}

