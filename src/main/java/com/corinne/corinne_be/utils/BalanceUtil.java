package com.corinne.corinne_be.utils;

import com.corinne.corinne_be.dto.account_dto.CoinsDto;
import com.corinne.corinne_be.dto.coin_dto.CoinBalanceDto;
import com.corinne.corinne_be.model.Coin;
import com.corinne.corinne_be.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BalanceUtil {
    private final RedisRepository redisRepository;

    public CoinBalanceDto totalCoinBalance(List<Coin> coins){
        Long totalcoinBalance = 0L;

        List<CoinsDto> coinsDtos = new ArrayList<>();

        for(Coin coin : coins){

            // 살 당시 코인 현재가
            BigDecimal buyPrice = BigDecimal.valueOf(coin.getBuyPrice());
            // 현재가
            BigDecimal currentPrice = BigDecimal.valueOf(redisRepository.getTradePrice(coin.getTiker()).getTradePrice());
            // 래버리지
            BigDecimal leverage = BigDecimal.valueOf(coin.getLeverage());
            // 구매 총금액
            BigDecimal amount = BigDecimal.valueOf(coin.getAmount());
            // 래버리지 적용한 수익률
            BigDecimal fluctuationRate = currentPrice.subtract(buyPrice).multiply(leverage).divide(buyPrice,8, RoundingMode.HALF_EVEN);
            // 현재 해당 코인의 가치
            Long coinBalance = fluctuationRate.add(BigDecimal.valueOf(1)).multiply(amount).setScale(0,RoundingMode.CEILING).longValue();

            totalcoinBalance += coinBalance;
            CoinsDto coinsDto = new CoinsDto(coin.getTiker(), buyPrice.doubleValue(), currentPrice.intValue(), leverage.intValue(), fluctuationRate.doubleValue() * 100, coinBalance, coinBalance - coin.getAmount(), coinBalance);

            coinsDtos.add(coinsDto);
        }

        return new CoinBalanceDto(coinsDtos, totalcoinBalance);
    }
}
