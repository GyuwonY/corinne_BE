package com.corinne.corinne_be.dto.transaction_dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TransactionResponseDto {

    private String tiker;
    private String type;
    private int price;
    private String tradeAt;

    public TransactionResponseDto() {
    }

    public TransactionResponseDto(String tiker, String type, int price, String tradeAt) {
        this.tiker = tiker;
        this.type = type;
        this.price = price;
        this.tradeAt = tradeAt;
    }
}
