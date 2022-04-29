package com.corinne.corinne_be.dto.account_dto;

import lombok.Getter;

@Getter
public class AccountSimpleDto {
    private Long accountBalance;
    private int coinBalance;

    public AccountSimpleDto() {
    }

    public AccountSimpleDto(Long accountBalance, int coinBalance) {
        this.accountBalance = accountBalance;
        this.coinBalance = coinBalance;
    }
}
