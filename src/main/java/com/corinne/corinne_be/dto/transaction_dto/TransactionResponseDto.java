package com.corinne.corinne_be.dto.transaction_dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TransactionResponseDto {

    private String tiker;
    private String type;
    private int price;
    private Long amount;
    private String tradeAt;

    public TransactionResponseDto() {
    }

    public TransactionResponseDto(String tiker, String type, int price, Long amount, String tradeAt) {
        this.tiker = tiker;
        this.type = type;
        this.price = price;
        this.amount = amount;
        this.tradeAt = tradeAt;
    }
}
