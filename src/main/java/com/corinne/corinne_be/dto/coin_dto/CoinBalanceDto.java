package com.corinne.corinne_be.dto.coin_dto;

import com.corinne.corinne_be.dto.account_dto.CoinsDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CoinBalanceDto {
    private List<CoinsDto> coinsDtoList;
    private Long totalcoinBalance;

}
