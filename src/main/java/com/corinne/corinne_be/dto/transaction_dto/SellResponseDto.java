package com.corinne.corinne_be.dto.transaction_dto;

import lombok.Getter;

@Getter
public class SellResponseDto {

    private Long accountBalance;
    private int sellPrice;
    private int amount;
    private String type;
    private String tradeAt;

    public SellResponseDto() {
    }

    public SellResponseDto(Long accountBalance, int sellPrice, int amount, String type, String tradeAt) {
        this.accountBalance = accountBalance;
        this.sellPrice = sellPrice;
        this.amount = amount;
        this.type = type;
        this.tradeAt = tradeAt;
    }
}
