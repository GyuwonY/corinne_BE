package com.corinne.corinne_be.dto.coin_dto;

import lombok.Getter;



@Getter
public class CoinUpdateDto {
    private int buyPrice;
    private int amount;

    public CoinUpdateDto() {
    }

    public CoinUpdateDto(int buyPrice, int amount) {
        this.buyPrice = buyPrice;
        this.amount = amount;
    }
}
