package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.transaction_dto.*;
import com.corinne.corinne_be.exception.CustomException;
import com.corinne.corinne_be.exception.ErrorCode;
import com.corinne.corinne_be.model.Coin;
import com.corinne.corinne_be.model.Quest;
import com.corinne.corinne_be.model.Transaction;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Service
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CoinRepository coinRepository;
    private final UserRepository userRepository;
    private final RedisRepository redisRepository;
    private final QuestRepository questRepository;
    List<String> tikers = Arrays.asList("KRW-BTC", "KRW-SOL", "KRW-ETH", "KRW-XRP", "KRW-ADA", "KRW-DOGE", "KRW-AVAX", "KRW-DOT", "KRW-MATIC");

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, CoinRepository coinRepository,
                              UserRepository userRepository, RedisRepository redisRepository, QuestRepository questRepository) {
        this.transactionRepository = transactionRepository;
        this.coinRepository = coinRepository;
        this.userRepository = userRepository;
        this.redisRepository = redisRepository;
        this.questRepository = questRepository;
    }

    //코인 거래 내역
    @Transactional
    public ResponseEntity<Page<TransactionResponseDto>> transactionalList(int page, int size, String sortBy, User user) {

        if(page < 0) {
            throw new CustomException(ErrorCode.WRONG_VALUE_PAGE);
        }

        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Transaction> entities = transactionRepository.findAllByUser_UserId(user.getUserId(),pageable);
        Page<TransactionResponseDto> transactionDtos =  pageReturnSwitch(entities);

        return new ResponseEntity<>(transactionDtos, HttpStatus.OK);
    }

    // 해당 코인 거래내역
    @Transactional
    public ResponseEntity<Page<TransactionResponseDto>> specifiedTransactional(int page, int size, String sortBy, String coinName, User user) {

        if(page < 0) {
            throw new CustomException(ErrorCode.WRONG_VALUE_PAGE);
        }

        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Transaction> entities = transactionRepository.findAllByTikerAndUser_UserId(coinName,user.getUserId(),pageable);
        Page<TransactionResponseDto> transactionDtos =  pageReturnSwitch(entities);

        return new ResponseEntity<>(transactionDtos, HttpStatus.OK);
    }

    // 페이징시 리턴값 교체 리팩토링 메소드
    @Transactional
    public Page<TransactionResponseDto> pageReturnSwitch(Page<Transaction> entities){

        return entities.map(transaction -> {
            String tiker = transaction.getTiker();
            String type = transaction.getType();
            int buyPrice = transaction.getBuyprice();
            Long amount = transaction.getAmount();
            String tradeAt = transaction.getTradeAt().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss"));
            int leverage = transaction.getLeverage();
            return new TransactionResponseDto(tiker,type,buyPrice,amount,tradeAt,leverage);
        });
    }


    // 매수
    @Transactional
    public ResponseEntity<BuyResponseDto> buy(BuyRequestDto buyRequestDto, User user) {

        if(!tikers.contains(buyRequestDto.getTiker())){
            throw new CustomException(ErrorCode.NON_EXIST_TIKER);
        }

        if(user.getAccountBalance() < buyRequestDto.getBuyAmount() || buyRequestDto.getBuyAmount() < 50000 || buyRequestDto.getTradePrice() < 20){
            throw new CustomException(ErrorCode.WRONG_AMOUNT);
        }

        if(buyRequestDto.getLeverage()!=1 && buyRequestDto.getLeverage()!=25 &&
                buyRequestDto.getLeverage()!=50 && buyRequestDto.getLeverage()!=75 && buyRequestDto.getLeverage()!=100){
            throw new CustomException(ErrorCode.WRONG_LEVERAGE);
        }

        Quest quest;

        if(buyRequestDto.getLeverage()==1) {
            quest = questRepository.findByUser_UserIdAndQuestNo(user.getUserId(), 2).orElse(null);
        }else {
            quest = questRepository.findByUser_UserIdAndQuestNo(user.getUserId(), 4).orElse(null);
        }

        if (quest != null) {
            if (!quest.isClear()) {
                quest.update(true);
            }
        }

        Coin coin = coinRepository.findByTikerAndUser_UserIdAndLeverage(buyRequestDto.getTiker(),
                user.getUserId(), buyRequestDto.getLeverage()).orElse(null);
        Long accountBalance = user.getAccountBalance();

        // 보유한 코인의 현재가 or 평균가
        double buyPrice = 0.0;
        // 보유한 코인 총 값
        Long amount = 0L;

        if(coin != null){
            // 이전 코인과 지금 코인의 평균가 계산
            double avgPrice = BigDecimal.valueOf(buyRequestDto.getTradePrice() + coin.getBuyPrice()).
                    divide(BigDecimal.valueOf(2),8,RoundingMode.HALF_UP).doubleValue();
            buyPrice = avgPrice;

            // 이전 코인과 지금 코인의 평균가에 따른 현재 코인량
            BigDecimal preBalance = BigDecimal.valueOf(avgPrice).multiply(BigDecimal.valueOf(coin.getAmount())).
                    divide(BigDecimal.valueOf(coin.getBuyPrice()),0,RoundingMode.CEILING);
            BigDecimal nowBalance = BigDecimal.valueOf(avgPrice).multiply(BigDecimal.valueOf(buyRequestDto.getBuyAmount())).
                    divide(BigDecimal.valueOf(buyRequestDto.getTradePrice()),0,RoundingMode.CEILING);

            Long coinBalance = preBalance.add(nowBalance).longValue();
            amount = coinBalance;

            // 보유한 코인량 변경
            coin.update(avgPrice, coinBalance);
        } else {
            buyPrice = buyRequestDto.getTradePrice();
            amount = buyRequestDto.getBuyAmount();
            Coin saveCoin = new Coin(user, buyRequestDto);

            coin = coinRepository.save(saveCoin);
        }

        //redis 현재가를 이용한 파산 구현을 위한 리스트 저장
        if(buyRequestDto.getLeverage() == 50){
            redisRepository.saveBankruptcy(new BankruptcyDto(buyRequestDto.getTiker(), user.getUserId(), coin.getCoinId(),
                    BigDecimal.valueOf(buyPrice).multiply(BigDecimal.valueOf(0.98)).setScale(0,RoundingMode.FLOOR).intValue()));
        }else if(buyRequestDto.getLeverage() == 100){
            redisRepository.saveBankruptcy(new BankruptcyDto(buyRequestDto.getTiker(), user.getUserId(), coin.getCoinId(),
                    BigDecimal.valueOf(buyPrice).multiply(BigDecimal.valueOf(0.99)).setScale(0,RoundingMode.FLOOR).intValue()));
        }else if(buyRequestDto.getLeverage() == 25){
            redisRepository.saveBankruptcy(new BankruptcyDto(buyRequestDto.getTiker(), user.getUserId(), coin.getCoinId(),
                    BigDecimal.valueOf(buyPrice).multiply(BigDecimal.valueOf(0.96)).setScale(0,RoundingMode.FLOOR).intValue()));
        }else if(buyRequestDto.getLeverage() == 75){
            redisRepository.saveBankruptcy(new BankruptcyDto(buyRequestDto.getTiker(), user.getUserId(), coin.getCoinId(),
                    BigDecimal.valueOf(buyPrice).multiply(BigDecimal.valueOf(0.98777777777777777)).setScale(0,RoundingMode.FLOOR).intValue()));
        }

        // 구매할때 사용한 포인트 차감 저장
        user.balanceUpdate(accountBalance - buyRequestDto.getBuyAmount());
        userRepository.save(user);

        Long commission = (long)(buyRequestDto.getBuyAmount()*buyRequestDto.getLeverage()*0.0005);

        TransactionDto transactionDto = new TransactionDto(user, "buy", buyRequestDto.getTradePrice(),
                buyRequestDto.getBuyAmount(), buyRequestDto.getTiker(), buyRequestDto.getLeverage(), commission);
        Transaction transaction = new Transaction(transactionDto);

        // 거래 내역 입력
        Transaction saveTran = transactionRepository.save(transaction);

        String tradeAt = saveTran.getTradeAt().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss"));

        BuyResponseDto buyResponseDto = new BuyResponseDto(user.getAccountBalance(),buyPrice,amount,"buy",tradeAt,
                buyRequestDto.getLeverage());
        
        return new ResponseEntity<>(buyResponseDto, HttpStatus.OK);
    }

    // 매도
    @Transactional
    public ResponseEntity<SellResponseDto> sell(SellRequestDto sellRequestDto, User user) {

        if(!tikers.contains(sellRequestDto.getTiker())){
            throw new CustomException(ErrorCode.NON_EXIST_TIKER);
        }

        if(sellRequestDto.getSellAmount()<=0){
            throw new CustomException(ErrorCode.WRONG_AMOUNT);
        }

        Coin coin = coinRepository.findByTikerAndUser_UserIdAndLeverage(sellRequestDto.getTiker(), user.getUserId(), sellRequestDto.getLeverage()).orElse(null);

        Long accountBalance = user.getAccountBalance();

        if(coin == null){
            throw new CustomException(ErrorCode.NON_EXIST_COIN);
        }

        if(coin.getLeverage() != 1L){
            redisRepository.deleteBankruptcy(coin.getCoinId(), coin.getTiker());
        }

        Quest quest;

        if(sellRequestDto.getLeverage()==1) {
            quest = questRepository.findByUser_UserIdAndQuestNo(user.getUserId(), 3).orElse(null);
        }else {
            quest = questRepository.findByUser_UserIdAndQuestNo(user.getUserId(), 5).orElse(null);
        }

        if (quest != null) {
            if (!quest.isClear()) {
                quest.update(true);
            }
        }

        BigDecimal amount = BigDecimal.valueOf(coin.getAmount());
        BigDecimal buyPrice = BigDecimal.valueOf(coin.getBuyPrice());
        BigDecimal sellPrice = BigDecimal.valueOf(sellRequestDto.getTradePrice());
        BigDecimal leverage = BigDecimal.valueOf(sellRequestDto.getLeverage());
        BigDecimal sellAmount = BigDecimal.valueOf(sellRequestDto.getSellAmount());
        //등략률
        BigDecimal fluctuation = (sellPrice.subtract(buyPrice)).multiply(leverage).divide(buyPrice,8, RoundingMode.HALF_EVEN);
        //판매 가능 금액
        BigDecimal sellable = amount.multiply(fluctuation).add(amount);
        //판매 비율
        BigDecimal sellRate = sellAmount.divide(sellable, 2, RoundingMode.HALF_UP);

        Long leftover;
        if(sellable.intValue() >= sellRequestDto.getSellAmount()){
            leftover = amount.subtract(amount.multiply(sellRate)).longValue();
            if (leftover == 0L){
                coinRepository.delete(coin);
            } else {
                coin.update(leftover);
            }
        } else {
            throw new CustomException(ErrorCode.WRONG_AMOUNT);
        }

        user.balanceUpdate(accountBalance + sellRequestDto.getSellAmount());
        userRepository.save(user);

        Long commission = (long)(sellRequestDto.getSellAmount()*0.0005);

        // 매도 거래내역 추가
        TransactionDto transactionDto = new TransactionDto(user, "sell", sellRequestDto.getTradePrice(),
                sellRequestDto.getSellAmount(), sellRequestDto.getTiker(), sellRequestDto.getLeverage(), commission);
        Transaction transaction = new Transaction(transactionDto);
        Transaction saveTran = transactionRepository.save(transaction);

        String tradeAt = saveTran.getTradeAt().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss"));

        SellResponseDto sellResponseDto = new SellResponseDto(user.getAccountBalance(),sellRequestDto.getTradePrice(),
                sellRequestDto.getSellAmount(),"sell",tradeAt, sellRequestDto.getLeverage(), leftover);

        return new ResponseEntity<>(sellResponseDto,HttpStatus.OK);
    }


    // 상대방 최근 거래내역 보기
    @Transactional
    public ResponseEntity<UserTranResponseDto> userTranstnal(Long userId) {

        List<Transaction> transactionList = transactionRepository.findTop5ByUser_UserIdOrderByTradeAtDesc(userId);
        List<TransactionResponseDto> tranDtos = new ArrayList<>();

        for(Transaction transaction : transactionList){
            String tiker = transaction.getTiker();
            String type = transaction.getType();
            int price = transaction.getBuyprice();
            Long amount = transaction.getAmount();
            String tradeAt = transaction.getTradeAt().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss"));
            int leverage = transaction.getLeverage();
            tranDtos.add(new TransactionResponseDto(tiker,type,price,amount,tradeAt, leverage));
        }

        return new ResponseEntity<>(new UserTranResponseDto(tranDtos),HttpStatus.OK);
    }

    // 코린이 회원 중 특정 코인 매수 카운트
    @Transactional
    public ResponseEntity<BuyCountDto> buyCount(String tiker) {

        if(!tikers.contains(tiker)){
            throw new CustomException(ErrorCode.NON_EXIST_TIKER);
        }

        // 초기화 시점인 월요일 9시 기준
        Calendar cal = Calendar.getInstance();
        if(cal.get(Calendar.DAY_OF_WEEK)==1){
            cal.add(Calendar.DATE, -1);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String mondayDate = dateFormat.format(cal.getTime());
        mondayDate += " 00:00:00.000";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime startDate = LocalDateTime.parse(mondayDate, formatter);
        LocalDateTime endDate = LocalDateTime.now();

        //코린이 회원 중 특정 코인 매수 카운트
        Long buyCount = transactionRepository.countByTikerAndTypeAndTradeAtBetween(tiker,"buy",startDate,endDate);

        return new ResponseEntity<>(new BuyCountDto(buyCount),HttpStatus.OK);
    }
}






















