package com.corinne.corinne_be.dto.transaction_dto;

import lombok.Getter;

@Getter
public class BuyResponseDto {

    private Long accountBalance;
    private double buyPrice;
    private int amount;
    private String type;
    private String tradeAt;

    public BuyResponseDto() {
    }

    public BuyResponseDto(Long accountBalance, double buyPrice, int amount, String type, String tradeAt) {
        this.accountBalance = accountBalance;
        this.buyPrice = buyPrice;
        this.amount = amount;
        this.type = type;
        this.tradeAt = tradeAt;
    }
}
