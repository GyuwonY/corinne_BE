package com.corinne.corinne_be.dto.transaction_dto;

import lombok.Getter;

@Getter
public class BuyRequestDto {

    private String tiker;
    private int leverage;
    private int tradePrice;
    private Long buyAmount;


    public BuyRequestDto() {
    }

    public BuyRequestDto(String tiker, int leverage, int tradePrice, Long buyAmount) {
        this.tiker = tiker;
        this.leverage = leverage;
        this.tradePrice = tradePrice;
        this.buyAmount = buyAmount;
    }
}
