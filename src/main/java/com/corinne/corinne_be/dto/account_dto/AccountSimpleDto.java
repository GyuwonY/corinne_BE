package com.corinne.corinne_be.dto.account_dto;

import com.corinne.corinne_be.model.Coin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountSimpleDto {
    private Long accountBalance;
    private double buyPrice;
    private Long amount;
    private int leverage;
}
