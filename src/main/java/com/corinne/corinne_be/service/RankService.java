package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.user_dto.MyRankDto;
import com.corinne.corinne_be.dto.user_dto.MyRankResponseDto;
import com.corinne.corinne_be.dto.user_dto.RankDto;
import com.corinne.corinne_be.dto.user_dto.RankResponseDto;
import com.corinne.corinne_be.model.Coin;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.CoinRepository;
import com.corinne.corinne_be.repository.UserRepository;
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

    @Autowired
    public RankService(CoinRepository coinRepository, UserRepository userRepository) {
        this.coinRepository = coinRepository;
        this.userRepository = userRepository;
    }


    
    // 랭킹 리스트
    public ResponseEntity<?> getRank(User loginUser) {

        List<User> Users = userRepository.findAll();

        List<RankDto> rankDtos = new ArrayList<>();

        for(User user : Users){

            int totalBalance = 0;
            Long accountBalance = user.getAccountBalance();

            List<Coin> coins = coinRepository.findAllByUser_UserId(user.getUserId());

            // 보유 코인별 계산
            for(Coin coin : coins){
                // ---> 임의로 넣은 현재가 가격 현재가 수정 필수
                int currentTempPrice = 100;

                // 현재 보유 코인값 계산  수정 필수
                BigDecimal temp = new BigDecimal(coin.getAmount() * currentTempPrice);
                BigDecimal buyPrice = new BigDecimal(coin.getBuyPrice());
                BigDecimal currentPrice = temp.divide(buyPrice, RoundingMode.CEILING);


                totalBalance += currentPrice.intValue();
            }
            
            totalBalance += accountBalance;

            BigDecimal temp = new BigDecimal(totalBalance - 1000000);
            BigDecimal rateCal = new BigDecimal(10000);
            double fluctuationRate = temp.divide(rateCal,2,RoundingMode.HALF_EVEN).doubleValue();


            RankDto rankDto = new RankDto(user.getUserId(), user.getNickname(),user.getImageUrl(),totalBalance,fluctuationRate);
            rankDtos.add(rankDto);
        }

        rankDtos = rankDtos.stream().sorted(Comparator.comparing(RankDto::getTotalBalance).reversed()).collect(Collectors.toList());

        List<Long> userIds = new ArrayList<>();
        for(RankDto rankDto : rankDtos){
            userIds.add(rankDto.getUserId());
        }

        int myRank = userIds.indexOf(loginUser.getUserId()) + 1;

        MyRankResponseDto myRankResponseDto = new MyRankResponseDto(myRank,rankDtos);

        return  new ResponseEntity<>(myRankResponseDto, HttpStatus.OK);
    }

    // 상위 랭킹 리스트
    public ResponseEntity<?> getRankTop3() {

        List<User> Users = userRepository.findAll();

        List<RankDto> rankDtos = new ArrayList<>();

        for(User user : Users){

            int totalBalance = 0;
            Long accountBalance = user.getAccountBalance();

            List<Coin> coins = coinRepository.findAllByUser_UserId(user.getUserId());

            // 보유 코인별 계산
            for(Coin coin : coins){
                // ---> 임의로 넣은 현재가 가격 현재가 수정 필수
                int currentTempPrice = 100;

                // 현재 보유 코인값 계산  수정 필수
                BigDecimal temp = new BigDecimal(coin.getAmount() * currentTempPrice);
                BigDecimal buyPrice = new BigDecimal(coin.getBuyPrice());
                BigDecimal currentPrice = temp.divide(buyPrice, RoundingMode.CEILING);


                totalBalance += currentPrice.intValue();
            }

            totalBalance += accountBalance;

            BigDecimal temp = new BigDecimal(totalBalance - 1000000);
            BigDecimal rateCal = new BigDecimal(10000);
            double fluctuationRate = temp.divide(rateCal,2,RoundingMode.HALF_EVEN).doubleValue();


            RankDto rankDto = new RankDto(user.getUserId(), user.getNickname(),user.getImageUrl(),totalBalance,fluctuationRate);
            rankDtos.add(rankDto);
        }

        rankDtos = rankDtos.stream().sorted(Comparator.comparing(RankDto::getTotalBalance).reversed()).collect(Collectors.toList());

        List<RankDto> rankDtoList = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            rankDtoList.add(rankDtos.get(i));
        }

        RankResponseDto rankResponseDto = new RankResponseDto(rankDtoList);

        return  new ResponseEntity<>(rankResponseDto, HttpStatus.OK);
    }

    // 내 랭킹
    public ResponseEntity<?> getMyRank(User loginUser) {

        List<User> Users = userRepository.findAll();

        List<RankDto> rankDtos = new ArrayList<>();

        for(User user : Users){

            int totalBalance = 0;
            Long accountBalance = user.getAccountBalance();

            List<Coin> coins = coinRepository.findAllByUser_UserId(user.getUserId());

            // 보유 코인별 계산
            for(Coin coin : coins){
                // ---> 임의로 넣은 현재가 가격 현재가 수정 필수
                int currentTempPrice = 100;

                // 현재 보유 코인값 계산  수정 필수
                BigDecimal temp = new BigDecimal(coin.getAmount() * currentTempPrice);
                BigDecimal buyPrice = new BigDecimal(coin.getBuyPrice());
                BigDecimal currentPrice = temp.divide(buyPrice, RoundingMode.CEILING);


                totalBalance += currentPrice.intValue();
            }

            totalBalance += accountBalance;

            BigDecimal temp = new BigDecimal(totalBalance - 1000000);
            BigDecimal rateCal = new BigDecimal(10000);
            double fluctuationRate = temp.divide(rateCal,2,RoundingMode.HALF_EVEN).doubleValue();


            RankDto rankDto = new RankDto(user.getUserId(), user.getNickname(),user.getImageUrl(),totalBalance,fluctuationRate);
            rankDtos.add(rankDto);
        }

        rankDtos = rankDtos.stream().sorted(Comparator.comparing(RankDto::getTotalBalance).reversed()).collect(Collectors.toList());

        List<Long> userIds = new ArrayList<>();
        for(RankDto rankDto : rankDtos){
            userIds.add(rankDto.getUserId());
        }

        int myRankIndex = userIds.indexOf(loginUser.getUserId());

        MyRankDto myRankDto = new MyRankDto(myRankIndex + 1,rankDtos.get(myRankIndex).getFluctuationRate());

        return  new ResponseEntity<>(myRankDto, HttpStatus.OK);
    }
}

























