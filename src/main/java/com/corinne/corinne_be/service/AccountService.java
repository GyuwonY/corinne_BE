package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.account_dto.AccountResponseDto;
import com.corinne.corinne_be.dto.account_dto.AccountSimpleDto;
import com.corinne.corinne_be.dto.account_dto.CoinsDto;
import com.corinne.corinne_be.model.Coin;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.CoinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {
    ;
    private final CoinRepository coinRepository;

    @Autowired
    public AccountService( CoinRepository coinRepository) {
        this.coinRepository = coinRepository;
    }


    // 보유 자산
   public ResponseEntity<?> getBalance(User user) {

        // ---> 임의로 넣은 현재가 가격 현재가 수정 필수
        int currentTempPrice = 100;

        // 사용 가능한 포인트
        Long accountBalance = user.getAccountBalance();

        // 총  보우 코인 재산
       long totalCoinBalance = 0;
        // 보유중인 코인 리스트
        List<Coin> haveCoins = coinRepository.findAllByUser_UserId(user.getUserId());
        
        // 보유중인 코인 정보 리스트
       List<CoinsDto> coins = new ArrayList<>();


       List<Integer> coinBalances = new ArrayList<>();

        for(Coin coin : haveCoins){
            String tiker = coin.getTiker();
            double buyPrice = coin.getBuyPrice();

            // 코인의 현재가 수정 필수
            int tradePrice = currentTempPrice;

            // 현재 수익률 계산 현재가 수정 필수
            BigDecimal fluctuationtempCal = BigDecimal.valueOf(((double) tradePrice - buyPrice) * 100);
            double coinfluctuationRate = fluctuationtempCal.divide(new BigDecimal(buyPrice),2, RoundingMode.HALF_EVEN).doubleValue();

            // 현재 보유한 코인 balance 현재가 수정 필수
            BigDecimal coinBalanceTampCal = new BigDecimal(tradePrice*coin.getAmount());
            int currentcoinBalance = coinBalanceTampCal.divide(new BigDecimal(buyPrice), RoundingMode.CEILING).intValue();

            totalCoinBalance += currentcoinBalance;
            coinBalances.add(currentcoinBalance);


            CoinsDto coinsDto = new CoinsDto(tiker,buyPrice,tradePrice,coinfluctuationRate);
            coins.add(coinsDto);
        }

        Long totalBalance = totalCoinBalance  + accountBalance;

       // 수익률 계산
        BigDecimal temp = new BigDecimal(totalBalance - 1000000);
        BigDecimal rateCal = new BigDecimal(10000);
        double fluctuationRate = temp.divide(rateCal,2,RoundingMode.HALF_EVEN).doubleValue();


        for(int i = 0; i < coins.size(); i++){
            long balance = coinBalances.get(i);
            // 원그래프 비중 계산
            BigDecimal importanceRateCal = new BigDecimal(balance * 100);
            double importanceRate = importanceRateCal.divide(new BigDecimal(totalCoinBalance),2, RoundingMode.HALF_EVEN).doubleValue();
            coins.get(i).setImportanceRate(importanceRate);
        }

        return  new ResponseEntity<>(new AccountResponseDto(user.getLastFluctuation(), accountBalance,totalBalance,fluctuationRate,coins), HttpStatus.OK);
    }



    // 모의투자페이지 자산
    public ResponseEntity<?> getSimpleBalance(String tiker, User user) {

        // ---> 임의로 넣은 현재가 가격 현재가 수정 필수
        int currentTempPrice = 100;

        Coin coin = coinRepository.findByTikerAndUser_UserId(tiker,user.getUserId()).orElse(null);

        // 현재 보유한 코인 balance 현재가 수정 필수
        Long accountBalance = user.getAccountBalance();

        if(coin == null){
            return  new ResponseEntity<>(new AccountSimpleDto(accountBalance,0),HttpStatus.OK);
        }

        // ---> 임의로 넣은 현재가 가격 현재가 수정 필수
        BigDecimal coinBalanceTampCal = new BigDecimal(currentTempPrice * coin.getAmount());
        int coinBalance = coinBalanceTampCal.divide(BigDecimal.valueOf(coin.getBuyPrice()), RoundingMode.CEILING).intValue();

        return  new ResponseEntity<>(new AccountSimpleDto(accountBalance,coinBalance),HttpStatus.OK);
    }
}


















