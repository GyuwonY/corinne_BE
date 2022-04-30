package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.transaction_dto.*;
import com.corinne.corinne_be.model.Coin;
import com.corinne.corinne_be.model.Transaction;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.CoinRepository;
import com.corinne.corinne_be.repository.TransactionRepository;
import com.corinne.corinne_be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CoinRepository coinRepository;
    private final UserRepository userRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, CoinRepository coinRepository,UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.coinRepository = coinRepository;
        this.userRepository = userRepository;
    }


    //코인 거래 내역
    public ResponseEntity<?> getTransactional(int page, int size, String sortBy, User user) {

        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Transaction> entities = transactionRepository.findAllByUser_UserId(user.getUserId(),pageable);
        Page<TransactionResponseDto> transactionDtos =  pageReturnSwitch(entities);

        return new ResponseEntity<>(transactionDtos, HttpStatus.OK);
    }

    // 해당 코인 거래내역
    public ResponseEntity<?> getSpecifiedTranstnal(int page, int size, String sortBy, String coinName, User user) {

        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Transaction> entities = transactionRepository.findAllByTikerAndUser_UserId(coinName,user.getUserId(),pageable);
        Page<TransactionResponseDto> transactionDtos =  pageReturnSwitch(entities);

        return new ResponseEntity<>(transactionDtos, HttpStatus.OK);
    }

    // 페이징시 리턴값 교체 리팩토링 메소드
    public Page<TransactionResponseDto> pageReturnSwitch(Page<Transaction> entities){

        return entities.map(transaction -> {
            String tiker = transaction.getTiker();
            String type = transaction.getType();
            int price = transaction.getPrice();
            String tradeAt = transaction.getTradeAt().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss")
            );
            return  new TransactionResponseDto(tiker,type,price,tradeAt);
        });
    }


    // 매수
    @Transactional
    public ResponseEntity<?> buy(BuyRequestDto buyRequestDto, User user) {

        Coin coin = coinRepository.findByTikerAndUser_UserId(buyRequestDto.getTiker(), user.getUserId()).orElse(null);

        Long accountBalance = user.getAccountBalance();

        // 보유한 코인의 현재가 or 평균가
        double buyPrice = 0.0;
        // 보유한 코인 총 값
        int amount = 0;

        if(coin != null){
            // 이전 코인과 지금 코인의 평균가 계산
            double avgPrice = BigDecimal.valueOf(buyRequestDto.getTradePrice() + coin.getBuyPrice()).divide(BigDecimal.valueOf(2),2,RoundingMode.HALF_UP).doubleValue();
            buyPrice = avgPrice;
            // 이전 코인과 지금 코인의 평균가에 따른 현재 코인량
            BigDecimal preBalance = BigDecimal.valueOf(avgPrice).multiply(BigDecimal.valueOf(coin.getAmount())).divide(BigDecimal.valueOf(coin.getBuyPrice()),RoundingMode.CEILING);
            BigDecimal nowBalance = BigDecimal.valueOf(avgPrice).multiply(BigDecimal.valueOf(buyRequestDto.getBuyAmount())).divide(BigDecimal.valueOf(buyRequestDto.getTradePrice()),RoundingMode.CEILING);
            int coinBalance = preBalance.add(nowBalance).intValue();
            amount = coinBalance;
            // 보유한 코인량 변경
            coin.update(avgPrice, coinBalance);
        } else {
            buyPrice = buyRequestDto.getTradePrice();
            amount = buyRequestDto.getBuyAmount();
            Coin saveCoin = new Coin(user, buyRequestDto.getTiker(),buyPrice,amount);

            coinRepository.save(saveCoin);
        }

        // 구매할때 사용한 포인트 차감 저장
        user.update(accountBalance - buyRequestDto.getBuyAmount());
        userRepository.save(user);

        TransactionDto transactionDto = new TransactionDto(user,"buy",buyRequestDto.getBuyAmount(), buyRequestDto.getTiker());
        Transaction transaction = new Transaction(transactionDto);

        // 거래 내역 저장
        Transaction saveTran = transactionRepository.save(transaction);

        String tradeAt = saveTran.getTradeAt().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss"));

        BuyResponseDto buyResponseDto = new BuyResponseDto(user.getAccountBalance(),buyPrice,amount,"buy",tradeAt);
        
        return new ResponseEntity<>(buyResponseDto, HttpStatus.OK);
    }

    // 매도
    @Transactional
    public ResponseEntity<?> sell(SellRequestDto sellRequestDto, User user) {
        Coin coin = coinRepository.findByTikerAndUser_UserId(sellRequestDto.getTiker(), user.getUserId()).orElse(null);

        Long accountBalance = user.getAccountBalance();

        if(coin == null){
            return  new ResponseEntity<>("보유한 코인이 아닙니다",HttpStatus.BAD_REQUEST);
        }
        
        // 보유한 코인량 변경
        BigDecimal coinBalanceCal = BigDecimal.valueOf(coin.getBuyPrice() * sellRequestDto.getSellAmount());
        int coinBalance = coin.getAmount() - coinBalanceCal.divide(new BigDecimal(sellRequestDto.getTradePrice()), RoundingMode.CEILING).intValue();

        if(coinBalance < 0){
            return  new ResponseEntity<>("코인 보유량을 확인해주세요",HttpStatus.BAD_REQUEST);
        }
        else if(coinBalance == 0){
            coinRepository.deleteById(coin.getCoinId());
        }
        else{
            coin.update(coinBalance);
        }
        
        // 매도한 만큼 포인트 증가
        user.update(accountBalance + sellRequestDto.getSellAmount());
        userRepository.save(user);

        // 매도 거래내역 추가
        TransactionDto transactionDto = new TransactionDto(user,"sell",sellRequestDto.getSellAmount(),sellRequestDto.getTiker());
        Transaction transaction = new Transaction(transactionDto);
        Transaction saveTran = transactionRepository.save(transaction);

        String tradeAt = saveTran.getTradeAt().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss"));

        SellResponseDto sellResponseDto = new SellResponseDto(user.getAccountBalance(),sellRequestDto.getTradePrice(),sellRequestDto.getSellAmount(),"sell",tradeAt);

        return new ResponseEntity<>(sellResponseDto,HttpStatus.OK);
    }

    // 상대방 최근 거래내역 보기
    public ResponseEntity<?> getUserTranstnal(Long userId) {

        List<Transaction> transactionList = transactionRepository.findTop5ByUser_UserIdOrderByTradeAtDesc(userId);


        List<UserTransactionDto> tranDtos = new ArrayList<>();

        for(Transaction transaction : transactionList){
            String tiker = transaction.getTiker();
            String type = transaction.getType();
            int price = transaction.getPrice();
            String tradeAt = transaction.getTradeAt().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss"));

            tranDtos.add(new UserTransactionDto(tiker,type,price,tradeAt));
        }

        return new ResponseEntity<>(new UserTranResponseDto(tranDtos),HttpStatus.OK);
    }

    // 코린이 회원 중 특정 코인 매수 카운트
    public ResponseEntity<?> getBuyCount(String tiker) {

        // 초기화 시점인 월요일 9시 기준
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String mondayDate = dateFormat.format(cal.getTime());
        mondayDate += " 09:00:00.000";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        LocalDateTime startDate = LocalDateTime.parse(mondayDate, formatter);
        LocalDateTime endDate = LocalDateTime.now();

        //코린이 회원 중 특정 코인 매수 카운트
        Long buyCount = transactionRepository.countByTikerAndTypeAndTradeAtBetween(tiker,"buy",startDate,endDate);

        return new ResponseEntity<>(new BuyCountDto(buyCount),HttpStatus.OK);
    }
}






















