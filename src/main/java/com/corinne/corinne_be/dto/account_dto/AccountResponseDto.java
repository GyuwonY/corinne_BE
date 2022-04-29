package com.corinne.corinne_be.dto.account_dto;

import lombok.Getter;

import java.util.List;

@Getter
public class AccountResponseDto {

    private double lastFluctuation;
    private Long accountBalance;
    private int totalBalance;
    private double fluctuationRate;
    private List<CoinsDto> coins;

    public AccountResponseDto() {
    }

    public AccountResponseDto(double lastFluctuation, Long accountBalance, int totalBalance, double fluctuationRate, List<CoinsDto> coins) {
        this.lastFluctuation = lastFluctuation;
        this.accountBalance = accountBalance;
        this.totalBalance = totalBalance;
        this.fluctuationRate = fluctuationRate;
        this.coins = coins;
    }

}
