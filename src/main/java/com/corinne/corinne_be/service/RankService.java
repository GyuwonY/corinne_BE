package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.rank_dto.*;
import com.corinne.corinne_be.model.Coin;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankService {

    private final CoinRepository coinRepository;
    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final TransactionRepository transactionRepository;
    private final RedisRepository redisRepository;

    @Autowired
    public RankService(CoinRepository coinRepository, UserRepository userRepository, FollowerRepository followerRepository, TransactionRepository transactionRepository, RedisRepository redisRepository) {
        this.coinRepository = coinRepository;
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
        this.transactionRepository = transactionRepository;
        this.redisRepository = redisRepository;
    }


    // 랭킹 리스트
    public ResponseEntity<?> getRank(int page, User loginUser) {

        // 페이징 사이즈
        int size = 3;

        List<User> Users = userRepository.findAll();

        List<RankDto> rankDtos = new ArrayList<>();

        for(User user : Users){

            Long totalBalance = 0L;

            Long accountBalance = user.getAccountBalance();

            List<Coin> coins = coinRepository.findAllByUser_UserId(user.getUserId());

            // 보유 코인별 계산
            totalBalance = getTotalCoinBalance(coins) +  accountBalance;

            BigDecimal temp = new BigDecimal(totalBalance - 1000000);
            BigDecimal rateCal = new BigDecimal(10000);
            double fluctuationRate = temp.divide(rateCal,2,RoundingMode.HALF_EVEN).doubleValue();

            boolean follow = followerRepository.existsByUser_UserIdAndFollower_UserId(loginUser.getUserId(),user.getUserId());

            int restCount = transactionRepository.countByUser_UserIdAndType(user.getUserId(),"reset").intValue();

            int exp = user.getExp();

            RankDto rankListDto = new RankDto(user.getUserId(), user.getNickname(),user.getImageUrl(),totalBalance,fluctuationRate, follow,restCount,exp);

            rankDtos.add(rankListDto);
        }

        rankDtos = rankDtos.stream().sorted(Comparator.comparing(RankDto::getTotalBalance).reversed()).collect(Collectors.toList());

        List<Long> userIds = new ArrayList<>();
        for(RankDto rankDto : rankDtos){
            userIds.add(rankDto.getUserId());
        }

        int myRank = userIds.indexOf(loginUser.getUserId()) + 1;

        // 페이징
        if(page <= 0) {
            return new ResponseEntity<>("올바르지 않는 페이지 요청입니다", HttpStatus.BAD_REQUEST);
        }
        int fromIndex = (page - 1) * size;

        int totalPage = rankDtos.size()/3 + 1;
        if(rankDtos.size()%3 == 0){
            totalPage -= 1;
        }
        if(rankDtos.size() <= fromIndex){
            return new ResponseEntity<>("올바르지 않는 페이지 요청입니다", HttpStatus.BAD_REQUEST);
        }

        MyRankResponseDto myRankResponseDto = new MyRankResponseDto(myRank,rankDtos.subList(fromIndex,Math.min(fromIndex + size, rankDtos.size())), totalPage);

        return  new ResponseEntity<>(myRankResponseDto, HttpStatus.OK);
    }


    // 상위 랭킹 리스트
    public ResponseEntity<?> getRankTop3() {

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


            RankInfoDto rankDto = new RankInfoDto(user.getUserId(), user.getNickname(),user.getImageUrl(), totalBalance,fluctuationRate);
            rankDtos.add(rankDto);
        }

        rankDtos = rankDtos.stream().sorted(Comparator.comparing(RankInfoDto::getTotalBalance).reversed()).collect(Collectors.toList());

        List<RankInfoDto> rankDtoList = new ArrayList<>();

        int size = Math.min(rankDtos.size(), 3);

        for(int i = 0; i < size; i++){
            rankDtoList.add(rankDtos.get(i));
        }

        return new ResponseEntity<>(new RankTopDto(rankDtoList), HttpStatus.OK);
    }

    // 내 랭킹
    public ResponseEntity<?> getMyRank(User loginUser) {

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

        rankDtos = rankDtos.stream().sorted(Comparator.comparing(RankInfoDto::getTotalBalance).reversed()).collect(Collectors.toList());

        List<Long> userIds = new ArrayList<>();
        for(RankInfoDto rankDto : rankDtos){
            userIds.add(rankDto.getUserId());
        }

        int myRankIndex = userIds.indexOf(loginUser.getUserId());

        MyRankDto myRankDto = new MyRankDto(myRankIndex + 1,rankDtos.get(myRankIndex).getFluctuationRate());

        return  new ResponseEntity<>(myRankDto, HttpStatus.OK);
    }

    // 보유 코인 값 구하기
    public Long getTotalCoinBalance(List<Coin> coins){
        Long totalcoinBalance = 0L;

        for(Coin coin : coins){
            
            // 살 당시 코인 현재가
            BigDecimal buyPrice = BigDecimal.valueOf(coin.getBuyPrice());
            // 현재가
            BigDecimal currentPrice = BigDecimal.valueOf(redisRepository.getTradePrice(coin.getTiker()));
            // 래버리지
            BigDecimal leverage = BigDecimal.valueOf(coin.getLeverage());
            // 구매 총금액
            BigDecimal amount = BigDecimal.valueOf(coin.getAmount());
            // 래버리지 적용한 수익률
            BigDecimal fluctuationRate = currentPrice.subtract(buyPrice).multiply(leverage).divide(buyPrice,8,RoundingMode.HALF_EVEN);
            // 현재 해당 코인의 가치
            Long coinBalance = fluctuationRate.add(BigDecimal.valueOf(1)).multiply(amount).setScale(0,RoundingMode.CEILING).longValue();

            totalcoinBalance += coinBalance;
        }
        return totalcoinBalance;
    }
}

























