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
import java.time.format.DateTimeFormatter;

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
        Page<TransactionResponseDto> transactionResponseDtos = entities.map(transaction -> {

            String coin = transaction.getTiker();
            String type = transaction.getType();
            int price = transaction.getPrice();
            String tradeAt = transaction.getTradeAt().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss")
            );
            return  new TransactionResponseDto(coin,type,price,tradeAt);
        });

        return new ResponseEntity<>(transactionResponseDtos, HttpStatus.OK);
    }

    // 해당 코인 거래내역
    public ResponseEntity<?> getSpecifiedTranstnal(int page, int size, String sortBy, String coinName, User user) {

        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Transaction> entities = transactionRepository.findAllByCoinNameAndUser_UserId(coinName,user.getUserId(),pageable);
        Page<TransactionResponseDto> transactionResponseDtos = entities.map(transaction -> {
                String coin = transaction.getTiker();
                String type = transaction.getType();
                int price = transaction.getPrice();
                String tradeAt = transaction.getTradeAt().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss")
                );
                return  new TransactionResponseDto(coin,type,price,tradeAt);
        });

        return new ResponseEntity<>(transactionResponseDtos, HttpStatus.OK);
    }

    // 매수
    @Transactional
    public ResponseEntity<?> buy(BuyRequestDto buyRequestDto, User user) {

        Coin coin = coinRepository.findByCoinNameAndUser_UserId(buyRequestDto.getTiker(), user.getUserId()).orElse(null);

        Long accountBalance = user.getAccountBalance();

        if(coin != null){
            // 보유한 코인량 변경
            BigDecimal coinBalanceCal = new BigDecimal(coin.getAmount() * buyRequestDto.getTradePrice());
            int coinBalance = coinBalanceCal.divide(BigDecimal.valueOf(coin.getBuyPrice()), RoundingMode.HALF_EVEN).intValue() + buyRequestDto.getBuyAmount();
            coin.update(buyRequestDto.getTradePrice(), coinBalance);
        } else {
            Coin saveCoin = new Coin(user, buyRequestDto.getTiker(),buyRequestDto.getTradePrice(),buyRequestDto.getBuyAmount());
            coinRepository.save(saveCoin);
        }

        user.update(accountBalance - buyRequestDto.getBuyAmount());
        userRepository.save(user);

        TransactionDto transactionDto = new TransactionDto(user,"buy",buyRequestDto.getBuyAmount(), buyRequestDto.getTiker());
        Transaction transaction = new Transaction(transactionDto);

        Transaction saveTran = transactionRepository.save(transaction);

        String tradeAt = saveTran.getTradeAt().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss"));

        BuyResponseDto buyResponseDto = new BuyResponseDto(user.getAccountBalance(),buyRequestDto.getTradePrice(),buyRequestDto.getBuyAmount(),"buy",tradeAt);


        return new ResponseEntity<>(buyResponseDto, HttpStatus.OK);
    }

    // 매도
    @Transactional
    public ResponseEntity<?> sell(SellRequestDto sellRequestDto, User user) {
        Coin coin = coinRepository.findByCoinNameAndUser_UserId(sellRequestDto.getTiker(), user.getUserId()).orElse(null);

        Long accountBalance = user.getAccountBalance();

        if(coin == null){
            return  new ResponseEntity<>("보유한 코인이 아닙니다",HttpStatus.BAD_REQUEST);
        }
        
        // 보유한 코인량 변경
        BigDecimal coinBalanceCal = BigDecimal.valueOf(coin.getBuyPrice() * sellRequestDto.getSellAmount());
        int coinBalance = coin.getAmount() - coinBalanceCal.divide(new BigDecimal(sellRequestDto.getTradePrice()), RoundingMode.HALF_EVEN).intValue();

        if(coinBalance < 0){
            return  new ResponseEntity<>("코인 보유량을 확인해주세요",HttpStatus.BAD_REQUEST);
        }
        else if(coinBalance == 0){
            coinRepository.deleteById(coin.getCoinId());
        }
        else{
            coin.update(coinBalance);
            user.update(accountBalance + sellRequestDto.getSellAmount());
            userRepository.save(user);
        }

        user.update(accountBalance + sellRequestDto.getSellAmount());
        TransactionDto transactionDto = new TransactionDto(user,"sell",sellRequestDto.getSellAmount(),sellRequestDto.getTiker());
        Transaction transaction = new Transaction(transactionDto);
        Transaction saveTran = transactionRepository.save(transaction);

        String tradeAt = saveTran.getTradeAt().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss"));

        SellResponseDto sellResponseDto = new SellResponseDto(user.getAccountBalance(),sellRequestDto.getTradePrice(),sellRequestDto.getSellAmount(),"sell",tradeAt);

        return new ResponseEntity<>(sellResponseDto,HttpStatus.OK);
    }
}






















