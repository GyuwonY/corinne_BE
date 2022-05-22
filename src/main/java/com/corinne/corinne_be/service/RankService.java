package com.corinne.corinne_be.service;

import com.corinne.corinne_be.dto.rank_dto.*;
import com.corinne.corinne_be.exception.CustomException;
import com.corinne.corinne_be.exception.ErrorCode;
import com.corinne.corinne_be.model.Coin;
import com.corinne.corinne_be.model.User;
import com.corinne.corinne_be.repository.*;
import com.corinne.corinne_be.utils.RankUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankService {

    private final CoinRepository coinRepository;
    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final TransactionRepository transactionRepository;
    private final RankUtil rankUtil;

    @Autowired
    public RankService(CoinRepository coinRepository, UserRepository userRepository, FollowerRepository followerRepository, TransactionRepository transactionRepository, RankUtil rankUtil) {
        this.coinRepository = coinRepository;
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
        this.transactionRepository = transactionRepository;
        this.rankUtil = rankUtil;
    }


    // 랭킹 리스트
    @Transactional
    public ResponseEntity<MyRankResponseDto> getRank(int page, User loginUser) {
        if(page <= 0) {
            throw new CustomException(ErrorCode.WRONG_VALUE_PAGE);
        }
        // 페이징 사이즈
        int size = 20;

        List<User> Users = userRepository.findAll();

        List<RankDto> rankDtos = new ArrayList<>();

        for(User user : Users){

            Long totalBalance = 0L;

            Long accountBalance = user.getAccountBalance();

            List<Coin> coins = coinRepository.findAllByUser_UserId(user.getUserId());

            // 보유 코인별 계산
            totalBalance = rankUtil.getTotalCoinBalance(coins) +  accountBalance;

            BigDecimal temp = new BigDecimal(totalBalance - 1000000);
            BigDecimal rateCal = new BigDecimal(10000);
            double fluctuationRate = temp.divide(rateCal,2,RoundingMode.HALF_EVEN).doubleValue();

            boolean follow = followerRepository.existsByUser_UserIdAndFollower_UserId(loginUser.getUserId(),user.getUserId());

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

            int restCount = transactionRepository.countByUser_UserIdAndTypeAndTradeAtBetween(user.getUserId(),"reset",startDate, endDate).intValue();

            int exp = user.getExp();

            RankDto rankListDto = new RankDto(user.getUserId(), user.getNickname(),user.getImageUrl(),totalBalance,fluctuationRate, follow,restCount,exp);

            rankDtos.add(rankListDto);
        }

        rankDtos = rankDtos.stream().sorted(Comparator.comparing(RankDto::getTotalBalance).reversed()).collect(Collectors.toList());

        int i = 1;
        for(RankDto rankDto : rankDtos){
            rankDto.setRank(i);
            i++;
        }

        int fromIndex = (page - 1) * size;

        int totalPage = rankDtos.size()/size + 1;
        if(rankDtos.size()%size == 0){
            totalPage -= 1;
        }
        if(rankDtos.size() <= fromIndex){
            throw new CustomException(ErrorCode.WRONG_VALUE_PAGE);
        }

        MyRankResponseDto myRankResponseDto = new MyRankResponseDto(rankDtos.subList(fromIndex,Math.min(fromIndex + size, rankDtos.size())), totalPage);

        return new ResponseEntity<>(myRankResponseDto, HttpStatus.OK);
    }


    // 상위 랭킹 리스트
    @Transactional
    public ResponseEntity<RankTopDto> getRankTop3() {

        List<User> users = userRepository.findTop3ByOrderByLastFluctuationDesc();
        List<RankInfoDto> rankDtos = new ArrayList<>();
        for(User user : users){
            rankDtos.add(new RankInfoDto(user));
        }
        return new ResponseEntity<>(new RankTopDto(rankDtos), HttpStatus.OK);
    }

    // 내 랭킹
    @Transactional
    public ResponseEntity<MyRankDto> getMyRank(User loginUser) {

        return new ResponseEntity<>(rankUtil.getMyRank(loginUser.getUserId()), HttpStatus.OK);
    }

    // 지난주 랭킹 리스트
    @Transactional
    public ResponseEntity<LaskweekRankDto> getLastweekRank(int page, User loginUser) {
        if(page <= 0) {
            throw new CustomException(ErrorCode.WRONG_VALUE_PAGE);
        }

        // 페이징 사이즈
        int size = 20;

        List<User> Users = userRepository.findAllByOrderByLastFluctuationDesc();
        List<RankDto> rankDtos = new ArrayList<>();
        for(User user : Users){

            BigDecimal lastFluctuation = BigDecimal.valueOf(user.getLastFluctuation());

            Long totalBalance = lastFluctuation.add(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(10000)).longValue();

            double fluctuationRate = user.getLastFluctuation();

            boolean follow = followerRepository.existsByUser_UserIdAndFollower_UserId(loginUser.getUserId(),user.getUserId());


            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -7);
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

            int restCount = transactionRepository.countByUser_UserIdAndTypeAndTradeAtBetween(user.getUserId(),"reset", startDate,endDate ).intValue();

            int exp = user.getExp();

            RankDto rankListDto = new RankDto(user.getUserId(), user.getNickname(),user.getImageUrl(),totalBalance,fluctuationRate, follow,restCount,exp);

            rankDtos.add(rankListDto);
        }


        int fromIndex = (page - 1) * size;

        int totalPage = rankDtos.size()/size + 1;
        if(rankDtos.size()%size == 0){
            totalPage -= 1;
        }
        if(rankDtos.size() <= fromIndex){
            throw new CustomException(ErrorCode.WRONG_VALUE_PAGE);
        }

        LaskweekRankDto laskweekRankDto = new LaskweekRankDto(rankDtos.subList(fromIndex,Math.min(fromIndex + size, rankDtos.size())), totalPage);

        return new ResponseEntity<>(laskweekRankDto, HttpStatus.OK);
    }


}

























