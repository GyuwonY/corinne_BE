package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.account_dto.AccountResponseDto;
import com.corinne.corinne_be.dto.account_dto.AccountSimpleDto;
import com.corinne.corinne_be.dto.account_dto.CoinsDto;
import com.corinne.corinne_be.dto.transaction_dto.TransactionDto;
import com.corinne.corinne_be.model.Coin;
import com.corinne.corinne_be.model.Transaction;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.CoinRepository;
import com.corinne.corinne_be.repository.RedisRepository;
import com.corinne.corinne_be.repository.TransactionRepository;
import com.corinne.corinne_be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {

    private final CoinRepository coinRepository;
    private final UserRepository userRepository;
    private final RedisRepository redisRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public AccountService(CoinRepository coinRepository, UserRepository userRepository, RedisRepository redisRepository, TransactionRepository transactionRepository) {
        this.coinRepository = coinRepository;
        this.userRepository = userRepository;
        this.redisRepository = redisRepository;
        this.transactionRepository = transactionRepository;
    }



    // 보유 자산
   public ResponseEntity<?> getBalance(User user) {

        // 사용 가능한 포인트
        Long accountBalance = user.getAccountBalance();

        // 총  보유 코인 재산
       Long totalCoinBalance = 0L;
        // 보유중인 코인 리스트
        List<Coin> haveCoins = coinRepository.findAllByUser_UserId(user.getUserId());
        
        // 보유중인 코인 정보 리스트
       List<CoinsDto> coins = new ArrayList<>();

       List<Long> coinBalances = new ArrayList<>();

        for(Coin coin : haveCoins){

            String tiker = coin.getTiker();

            // 살 당시 코인 현재가
            BigDecimal buyPrice = BigDecimal.valueOf(coin.getBuyPrice());
            // 현재가
            BigDecimal currentPrice = BigDecimal.valueOf(redisRepository.getTradePrice(coin.getTiker()));
            // 래버리지
            BigDecimal leverage = BigDecimal.valueOf(coin.getLeverage());
            // 구매 총금액
            BigDecimal amount = BigDecimal.valueOf(coin.getAmount());
            // 래버리지 적용한 수익률
            BigDecimal fluctuationRate = currentPrice.subtract(buyPrice).multiply(leverage).divide(buyPrice,2,RoundingMode.HALF_UP);
            // 현재 해당 코인의 가치
            Long coinBalance = fluctuationRate.add(BigDecimal.valueOf(1)).multiply(amount).setScale(0,RoundingMode.CEILING).longValue();

            totalCoinBalance += coinBalance;
            coinBalances.add(coinBalance);

            CoinsDto coinsDto = new CoinsDto(tiker,buyPrice.doubleValue(),currentPrice.intValue(),leverage.intValue(),fluctuationRate.doubleValue() * 100 );
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

        Coin coin = coinRepository.findByTikerAndUser_UserId(tiker,user.getUserId()).orElse(null);
        if(coin == null){
            return  new ResponseEntity<>("보유한 코인이 아닙니다" ,HttpStatus.BAD_REQUEST);
        }
        Long accountBalance = user.getAccountBalance();
        return  new ResponseEntity<>(new AccountSimpleDto(accountBalance, coin.getBuyPrice() , coin.getAmount(), coin.getLeverage()) ,HttpStatus.OK);
    }

    // 보유 자산 리셋
    @Transactional
    public ResponseEntity<?> resetAccount(User user) {

        user.update(1000000L);
        userRepository.save(user);

        // 보유 코인 지우기
        coinRepository.deleteAllByUser_UserId(user.getUserId());

        // 리셋 내역 추가
        TransactionDto transactionDto = new TransactionDto(user, "reset", 0, 1000000L, "reset", 1);
        Transaction transaction = new Transaction(transactionDto);
        transactionRepository.save(transaction);

        return  new ResponseEntity<>(HttpStatus.OK);
    }
}


















