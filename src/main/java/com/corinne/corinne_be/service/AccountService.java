package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.account_dto.AccountResponseDto;
import com.corinne.corinne_be.dto.account_dto.AccountSimpleDto;
import com.corinne.corinne_be.dto.account_dto.CoinsDto;
import com.corinne.corinne_be.dto.coin_dto.CoinBalanceDto;
import com.corinne.corinne_be.dto.socket_dto.ChatMessage;
import com.corinne.corinne_be.dto.transaction_dto.TransactionDto;
import com.corinne.corinne_be.exception.CustomException;
import com.corinne.corinne_be.exception.ErrorCode;
import com.corinne.corinne_be.model.*;
import com.corinne.corinne_be.repository.*;
import com.corinne.corinne_be.utils.AlarmUtil;
import com.corinne.corinne_be.utils.BalanceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

@Service
public class AccountService {

    private final CoinRepository coinRepository;
    private final UserRepository userRepository;
    private final RedisRepository redisRepository;
    private final TransactionRepository transactionRepository;
    private final BookmarkRepository bookmarkRepository;
    private final QuestRepository questRepository;
    private final BalanceUtil balanceUtil;
    private final AlarmUtil alarmUtil;

    List<String> tikers = Arrays.asList("KRW-BTC", "KRW-SOL", "KRW-ETH", "KRW-XRP", "KRW-ADA", "KRW-AVAX", "KRW-DOT", "KRW-MATIC");

    @Autowired
    public AccountService(CoinRepository coinRepository, UserRepository userRepository,
                          RedisRepository redisRepository, TransactionRepository transactionRepository,
                          BookmarkRepository bookmarkRepository, QuestRepository questRepository, BalanceUtil balanceUtil,
                          AlarmUtil alarmUtil) {
        this.coinRepository = coinRepository;
        this.userRepository = userRepository;
        this.redisRepository = redisRepository;
        this.transactionRepository = transactionRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.questRepository = questRepository;
        this.balanceUtil = balanceUtil;
        this.alarmUtil = alarmUtil;
    }


    // ?????? ??????
    @Transactional
    public ResponseEntity<AccountResponseDto> balance(User user) {

        // ?????? ????????? ?????????
        Long accountBalance = user.getAccountBalance();

        // ???????????? ?????? ?????????
        List<Coin> haveCoins = coinRepository.findAllByUser_UserId(user.getUserId());

        CoinBalanceDto coinBalanceDto = balanceUtil.totalCoinBalance(haveCoins);

        // ???????????? ?????? ?????? ?????????
        List<CoinsDto> coins = coinBalanceDto.getCoinsDtoList();

        Long totalBalance = coinBalanceDto.getTotalcoinBalance() + accountBalance;

        // ????????? ??????
        BigDecimal temp = new BigDecimal(totalBalance - 1000000);
        BigDecimal rateCal = new BigDecimal(10000);
        double fluctuationRate = temp.divide(rateCal, 2, RoundingMode.HALF_EVEN).doubleValue();

        for (int i = 0; i < coins.size(); i++) {
            long balance = coins.get(i).getCoinBalance();
            // ???????????? ?????? ??????
            BigDecimal importanceRateCal = new BigDecimal(balance * 100);
            double importanceRate = importanceRateCal.divide(new BigDecimal(coinBalanceDto.getTotalcoinBalance()), 2, RoundingMode.HALF_EVEN).doubleValue();
            coins.get(i).setImportanceRate(importanceRate);
        }

        return new ResponseEntity<>(new AccountResponseDto(user.getLastFluctuation(), accountBalance, totalBalance, fluctuationRate, coins), HttpStatus.OK);
    }


    // ????????????????????? ??????
    @Transactional
    public ResponseEntity<AccountSimpleDto> simpleBalance(String tiker, User user) {

        if(!tikers.contains(tiker)){
            throw new CustomException(ErrorCode.NON_EXIST_TIKER);
        }

        List<Coin> coin = coinRepository.findByTikerAndUser_UserId(tiker, user.getUserId());

        Long accountBalance = user.getAccountBalance();
        return new ResponseEntity<>(new AccountSimpleDto(accountBalance, coin), HttpStatus.OK);
    }

    // ?????? ?????? ??????
    @Transactional
    public ResponseEntity<HttpStatus> resetAccount(User user) {

        user.balanceUpdate(1000000L);
        userRepository.save(user);

        // ?????? ?????? ?????????
        coinRepository.deleteAllByUser_UserId(user.getUserId());

        // ?????? ?????? ??????
        TransactionDto transactionDto = new TransactionDto(user, "reset", 0, 1000000L, "reset", 1, 0L);
        Transaction transaction = new Transaction(transactionDto);
        transactionRepository.save(transaction);
        redisRepository.resetBankruptcy(user.getUserId());

        Quest quest = questRepository.findByUser_UserIdAndQuestNo(user.getUserId(), 9).orElse(null);

        if (quest != null) {
            if (!quest.isClear()) {
                quest.update(true);
                alarmUtil.sendAlarm(user.getUserId().toString());
            }
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // ???????????? ??????
    @Transactional
    public ResponseEntity<HttpStatus> inputBookmark(String tiker, User user) {

        if(!tikers.contains(tiker)){
            throw new CustomException(ErrorCode.NON_EXIST_TIKER);
        }

        if (bookmarkRepository.existsByUserIdAndTiker(user.getUserId(), tiker)) {
            throw new CustomException(ErrorCode.EXIST_BOOKMARK);
        }

        Quest quest = questRepository.findByUser_UserIdAndQuestNo(user.getUserId(), 1).orElse(null);

        if (quest != null) {
            if (!quest.isClear()) {
                quest.update(true);
                alarmUtil.sendAlarm(user.getUserId().toString());
            }
        }

        // ???????????? ??????
        Bookmark bookmark = new Bookmark(user.getUserId(), tiker);
        bookmarkRepository.save(bookmark);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // ???????????? ??????
    @Transactional
    public ResponseEntity<HttpStatus> deleteBookmark(String tiker, User user) {

        if(!tikers.contains(tiker)){
            throw new CustomException(ErrorCode.NON_EXIST_TIKER);
        }

        Bookmark bookmark = bookmarkRepository.findByUserIdAndTiker(user.getUserId(), tiker).orElse(null);

        if (bookmark == null) {
            throw new CustomException(ErrorCode.NON_EXIST_BOOKMARK);
        }

        bookmarkRepository.delete(bookmark);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}


















