package com.corinne.corinne_be.dto.account_dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CoinsDto {

    private String coin;
    private double buyPrice;
    private int tradePrice;
    private int leverage;
    private double fluctuationRate;
    private double importanceRate;
    private Long amount;
    private Long fluctuation;
    private Long coinBalance;

    public CoinsDto() {
    }

    public CoinsDto(String coin, double buyPrice,int tradePrice,int leverage, double fluctuationRate, Long amount, Long fluctuation, Long coinBalance) {
        this.coin = coin;
        this.buyPrice = buyPrice;
        this.leverage = leverage;
        this.tradePrice = tradePrice;
        this.fluctuationRate = fluctuationRate;
        this.amount = amount;
        this.fluctuation = fluctuation;
        this.coinBalance = coinBalance;
    }
}
