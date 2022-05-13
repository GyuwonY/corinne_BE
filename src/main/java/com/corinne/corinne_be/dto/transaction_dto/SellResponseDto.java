package com.corinne.corinne_be.dto.transaction_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SellResponseDto {

    private Long accountBalance;
    private int sellPrice;
    private Long amount;
    private String type;
    private String tradeAt;
    private int leverage;
    private Long leftover;

    public SellResponseDto(Long accountBalance, int sellPrice, Long amount, String type, String tradeAt, int leverage, Long leftover) {
        this.accountBalance = accountBalance;
        this.sellPrice = sellPrice;
        this.amount = amount;
        this.type = type;
        this.tradeAt = tradeAt;
        this.leverage = leverage;
        this.leftover = leftover;
    }
}
