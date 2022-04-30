package com.corinne.corinne_be.dto.transaction_dto;

import lombok.Getter;

@Getter
public class BuyRequestDto {

    private String tiker;
    private int leverage;
    private int tradePrice;
    private int buyAmount;


    public BuyRequestDto() {
    }

    public BuyRequestDto(String tiker, int leverage, int tradePrice, int buyAmount) {
        this.tiker = tiker;
        this.leverage = leverage;
        this.tradePrice = tradePrice;
        this.buyAmount = buyAmount;
    }
}
