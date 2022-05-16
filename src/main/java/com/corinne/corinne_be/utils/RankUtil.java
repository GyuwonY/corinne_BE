package com.corinne.corinne_be.utils;

import com.corinne.corinne_be.dto.rank_dto.MyRankDto;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RankUtil {
    private final RedisRepository redisRepository;
    private final UserRepository userRepository;
    private final CoinRepository coinRepository;

    @Autowired
    public RankUtil(RedisRepository redisRepository, UserRepository userRepository, CoinRepository coinRepository){
        this.redisRepository = redisRepository;
        this.userRepository = userRepository;
        this.coinRepository = coinRepository;
    }

    public List<RankInfoDto> getRankList(){
        List<User> Users = userRepository.findAll();

        List<RankInfoDto> rankDtos = new ArrayList<>();

        for(User user : Users){

            Long totalBalance = 0L;
            Long accountBalance = user.getAccountBalance();

            List<Coin> coins = coinRepository.findAllByUser_UserId(user.getUserId());

            // 보유 코인별 계산
            totalBalance += getTotalCoinBalance(coins) + accountBalance;

            BigDecimal temp = new BigDecimal(totalBalance - 1000000);
            BigDecimal rateCal = new BigDecimal(10000);
            double fluctuationRate = temp.divide(rateCal,2,RoundingMode.HALF_EVEN).doubleValue();

            RankInfoDto rankDto = new RankInfoDto(user.getUserId(), user.getNickname(),user.getImageUrl(),totalBalance,fluctuationRate);
            rankDtos.add(rankDto);
        }

        return rankDtos.stream().sorted(Comparator.comparing(RankInfoDto::getTotalBalance).reversed()).collect(Collectors.toList());
    }


    public MyRankDto getMyRank(Long userId){
        List<RankInfoDto> rankDtos = getRankList();

        List<Long> userIds = new ArrayList<>();
        for(RankInfoDto rankDto : rankDtos){
            userIds.add(rankDto.getUserId());
        }

        int myRankIndex = userIds.indexOf(userId);

        return new MyRankDto(myRankIndex + 1,rankDtos.get(myRankIndex).getFluctuationRate(), rankDtos.get(myRankIndex).getTotalBalance());
    }


    public Long getTotalCoinBalance(List<Coin> coins){
        Long totalcoinBalance = 0L;

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
        }
        return totalcoinBalance;
    }
}
