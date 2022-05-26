package com.corinne.corinne_be.utils;

import com.corinne.corinne_be.dto.rank_dto.MyRankDto;
import com.corinne.corinne_be.dto.rank_dto.RankInfoDto;
import com.corinne.corinne_be.model.Coin;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RankUtil {
    private final UserRepository userRepository;
    private final BalanceUtil balanceUtil;

    @Autowired
    public RankUtil(UserRepository userRepository, BalanceUtil balanceUtil){
        this.userRepository = userRepository;
        this.balanceUtil = balanceUtil;
    }

    public List<RankInfoDto> getRankList(){
        List<User> Users = userRepository.findAllJPQLFetch();
        List<RankInfoDto> rankDtos = new ArrayList<>();

        for(User user : Users){

            Long totalBalance = 0L;
            Long accountBalance = user.getAccountBalance();

            List<Coin> coins = user.getCoin();

            // 보유 코인별 계산
            totalBalance += balanceUtil.totalCoinBalance(coins).getTotalcoinBalance() + accountBalance;

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

        List<RankInfoDto> rankDtos = getRankList();

        Map<Long, RankInfoDto> rankInfoDtoMap = new LinkedHashMap<>();

        for(RankInfoDto rankDto : rankDtos){
            rankInfoDtoMap.put(rankDto.getUserId(), rankDto);
        }
        return rankInfoDtoMap;
    }


    public MyRankDto getMyRank(Long userId){

        return new MyRankDto(getRankMap().get(userId));
    }

}
