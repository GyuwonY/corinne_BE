package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.rank_dto.*;
import com.corinne.corinne_be.model.Coin;
import com.corinne.corinne_be.model.Follower;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.CoinRepository;
import com.corinne.corinne_be.repository.FollowerRepository;
import com.corinne.corinne_be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankService {

    private final CoinRepository coinRepository;
    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;

    @Autowired
    public RankService(CoinRepository coinRepository, UserRepository userRepository, FollowerRepository followerRepository) {
        this.coinRepository = coinRepository;
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
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

            Follower follower = followerRepository.findByUser_UserIdAndFollower_UserId(loginUser.getUserId(),user.getUserId()).orElse(null);
            boolean follow = follower != null;

            RankDto rankListDto = new RankDto(user.getUserId(), user.getNickname(),user.getImageUrl(),totalBalance,fluctuationRate, follow);
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
        for(int i = 0; i < 3; i++){
            rankDtoList.add(rankDtos.get(i));
        }

        return  new ResponseEntity<>(new RankTopDto(rankDtoList), HttpStatus.OK);
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
    public int getTotalCoinBalance(List<Coin> coins){
        int totalcoinBalance = 0;

        for(Coin coin : coins){
            // ---> 임의로 넣은 현재가 가격 현재가 수정 필수
            int currentTempPrice = 100;

            // 현재 보유 코인값 계산  수정 필수
            BigDecimal temp = new BigDecimal(coin.getAmount() * currentTempPrice);
            BigDecimal buyPrice = BigDecimal.valueOf(coin.getBuyPrice());
            BigDecimal currentPrice = temp.divide(buyPrice, RoundingMode.CEILING);


            totalcoinBalance += currentPrice.intValue();
        }
        return totalcoinBalance;
    }
}

























