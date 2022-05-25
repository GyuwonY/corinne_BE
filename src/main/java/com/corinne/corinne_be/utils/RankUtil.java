package com.corinne.corinne_be.utils;

import com.corinne.corinne_be.dto.account_dto.CoinsDto;
import com.corinne.corinne_be.dto.coin_dto.CoinBalanceDto;
import com.corinne.corinne_be.dto.rank_dto.MyRankDto;
import com.corinne.corinne_be.dto.rank_dto.RankDto;
import com.corinne.corinne_be.dto.rank_dto.RankInfoDto;
import com.corinne.corinne_be.model.Coin;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.CoinRepository;
import com.corinne.corinne_be.repository.RedisRepository;
import com.corinne.corinne_be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RankUtil {
    private final RedisRepository redisRepository;
    private final UserRepository userRepository;

    @Autowired
    public RankUtil(RedisRepository redisRepository, UserRepository userRepository){
        this.redisRepository = redisRepository;
        this.userRepository = userRepository;
    }

    public List<RankInfoDto> getRankList(){
        List<User> Users = userRepository.findAllJPQLFetch();
        List<RankInfoDto> rankDtos = new ArrayList<>();

        for(User user : Users){

            Long totalBalance = 0L;
            Long accountBalance = user.getAccountBalance();

            List<Coin> coins = user.getCoin();

            // 보유 코인별 계산
            totalBalance += totalCoinBalance(coins).getTotalcoinBalance() + accountBalance;

            BigDecimal temp = new BigDecimal(totalBalance - 1000000);
            BigDecimal rateCal = new BigDecimal(10000);
            double fluctuationRate = temp.divide(rateCal,2,RoundingMode.HALF_EVEN).doubleValue();

            RankInfoDto rankDto = new RankInfoDto(user,totalBalance,fluctuationRate);
            rankDtos.add(rankDto);
        }

        rankDtos.stream().sorted(Comparator.comparing(RankInfoDto::getTotalBalance).reversed()).collect(Collectors.toList());

        int i = 1;
        for(RankInfoDto rankDto : rankDtos){
            rankDto.setRank(i);
            i++;
        }

        return rankDtos;
    }

    public Map<Long, RankInfoDto> getRankMap(){
        Map<Long, RankInfoDto> rankInfoDtoMap = new LinkedHashMap<>();
        List<RankInfoDto> rankDtos = getRankList();

        for(RankInfoDto rankDto : rankDtos){
            rankInfoDtoMap.put(rankDto.getUserId(), rankDto);
        }
        return rankInfoDtoMap;
    }


    public MyRankDto getMyRank(Long userId){

        return new MyRankDto(getRankMap().get(userId));
    }


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
