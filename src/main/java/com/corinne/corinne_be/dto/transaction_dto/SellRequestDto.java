package com.corinne.corinne_be.dto.transaction_dto;

import lombok.Getter;

@Getter
public class SellRequestDto {

    private String tiker;
    private int tradePrice;
    private Long sellAmount;
    private int leverage;


    public SellRequestDto() {
    }

    public SellRequestDto(String tiker, int tradePrice, Long sellAmount) {
        this.tiker = tiker;
        this.tradePrice = tradePrice;
        this.sellAmount = sellAmount;
    }
}
