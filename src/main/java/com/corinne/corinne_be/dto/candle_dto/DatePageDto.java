package com.corinne.corinne_be.dto.candle_dto;

import lombok.Getter;

@Getter
public class DatePageDto {
    private String tiker;
    private int startPrice;
    private int endPrice;
    private int highPrice;
    private int lowPrice;
    private String tradeDate;

    public DatePageDto() {
    }

    public DatePageDto(String tiker, int startPrice, int endPrice, int highPrice, int lowPrice, String tradeDate) {
        this.tiker = tiker;
        this.startPrice = startPrice;
        this.endPrice = endPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.tradeDate = tradeDate;
    }
}
