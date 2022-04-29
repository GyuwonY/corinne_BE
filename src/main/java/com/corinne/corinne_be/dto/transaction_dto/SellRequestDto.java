package com.corinne.corinne_be.dto.transaction_dto;

import lombok.Getter;

@Getter
public class SellRequestDto {

    private String tiker;
    private int tradePrice;
    private int sellAmount;


    public SellRequestDto() {
    }

    public SellRequestDto(String tiker, int tradePrice, int sellAmount) {
        this.tiker = tiker;
        this.tradePrice = tradePrice;
        this.sellAmount = sellAmount;
    }
}
